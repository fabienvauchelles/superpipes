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
import java.io.IOException;
import java.nio.file.Path;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
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
    }

    /**
     * Log in.
     *
     * @param key Facebook key
     * @param secret Facebook secret
     * @param scope Facebook scope
     * @param tokenPath Path to save the token
     * @param loginText Prefix message to request the token to the user
     * @throws IOException
     */
    public void login( final String key ,
                       final String secret ,
                       final String scope ,
                       final Path tokenPath ,
                       final String loginText )
        throws IOException
    {
        loginImpl( FacebookApi.class ,
                   key ,
                   secret ,
                   scope ,
                   "http://www.facebook.com/connect/login_success.html" ,
                   false ,
                   tokenPath ,
                   loginText );
    }

    /**
     * Post a message to Facebook.
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
    public String post( final String message ,
                        final String uri ,
                        final String uriName ,
                        final String uriCaption ,
                        final String uriDescription )
        throws FacebookException , IOException
    {
        if ( ( uri == null || uri.length() <= 0 ) && ( message == null || message.length() <= 0 ) )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] post() : message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriCaption=" + uriCaption + " / uriDescription=" + uriDescription );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://graph.facebook.com/me/feed" );

        if ( message != null && message.length() > 0 )
        {
            request.addBodyParameter( "message" ,
                                      message );
        }

        if ( uri != null && uri.length() > 0 )
        {
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
        }

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "id" ).asText();
    }

    /**
     * Like a Facebook Post.
     *
     * @param postID Post ID
     * @return True if successfull
     * @throws IOException
     * @throws FacebookException
     */
    public boolean likePost( final String postID )
        throws IOException , FacebookException
    {
        if ( postID == null || postID.length() <= 0 )
        {
            throw new IllegalArgumentException();
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://graph.facebook.com/" + postID + "/likes" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.asBoolean();
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( FacebookClient.class );

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
}
