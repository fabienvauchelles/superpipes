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

import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.OAuthClient;
import com.vaushell.spipes.tools.scribe.twitter.TwitterClient;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
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
        super();

        this.client = new TwitterClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = Paths.get( getMainConfig( "datas-directory" ) ,
                                          getNodeID() ,
                                          "token" );

        client.login( getConfig( "key" ) ,
                      getConfig( "secret" ) ,
                      tokenPath ,
                      OAuthClient.VCodeMethod.SYSTEM_INPUT ,
                      "[" + getClass().getName() + " / " + getNodeID() + "]" );
    }

    @Override
    protected void loop()
        throws Exception
    {
        // Receive
        final Message message = getLastMessageOrWait();

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        // Send to Twitter
        final String content = createContent( message );

        final long ID = client.tweet( content );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        message.setProperty( "id-twitter" ,
                             ID );

        sendMessage( message );
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // DEFAULT
    static final int TWEET_SIZE = 140;

    /**
     * Convert a message to a tweet correctly sized.
     *
     * @param message the Message
     * @return the Tweet content
     */
    @SuppressWarnings( "unchecked" )
    static String createContent( final Message message )
    {
        if ( message == null || !message.contains( Message.KeyIndex.URI ) )
        {
            throw new IllegalArgumentException();
        }

        final String uri = message.getProperty( Message.KeyIndex.URI ).toString();
        if ( uri.length() > TWEET_SIZE )
        {
            throw new IllegalArgumentException( "URL is too long" );
        }

        final StringBuilder sb = new StringBuilder();
        if ( uri.length() > TWEET_SIZE - 15 )
        {
            sb.append( uri );
        }
        else
        {
            if ( message.contains( Message.KeyIndex.TITLE ) )
            {
                final String title = (String) message.getProperty( Message.KeyIndex.TITLE );

                sb.append( " (" ).append( uri ).append( ')' );
                if ( title.length() + sb.length() > TWEET_SIZE )
                {
                    sb.insert( 0 ,
                               title.substring( 0 ,
                                                TWEET_SIZE - sb.length() ) );
                }
                else
                {
                    sb.insert( 0 ,
                               title );
                }
            }

            if ( message.contains( Message.KeyIndex.TAGS ) )
            {
                final Set<String> tags = (Set<String>) message.getProperty( Message.KeyIndex.TAGS );
                for ( final String tag : tags )
                {
                    final String ct = " #" + tag;

                    if ( sb.length() + ct.length() <= TWEET_SIZE )
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
}
