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
import com.vaushell.spipes.tools.scribe.code.A_ValidatorCode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
    public static final int TWEET_SIZE = 140;
    public static final int TWEET_IMAGE_SIZE = 112;

    public TwitterClient()
    {
        super();

        this.fmt = DateTimeFormat.forPattern( "EEE MMM dd HH:mm:ss Z yyyy" );
    }

    /**
     * Log in.
     *
     * @param key OAuth key
     * @param secret OAuth secret
     * @param tokenPath Path to save the token
     * @param vCode How to get the verification code
     * @throws IOException
     * @throws java.lang.InterruptedException
     */
    public void login( final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final A_ValidatorCode vCode )
        throws IOException , InterruptedException
    {
        loginImpl( TwitterApi.SSL.class ,
                   key ,
                   secret ,
                   null ,
                   null ,
                   true ,
                   tokenPath ,
                   vCode );
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
     * Tweet picture.
     *
     * @param message Tweet's content
     * @param picturePath Path of the picture
     * @return Tweet's ID
     * @throws IOException
     * @throws OAuthException
     */
    public long tweetPicture( final String message ,
                              final Path picturePath )
        throws IOException , OAuthException
    {
        if ( picturePath == null || Files.notExists( picturePath ) )
        {
            throw new IllegalArgumentException();
        }

        // Don't use a try-with-resources
        // Because of a findbug bug on : RCN_REDUNDANT_NULLCHECK_OF_NULL_VALUE
        // double check in the finally
        InputStream is = null;
        try
        {
            is = Files.newInputStream( picturePath );

            return tweetPicture( message ,
                                 is );
        }
        finally
        {
            if ( null != is )
            {
                is.close();
            }
        }
    }

    /**
     * Tweet picture.
     *
     * @param message Tweet's content
     * @param is InputStream of the picture
     * @return Tweet's ID
     * @throws IOException
     * @throws OAuthException
     */
    public long tweetPicture( final String message ,
                              final InputStream is )
        throws IOException , OAuthException
    {
        if ( message == null || is == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] tweetPicture() : message=" + message );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "https://api.twitter.com/1.1/statuses/update_with_media.json" );
        final HttpEntity entity = MultipartEntityBuilder
            .create()
            .addBinaryBody( "status" ,
                            message.getBytes( "UTF-8" ) )
            .addBinaryBody( "media[]" ,
                            is ,
                            ContentType.APPLICATION_OCTET_STREAM ,
                            "media" )
            .build();

        final Header contentType = entity.getContentType();
        request.addHeader( contentType.getName() ,
                           contentType.getValue() );

        try( final ByteArrayOutputStream bos = new ByteArrayOutputStream() )
        {
            entity.writeTo( bos );
            request.addPayload( bos.toByteArray() );
        }

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
     */
    public TW_Tweet readTweet( final long ID )
        throws IOException , OAuthException
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

        return convertJsonToTweet( node );
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

    /**
     * Read a Twitter timeline.
     *
     * @param forcedTarget Target's ID. Could be null to use login target.
     * @param count Max tweet. Could be null to use default.
     * @return a list of tweets
     * @throws IOException
     * @throws OAuthException
     */
    public List<TW_Tweet> readTimeline( final Long forcedTarget ,
                                        final Integer count )
        throws IOException , OAuthException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readTimeline() : forcedTarget=" + forcedTarget + " / count=" + count );
        }

        final String url;
        final Properties properties = new Properties();
        if ( forcedTarget == null )
        {
            url = "https://api.twitter.com/1.1/statuses/home_timeline.json";
        }
        else
        {
            url = "https://api.twitter.com/1.1/statuses/user_timeline.json";
            properties.setProperty( "user_id" ,
                                    Long.toString( forcedTarget ) );
        }
        if ( count != null )
        {
            properties.setProperty( "count" ,
                                    Integer.toString( count ) );
        }

        return readTimelineImpl( url ,
                                 properties );
    }

    /**
     * Iterate a Twitter timeline.
     *
     * @param forcedTarget Target's ID. Could be null to use login target.
     * @param count Max tweet by call. Could be null to use default.
     * @return a tweets iterator
     */
    public Iterator<TW_Tweet> iteratorTimeline( final Long forcedTarget ,
                                                final Integer count )
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] iteratorTimeline() : forcedTarget=" + forcedTarget + " / count=" + count );
        }

        return new Iterator<TW_Tweet>()
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

                        final String url;
                        final Properties properties = new Properties();
                        if ( forcedTarget == null )
                        {
                            url = "https://api.twitter.com/1.1/statuses/home_timeline.json";
                        }
                        else
                        {
                            url = "https://api.twitter.com/1.1/statuses/user_timeline.json";
                            properties.setProperty( "user_id" ,
                                                    Long.toString( forcedTarget ) );
                        }
                        if ( count != null )
                        {
                            properties.setProperty( "count" ,
                                                    Integer.toString( count ) );
                        }
                        if ( maxID != null )
                        {
                            properties.setProperty( "max_id" ,
                                                    Long.toString( maxID - 1L ) );
                        }

                        final List<TW_Tweet> tweets = readTimelineImpl( url ,
                                                                        properties );
                        if ( tweets.isEmpty() )
                        {
                            return false;
                        }
                        else
                        {
                            maxID = tweets.get( tweets.size() - 1 ).getID();

                            buffer.addAll( tweets );

                            return true;
                        }
                    }
                }
                catch( final OAuthException |
                             IOException ex )
                {
                    throw new RuntimeException( ex );
                }
            }

            @Override
            public TW_Tweet next()
            {
                return buffer.get( bufferCursor++ );
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            // PRIVATE
            private final List<TW_Tweet> buffer = new ArrayList<>();
            private int bufferCursor;
            private Long maxID;
        };
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( TwitterClient.class );
    private final DateTimeFormatter fmt;

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

    private List<TW_Tweet> readTimelineImpl( final String url ,
                                             final Properties properties )
        throws IOException , OAuthException
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
        final JsonNode nodes = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     nodes );

        final List<TW_Tweet> tweets = new ArrayList<>();
        for ( final JsonNode node : nodes )
        {
            tweets.add( convertJsonToTweet( node ) );
        }

        return tweets;
    }

    private TW_Tweet convertJsonToTweet( final JsonNode node )
    {
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
                             fmt.parseDateTime( node.get( "created_at" ).asText() )
        );
    }
}
