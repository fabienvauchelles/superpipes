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

package com.vaushell.superpipes.nodes.twitter;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.dispatch.Tags;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.tools.retry.A_Retry;
import com.vaushell.superpipes.tools.retry.RetryException;
import com.vaushell.superpipes.tools.scribe.OAuthException;
import com.vaushell.superpipes.tools.scribe.twitter.TwitterClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post a tweet to Twitter.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TW_Post
    extends A_Node
{
    // PUBLIC
    public N_TW_Post()
    {
        super( null ,
               SECURE_ANTIBURST );

        this.client = new TwitterClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = getDispatcher().getDatas().resolve( Paths.get( getNodeID() ,
                                                                              "token" ) );

        client.login( getProperties().getConfigString( "key" ) ,
                      getProperties().getConfigString( "secret" ) ,
                      tokenPath ,
                      getDispatcher().getVCodeFactory().create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
    }

    @Override
    protected void loop()
        throws Exception
    {
        // Receive
        setMessage( getLastMessageOrWait() );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + Message.formatSimple( getMessage() ) );
        }

        // Send to Twitter
        final long ID;

        if ( getMessage().contains( Message.KeyIndex.PICTURE ) )
        {
            final byte[] picture = (byte[]) getMessage().getProperty( Message.KeyIndex.PICTURE );

            ID = tweetPictureFailsafe( createContent( getMessage() ,
                                                      TwitterClient.TWEET_IMAGE_SIZE ) ,
                                       picture );
        }
        else
        {
            ID = tweet( createContent( getMessage() ,
                                       TwitterClient.TWEET_SIZE ) );
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        getMessage().setProperty( "id-twitter" ,
                                  ID );

        sendMessage();
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // DEFAULT

    /**
     * Convert a message to a tweet correctly sized.
     *
     * @param message the Message
     * @param size message maximum size in characters
     * @return the Tweet content
     */
    @SuppressWarnings( "unchecked" )
    static String createContent( final Message message ,
                                 final int size )
    {
        if ( message == null || !message.contains( Message.KeyIndex.URI ) )
        {
            throw new IllegalArgumentException();
        }

        final String uri = message.getProperty( Message.KeyIndex.URI ).toString();
        if ( uri.length() > size )
        {
            throw new IllegalArgumentException( "URL is too long" );
        }

        final StringBuilder sb = new StringBuilder();
        // 15 caracters minimum size for message
        if ( uri.length() > size - 15 )
        {
            sb.append( uri );
        }
        else
        {
            final int realsize;
            if ( uri.length() >= 22 )
            {
                realsize = size;
            }
            else
            {
                realsize = size - 22 + uri.length();
            }

            sb.append( " (" ).append( uri ).append( ')' );

            if ( message.contains( Message.KeyIndex.TITLE ) )
            {
                final String title = (String) message.getProperty( Message.KeyIndex.TITLE );

                if ( title.length() + sb.length() > realsize )
                {
                    sb.insert( 0 ,
                               title.substring( 0 ,
                                                realsize - sb.length() ) );
                }
                else
                {
                    sb.insert( 0 ,
                               title );
                }
            }

            if ( message.contains( Message.KeyIndex.TAGS ) )
            {
                final Tags tags = (Tags) message.getProperty( Message.KeyIndex.TAGS );
                for ( final String tag : tags.getAll() )
                {
                    final String ct = " #" + tag;

                    if ( sb.length() + ct.length() <= realsize )
                    {
                        sb.append( ct );
                    }
                }
            }
        }

        return sb.toString();
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TW_Post.class );
    private final TwitterClient client;

    private long tweetPictureFailsafe( final String message ,
                                       final byte[] picture )
        throws RetryException
    {
        try
        {
            return new A_Retry<Long>()
            {
                @Override
                protected Long executeContent()
                    throws IOException , OAuthException
                {
                    try( ByteArrayInputStream bis = new ByteArrayInputStream( picture ) )
                    {
                        final long ID = client.tweetPicture( message ,
                                                             bis );

                        if ( ID < 0 )
                        {
                            throw new IOException( "Cannot tweet with message=" + message );
                        }
                        else
                        {
                            return ID;
                        }
                    }
                }
            }
                .setRetry( getProperties().getConfigInteger( "retry" ,
                                                             10 ) )
                .setWaitTime( getProperties().getConfigDuration( "wait-time" ,
                                                                 new Duration( 5000L ) ) )
                .setWaitTimeMultiplier( getProperties().getConfigDouble( "wait-time-multiplier" ,
                                                                         2.0 ) )
                .setJitterRange( getProperties().getConfigInteger( "jitter-range" ,
                                                                   500 ) )
                .setMaxDuration( getProperties().getConfigDuration( "max-duration" ,
                                                                    new Duration( 0L ) ) )
                .execute();
        }
        catch( final RetryException ex )
        {
            // Cannot send tweet+picture. Send only tweet.
            final Duration d = getProperties().getConfigDuration( "wait-time" ,
                                                                  new Duration( 5000L ) );
            if ( d.getMillis() > 0L )
            {
                try
                {
                    Thread.sleep( d.getMillis() );
                }
                catch( final InterruptedException ex2 )
                {
                    // Ignore
                }
            }

            return tweet( message );
        }
    }

    private long tweet( final String message )
        throws RetryException
    {
        return new A_Retry<Long>()
        {
            @Override
            protected Long executeContent()
                throws IOException , OAuthException
            {
                final long ID = client.tweet( message );

                if ( ID < 0 )
                {
                    throw new IOException( "Cannot tweet with message=" + message );
                }
                else
                {
                    return ID;
                }
            }
        }
            .
            setRetry( getProperties().getConfigInteger( "retry" ,
                                                        10 ) )
            .setWaitTime( getProperties().getConfigDuration( "wait-time" ,
                                                             new Duration( 5000L ) ) )
            .setWaitTimeMultiplier( getProperties().getConfigDouble( "wait-time-multiplier" ,
                                                                     2.0 ) )
            .setJitterRange( getProperties().getConfigInteger( "jitter-range" ,
                                                               500 ) )
            .setMaxDuration( getProperties().getConfigDuration( "max-duration" ,
                                                                new Duration( 0L ) ) )
            .execute();
    }
}
