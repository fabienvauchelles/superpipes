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

package com.vaushell.spipes.nodes.twitter;

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.dispatch.Tags;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.OAuthException;
import com.vaushell.spipes.tools.scribe.twitter.TwitterClient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.HierarchicalConfiguration;
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
        this.retry = 3;
        this.delayBetweenRetry = new Duration( 5L * 1000L );
    }

    @Override
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        super.load( cNode );

        // Load retry count if exists.
        final String retryStr = getConfig( "retry" ,
                                           true );
        if ( retryStr != null )
        {
            try
            {
                retry = Integer.parseInt( retryStr );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "'retry' must be an integer" ,
                                                    ex );
            }
        }

        // Load delay between retry if exists.
        final String delayBetweenRetryStr = getConfig( "delay-between-retry" ,
                                                       true );
        if ( delayBetweenRetryStr != null )
        {
            try
            {
                delayBetweenRetry = new Duration( Long.parseLong( delayBetweenRetryStr ) );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "'delay-between-retry' must be a long" ,
                                                    ex );
            }
        }
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = getDispatcher().getDatas().resolve( Paths.get( getNodeID() ,
                                                                              "token" ) );

        client.login( getConfig( "key" ,
                                 false ) ,
                      getConfig( "secret" ,
                                 false ) ,
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

            ID = tweetPicture( createContent( getMessage() ,
                                              TwitterClient.TWEET_IMAGE_SIZE ) ,
                               picture ,
                               retry - 1 );
        }
        else
        {
            ID = tweet( createContent( getMessage() ,
                                       TwitterClient.TWEET_SIZE ) ,
                        retry );
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        if ( ID >= 0 )
        {
            getMessage().setProperty( "id-twitter" ,
                                      ID );

            sendMessage();
        }
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
    private int retry;
    private Duration delayBetweenRetry;

    private long tweet( final String message ,
                        final int remainingRetry )
        throws IOException , OAuthException
    {
        try
        {
            final long ID = client.tweet( message );

            if ( ID >= 0 )
            {
                return ID;
            }
            else
            {
                if ( remainingRetry <= 0 )
                {
                    return ID;
                }
            }
        }
        catch( final IOException |
                     OAuthException ex )
        {
            if ( remainingRetry <= 0 )
            {
                throw ex;
            }
        }

        if ( delayBetweenRetry.getMillis() > 0L )
        {
            try
            {
                Thread.sleep( delayBetweenRetry.getMillis() );
            }
            catch( final InterruptedException ex )
            {
                // Ignore
            }
        }

        return tweet( message ,
                      remainingRetry - 1 );
    }

    private long tweetPicture( final String message ,
                               final byte[] picture ,
                               final int remainingRetry )
        throws IOException , OAuthException
    {
        try( ByteArrayInputStream bis = new ByteArrayInputStream( picture ) )
        {
            final long ID = client.tweetPicture( message ,
                                                 bis );

            if ( ID >= 0 )
            {
                return ID;
            }
            else
            {
                if ( remainingRetry <= 0 )
                {
                    return ID;
                }
            }
        }
        catch( final IOException |
                     OAuthException ex )
        {
            if ( remainingRetry <= 0 )
            {
                throw ex;
            }
        }

        if ( delayBetweenRetry.getMillis() > 0L )
        {
            try
            {
                Thread.sleep( delayBetweenRetry.getMillis() );
            }
            catch( final InterruptedException ex )
            {
                // Ignore
            }
        }

        return tweetPicture( message ,
                             picture ,
                             remainingRetry - 1 );
    }
}
