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

package com.vaushell.superpipes.tools.scribe.fb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.superpipes.tools.scribe.OAuthClient;
import com.vaushell.superpipes.tools.scribe.code.A_ValidatorCode;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

        this.fmtPSTread = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH:mm:ssZ" );

        // To manage backdating problem : http://stackoverflow.com/questions/20649571/backdating-posts-with-facebook-graph-api
        // We force our date, with a PST TimeZone (-0800)
        this.fmtPSTwrite = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH:mm:ss-0800" );

        this.target = null;
        this.page = false;
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
     * @throws com.vaushell.superpipes.tools.scribe.fb.FacebookException
     */
    public void login( final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final A_ValidatorCode vCode )
        throws IOException , InterruptedException , FacebookException
    {
        loginImpl( FacebookApi.class ,
                   key ,
                   secret ,
                   "read_stream,publish_actions" ,
                   "http://www.facebook.com/connect/login_success.html" ,
                   false ,
                   tokenPath ,
                   vCode );

        target = getMeID();
        page = false;
    }

    /**
     * Log in as a page.
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
    public void loginAsPage( final String pageName ,
                             final String key ,
                             final String secret ,
                             final Path tokenPath ,
                             final A_ValidatorCode vCode )
        throws IOException , InterruptedException , FacebookException
    {
        loginImpl( FacebookApi.class ,
                   key ,
                   secret ,
                   "read_stream,publish_actions,manage_pages" ,
                   "http://www.facebook.com/connect/login_success.html" ,
                   false ,
                   tokenPath ,
                   vCode );

        target = getPageIDandChangeToken( pageName );
        page = true;
    }

    /**
     * Post a message to Facebook.
     *
     * @param forcedTarget Target's ID (user or page). Could be null to use login target.
     * @param message Message's content
     * @param date Force message's date
     * @return Post ID
     * @throws FacebookException
     * @throws IOException
     */
    public String postMessage( final String forcedTarget ,
                               final String message ,
                               final DateTime date )
        throws FacebookException , IOException
    {
        if ( message == null || message.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] postMessage() : forcedTarget=" + forcedTarget + " / message=" + message + " / date=" + date );
        }

        final String realTarget = forcedTarget == null ? target : forcedTarget;

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://graph.facebook.com/" + realTarget + "/feed" );

        if ( forcedTarget == null )
        {
            if ( page )
            {
                if ( date != null )
                {
                    final DateTime now = new DateTime();

                    if ( date.isBefore( now ) )
                    {
                        request.addBodyParameter( "backdated_time" ,
                                                  fmtPSTwrite.print( date ) );
                    }
                    else if ( date.isAfter( now ) )
                    {
                        request.addBodyParameter( "scheduled_publish_time" ,
                                                  fmtPSTwrite.print( date ) );
                    }
                }
            }
            else
            {
                // Me only
                request.addBodyParameter( "privacy" ,
                                          "{'value':'EVERYONE'}" );
            }
        }

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
     * @param forcedTarget Target's ID (user or page). Could be null to use login target.
     * @param message Message's content
     * @param uri Message's link
     * @param uriName Link's name
     * @param uriCaption Link's caption
     * @param uriDescription Link's description
     * @param date Force message's date
     * @return Post ID
     * @throws FacebookException
     * @throws IOException
     */
    public String postLink( final String forcedTarget ,
                            final String message ,
                            final String uri ,
                            final String uriName ,
                            final String uriCaption ,
                            final String uriDescription ,
                            final DateTime date )
        throws FacebookException , IOException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] postLink() : forcedTarget=" + forcedTarget + " / message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriCaption=" + uriCaption + " / uriDescription=" + uriDescription + " / date=" + date );
        }

        final String realTarget = forcedTarget == null ? target : forcedTarget;

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://graph.facebook.com/" + realTarget + "/feed" );

        if ( forcedTarget == null )
        {
            if ( page )
            {
                if ( date != null )
                {
                    final DateTime now = new DateTime();

                    if ( date.isBefore( now ) )
                    {
                        request.addBodyParameter( "backdated_time" ,
                                                  fmtPSTwrite.print( date ) );
                    }
                    else if ( date.isAfter( now ) )
                    {
                        request.addBodyParameter( "scheduled_publish_time" ,
                                                  fmtPSTwrite.print( date ) );
                    }
                }
            }
            else
            {
                // Me only
                request.addBodyParameter( "privacy" ,
                                          "{'value':'EVERYONE'}" );
            }
        }

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
     */
    public FB_Post readPost( final String ID )
        throws IOException , FacebookException
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
     * @param forcedTarget Target's ID (user or page). Could be null to use login target.
     * @param limit Maximum number of results. Could be null to use default.
     * @return a list of posts
     * @throws IOException
     * @throws FacebookException
     */
    public List<FB_Post> readFeed( final String forcedTarget ,
                                   final Integer limit )
        throws IOException , FacebookException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readFeed() : forcedTarget=" + forcedTarget + " / limit=" + limit );
        }

        final Properties properties = new Properties();
        if ( limit != null )
        {
            properties.setProperty( "limit" ,
                                    Integer.toString( limit ) );
        }

        final String realTarget = forcedTarget == null ? target : forcedTarget;
        return filter( readFeedImpl(
            "https://graph.facebook.com/" + realTarget + "/feed" ,
            properties ) ,
                       realTarget );
    }

    /**
     * Iterate a Facebook Feed.
     *
     * @param forcedTarget Target's ID (user or page). Could be null to use login target.
     * @param limit Maximum number of results by call. Could be null to use default.
     * @return a posts iterator
     */
    public Iterator<FB_Post> iteratorFeed( final String forcedTarget ,
                                           final Integer limit )
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] iteratorFeed() : forcedTarget=" + forcedTarget + " / limit=" + limit );
        }

        return new Iterator<FB_Post>()
        {
            @Override
            public boolean hasNext()
            {
                try
                {
                    if ( bufferCursor < buffer.size() )
                    {
                        return true;
                    }
                    else
                    {
                        buffer.clear();
                        bufferCursor = 0;

                        List<FB_Post> linksFiltered;
                        do
                        {
                            final Properties properties = new Properties();
                            if ( limit != null )
                            {
                                properties.setProperty( "limit" ,
                                                        Integer.toString( limit ) );
                            }
                            if ( lastTimestamp != null )
                            {
                                properties.setProperty( "until" ,
                                                        Long.toString( lastTimestamp.getMillis() / 1000L - 1L ) );
                            }

                            final String realTarget = forcedTarget == null ? target : forcedTarget;
                            final List<FB_Post> links = readFeedImpl(
                                "https://graph.facebook.com/" + realTarget + "/feed" ,
                                properties );
                            if ( links.isEmpty() )
                            {
                                return false;
                            }

                            lastTimestamp = links.get( links.size() - 1 ).getCreatedTime();

                            linksFiltered = filter( links ,
                                                    realTarget );
                        }
                        while ( linksFiltered.isEmpty() );

                        buffer.addAll( linksFiltered );

                        return true;
                    }
                }
                catch( final FacebookException |
                             IOException ex )
                {
                    throw new RuntimeException( ex );
                }
            }

            @Override
            public FB_Post next()
            {
                return buffer.get( bufferCursor++ );
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            // PRIVATE
            private final List<FB_Post> buffer = new ArrayList<>();
            private int bufferCursor;
            private DateTime lastTimestamp;
        };
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
     * Delete all posts. Warning! Could crash because you haven't the rights.
     *
     * @throws IOException
     * @throws FacebookException
     */
    public void deleteAllPosts()
        throws IOException , FacebookException
    {
        List<FB_Post> posts = readFeed( null ,
                                        null );
        while ( !posts.isEmpty() )
        {
            for ( final FB_Post post : posts )
            {
                deletePost( post.getID() );
            }

            posts = readFeed( null ,
                              null );
        }
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
    private final DateTimeFormatter fmtPSTread;
    private final DateTimeFormatter fmtPSTwrite;
    private String target;
    private boolean page;

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

    private String getPageIDandChangeToken( final String pageName )
        throws IOException , FacebookException
    {
        if ( pageName == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] getPageIDandChangeToken() : pageName=" + pageName );
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
                    changeAccessToken( new Token( data.get( "access_token" ).asText() ,
                                                  "" ) );

                    return data.get( "id" ).asText();
                }
            }
        }

        throw new IllegalArgumentException( "Page '" + pageName + "' is not accessible" );
    }

    private String getMeID()
        throws IOException , FacebookException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] getMeID()" );
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       "https://graph.facebook.com/me" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "id" ).asText();
    }

    private List<FB_Post> readFeedImpl( final String url ,
                                        final Properties properties )
        throws IOException , FacebookException
    {
        if ( url == null || properties == null )
        {
            throw new IllegalArgumentException();
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       url );
        for ( final Entry<Object , Object> entry : properties.entrySet() )
        {
            request.addQuerystringParameter( (String) entry.getKey() ,
                                             (String) entry.getValue() );
        }

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

    private FB_Post convertJsonToPost( final JsonNode node )
    {
        final JsonNode nodeFrom = node.get( "from" );

        int count = 0;
        final JsonNode nodeActions = node.get( "actions" );
        if ( nodeActions != null && nodeActions.size() >= 2 )
        {
            for ( final JsonNode nodeAction : nodeActions )
            {
                final String name = nodeAction.get( "name" ).asText();
                if ( "Comment".equalsIgnoreCase( name ) || "Like".equalsIgnoreCase( name ) )
                {
                    ++count;
                }
            }
        }

        return new FB_Post( node.get( "id" ).asText() ,
                            convertNodeToString( node.get( "message" ) ) ,
                            convertNodeToString( node.get( "link" ) ) ,
                            convertNodeToString( node.get( "name" ) ) ,
                            convertNodeToString( node.get( "caption" ) ) ,
                            convertNodeToString( node.get( "description" ) ) ,
                            new FB_User( nodeFrom.get( "id" ).asText() ,
                                         nodeFrom.get( "name" ).asText() ) ,
                            fmtPSTread.parseDateTime( node.get( "created_time" ).asText() ) ,
                            count == 2 );
    }

    @SuppressWarnings( "PMD.UselessParentheses" )
    private List<FB_Post> filter( final List<FB_Post> posts ,
                                  final String fromID )
    {
        if ( posts == null || fromID == null )
        {
            throw new IllegalArgumentException();
        }

        final List<FB_Post> filtered = new ArrayList<>();

        for ( final FB_Post post : posts )
        {
            if ( post.getFrom() != null && fromID.equals( post.getFrom().getID() )
                 && post.isUsable()
                 && ( ( post.getMessage() != null && !post.getMessage().isEmpty() )
                      || ( post.getURL() != null && !post.getURL().isEmpty() ) ) )
            {
                filtered.add( post );
            }
        }

        return filtered;
    }
}
