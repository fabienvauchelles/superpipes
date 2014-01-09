/*
 * Copyright (C) 2013 Fabien Vauchelles (fabien_AT_vauchelles_DOT_com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3, 29 June 2007, of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package com.vaushell.spipes.tools.scribe.fb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.spipes.tools.scribe.OAuthClient;
import com.vaushell.spipes.tools.scribe.code.A_ValidatorCode;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facebook client.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class FacebookClient
    extends OAuthClient
{
    // PUBLIC
    public FacebookClient()
    {
        super();

        this.df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" ,
                                        Locale.ENGLISH );

        this.target = null;
    }

    /**
     * Log in.
     *
     * @param key Facebook key
     * @param secret Facebook secret
     * @param tokenPath Path to save the token
     * @param vCode How to get the verification code
     * @throws IOException
     * @throws InterruptedException
     */
    public void login( final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final A_ValidatorCode vCode )
        throws IOException , InterruptedException
    {
        loginImpl( FacebookApi.class ,
                   key ,
                   secret ,
                   "publish_stream" ,
                   "http://www.facebook.com/connect/login_success.html" ,
                   false ,
                   tokenPath ,
                   vCode );

        target = "me";
    }

    /**
     * Log in.
     *
     * @param pageName Page name (not the ID)
     * @param key Facebook key
     * @param secret Facebook secret
     * @param tokenPath Path to save the token
     * @param vCode How to get the verification code
     * @throws IOException
     * @throws InterruptedException
     * @throws FacebookException
     */
    public void login( final String pageName ,
                       final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final A_ValidatorCode vCode )
        throws IOException , InterruptedException , FacebookException
    {
        loginImpl( FacebookApi.class ,
                   key ,
                   secret ,
                   "publish_stream,manage_pages" ,
                   "http://www.facebook.com/connect/login_success.html" ,
                   false ,
                   tokenPath ,
                   vCode );

        updateTarget( pageName );
    }

    /**
     * Post a message to Facebook.
     *
     * @param message Message's content
     * @return Post ID
     * @throws FacebookException
     * @throws IOException
     */
    public String postMessage( final String message )
        throws FacebookException , IOException
    {
        if ( message == null || message.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] postMessage() : message=" + message );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://graph.facebook.com/" + target + "/feed" );

        request.addBodyParameter( "message" ,
                                  message );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "id" ).asText();
    }

    /**
     * Post a link to Facebook.
     *
     * @param message Message's content
     * @param uri Message's link
     * @param uriName Link's name
     * @param uriCaption Link's caption
     * @param uriDescription Link's description
     * @return Post ID
     * @throws FacebookException
     * @throws IOException
     */
    public String postLink( final String message ,
                            final String uri ,
                            final String uriName ,
                            final String uriCaption ,
                            final String uriDescription )
        throws FacebookException , IOException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] postLink() : message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriCaption=" + uriCaption + " / uriDescription=" + uriDescription );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://graph.facebook.com/" + target + "/feed" );

        if ( message != null && message.length() > 0 )
        {
            request.addBodyParameter( "message" ,
                                      message );
        }

        request.addBodyParameter( "link" ,
                                  uri );

        if ( uriName != null && uriName.length() > 0 )
        {
            request.addBodyParameter( "name" ,
                                      uriName );
        }

        if ( uriCaption != null && uriCaption.length() > 0 )
        {
            request.addBodyParameter( "caption" ,
                                      uriCaption );
        }

        if ( uriDescription != null && uriDescription.length() > 0 )
        {
            request.addBodyParameter( "description" ,
                                      uriDescription );
        }

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "id" ).asText();
    }

    /**
     * Read a Facebook Post.
     *
     * @param ID Post ID
     * @return the Post
     * @throws IOException
     * @throws FacebookException
     * @throws ParseException
     */
    public FB_Post readPost( final String ID )
        throws IOException , FacebookException , ParseException
    {
        if ( ID == null || ID.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readPost() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       "https://graph.facebook.com/" + ID );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return convertJsonToPost( node );
    }

    /**
     * Read a Facebook Feed.
     *
     * @param userID User ID. 'me' for authenficated user.
     * @return the list of Post
     * @throws IOException
     * @throws FacebookException
     * @throws ParseException
     */
    public List<FB_Post> readFeed( final String userID )
        throws IOException , FacebookException , ParseException
    {
        if ( userID == null || userID.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readFeed() : userID=" + userID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       "https://graph.facebook.com/" + userID + "/feed" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        final List<FB_Post> posts = new ArrayList<>();

        final JsonNode nDatas = node.get( "data" );
        if ( nDatas != null )
        {
            for ( final JsonNode nData : nDatas )
            {
                posts.add( convertJsonToPost( nData ) );
            }
        }

        return posts;
    }

    /**
     * Delete a Facebook Post.
     *
     * @param ID Post ID
     * @return True if successfull
     * @throws IOException
     * @throws FacebookException
     */
    public boolean deletePost( final String ID )
        throws IOException , FacebookException
    {
        if ( ID == null || ID.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] deletePost() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.DELETE ,
                                                       "https://graph.facebook.com/" + ID );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.asBoolean();
    }

    /**
     * Like a Facebook Post.
     *
     * @param ID Post ID
     * @return True if successfull
     * @throws IOException
     * @throws FacebookException
     */
    public boolean likePost( final String ID )
        throws IOException , FacebookException
    {
        if ( ID == null || ID.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] likePost() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://graph.facebook.com/" + ID + "/likes" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.asBoolean();
    }

    /**
     * Unlike a Facebook Post.
     *
     * @param ID Post ID
     * @return True if successfull
     * @throws IOException
     * @throws FacebookException
     */
    public boolean unlikePost( final String ID )
        throws IOException , FacebookException
    {
        if ( ID == null || ID.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] unlikePost() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.DELETE ,
                                                       "https://graph.facebook.com/" + ID + "/likes" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.asBoolean();
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( FacebookClient.class );
    private final SimpleDateFormat df;
    private String target;

    private void checkErrors( final Response response ,
                              final JsonNode root )
        throws FacebookException
    {
        final JsonNode error = root.get( "error" );
        if ( error != null )
        {
            throw new FacebookException( response.getCode() ,
                                         error.get( "code" ).asInt() ,
                                         error.get( "type" ).asText() ,
                                         error.get( "message" ).asText() );
        }
    }

    private void updateTarget( final String pageName )
        throws IOException , FacebookException
    {
        if ( pageName == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] updateTarget() : pageName=" + pageName );
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       "https://graph.facebook.com/me/accounts" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        final JsonNode datas = node.get( "data" );
        if ( datas != null )
        {
            for ( final JsonNode data : datas )
            {
                final String name = data.get( "name" ).asText();
                if ( pageName.equalsIgnoreCase( name ) )
                {
                    target = data.get( "id" ).asText();

                    changeAccessToken( new Token( data.get( "access_token" ).asText() ,
                                                  "" ) );

                    return;
                }
            }
        }

        throw new IllegalArgumentException( "Page '" + pageName + "' is not accessible" );
    }

    private FB_Post convertJsonToPost( final JsonNode node )
        throws ParseException
    {
        final JsonNode nodeFrom = node.get( "from" );

        return new FB_Post( node.get( "id" ).asText() ,
                            convertNodeToString( node.get( "message" ) ) ,
                            convertNodeToString( node.get( "link" ) ) ,
                            convertNodeToString( node.get( "name" ) ) ,
                            convertNodeToString( node.get( "caption" ) ) ,
                            convertNodeToString( node.get( "description" ) ) ,
                            new FB_User( nodeFrom.get( "id" ).asText() ,
                                         nodeFrom.get( "name" ).asText() ) ,
                            df.parse( node.get( "created_time" ).asText() ).getTime() );
    }
}
