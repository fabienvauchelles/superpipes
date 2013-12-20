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

package com.vaushell.spipes.tools.scribe.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.spipes.tools.scribe.OAuthClient;
import com.vaushell.spipes.tools.scribe.OAuthException;
import java.io.IOException;
import java.nio.file.Path;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Twitter client.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TwitterClient
    extends OAuthClient
{
    // PUBLIC
    public TwitterClient()
    {
        super();
    }

    /**
     * Log in.
     *
     * @param key OAuth key
     * @param secret OAuth secret
     * @param tokenPath Path to save the token
     * @param vCodeMethod How to get the verification code
     * @param loginText Prefix message to request the token to the user
     * @throws IOException
     * @throws java.lang.InterruptedException
     */
    public void login( final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final VCodeMethod vCodeMethod ,
                       final String loginText )
        throws IOException , InterruptedException
    {
        loginImpl( TwitterApi.class ,
                   key ,
                   secret ,
                   null ,
                   null ,
                   true ,
                   tokenPath ,
                   vCodeMethod ,
                   loginText );
    }

    /**
     * Tweet message.
     *
     * @param message Tweet's content
     * @return Tweet's ID
     * @throws IOException
     * @throws OAuthException
     */
    public long tweet( final String message )
        throws IOException , OAuthException
    {
        if ( message == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] tweet() : message=" + message );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://api.twitter.com/1.1/statuses/update.json" );
        request.addBodyParameter( "status" ,
                                  message );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "id" ).asLong();
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( TwitterClient.class );

    private void checkErrors( final Response response ,
                              final JsonNode root )
        throws OAuthException
    {
        final JsonNode error = root.get( "errors" );
        if ( error != null )
        {
            final JsonNode first = error.get( 0 );

            throw new OAuthException( response.getCode() ,
                                      first.get( "code" ).asInt() ,
                                      first.get( "message" ).asText() );
        }
    }
}
