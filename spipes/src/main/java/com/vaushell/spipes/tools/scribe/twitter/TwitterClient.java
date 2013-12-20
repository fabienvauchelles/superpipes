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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.vaushell.spipes.tools.scribe.OAuthClient;
import com.vaushell.spipes.tools.scribe.OAuthException;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
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

        this.df = new SimpleDateFormat( "EEE MMM dd HH:mm:ss Z yyyy" ,
                                        Locale.ENGLISH );
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

    /**
     * Read a tweet.
     *
     * @param ID Tweet ID
     * @return the tweet
     * @throws IOException
     * @throws com.vaushell.spipes.tools.scribe.OAuthException
     * @throws ParseException
     */
    public TW_Tweet readTweet( final long ID )
        throws IOException , OAuthException , ParseException
    {
        if ( ID < 0 )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readTweet() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       "https://api.twitter.com/1.1/statuses/show.json?id=" + Long.toString( ID ) );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        // Replace shorten URLs with expanded URLs
        String text = node.get( "text" ).asText();

        final JsonNode nodeEntities = node.get( "entities" );
        if ( nodeEntities != null )
        {
            final JsonNode nodeUrls = nodeEntities.get( "urls" );
            if ( nodeUrls != null )
            {
                for ( final JsonNode nodeUrl : nodeUrls )
                {
                    text = text.replace( nodeUrl.get( "url" ).asText() ,
                                         nodeUrl.get( "expanded_url" ).asText() );
                }
            }
        }

        final JsonNode nodeUser = node.get( "user" );

        return new TW_Tweet( node.get( "id" ).asLong() ,
                             text ,
                             new TW_User( nodeUser.get( "id" ).asLong() ,
                                          nodeUser.get( "name" ).asText() ,
                                          nodeUser.get( "screen_name" ).asText() ) ,
                             df.parse( node.get( "created_at" ).asText() ).getTime()
        );
    }

    /**
     * Delete a tweet.
     *
     * @param ID Tweet ID
     * @return True if successfull
     * @throws IOException
     * @throws com.vaushell.spipes.tools.scribe.OAuthException
     */
    public boolean deleteTweet( final long ID )
        throws IOException , OAuthException
    {
        if ( ID < 0 )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] deleteTweet() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://api.twitter.com/1.1/statuses/destroy/" + ID + ".json" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        final JsonNode nodeID = node.get( "id" );
        if ( nodeID == null )
        {
            return false;
        }
        else
        {
            return ID == nodeID.asLong();
        }
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( TwitterClient.class );
    private final SimpleDateFormat df;

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
