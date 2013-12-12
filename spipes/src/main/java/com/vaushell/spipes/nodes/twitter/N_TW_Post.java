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

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import com.vaushell.spipes.tools.HTMLhelper;
import com.vaushell.spipes.tools.scribe.OAuthException;
import com.vaushell.spipes.tools.scribe.twitter.TwitterClient;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TW_Post
    extends A_Node
{
    // PUBLIC
    public N_TW_Post()
    {
        this.client = new TwitterClient();
    }

    @Override
    public void prepare()
        throws Exception
    {
        Path tokenPath = Paths.get( getMainConfig( "datas-directory" ) ,
                                    getNodeID() ,
                                    "token" );

        client.login( getConfig( "key" ) ,
                      getConfig( "secret" ) ,
                      tokenPath ,
                      "[" + getClass().getName() + " / " + getNodeID() + "]" );
    }

    @Override
    public void terminate()
        throws Exception
    {
    }
    // PROTECTED

    @Override
    protected void loop()
        throws InterruptedException , IOException , OAuthException
    {
        // Receive
        Object message = getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        // Convert if possible
        Tweet tweet;
        if ( message == null )
        {
            tweet = null;
        }
        else
        {
            if ( message instanceof Tweet )
            {
                tweet = (Tweet) message;
            }
            else if ( message instanceof News )
            {
                tweet = convertFromNews( (News) message );
            }
            else
            {
                tweet = null;
            }
        }

        if ( tweet == null )
        {
            throw new IllegalArgumentException( "message type is unknown : " + message.getClass().getName() );
        }

        // Send to Twitter
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] send tweet to twitter : " + tweet );
        }

        long ID = client.tweet( tweet.getMessage() );

        tweet.setTweetID( ID );

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        sendMessage( tweet );
    }
    // DEFAULT
    final static int TWEET_SIZE = 140;

    static Tweet convertFromNews( News news )
    {
        if ( news.getURI() == null )
        {
            throw new NullPointerException();
        }

        String uri = news.getURI().toString();
        if ( uri.length() > TWEET_SIZE )
        {
            throw new IllegalArgumentException( "URL is too long" );
        }

        StringBuilder sb = new StringBuilder();
        if ( uri.length() > TWEET_SIZE - 15 )
        {
            sb.append( uri );
        }
        else
        {
            String title = HTMLhelper.cleanHTML( news.getTitle() );
            if ( title != null )
            {
                sb.append( " (" ).append( uri ).append( ")" );
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

            if ( news.getTags() != null )
            {
                TreeSet<String> correctedTags = new TreeSet<>();
                for ( String tag : news.getTags() )
                {
                    String correctedTag = tag.toLowerCase();

                    correctedTags.add( correctedTag );
                }

                for ( String correctedTag : correctedTags )
                {
                    String ct = " #" + correctedTag;

                    if ( sb.length() + ct.length() <= TWEET_SIZE )
                    {
                        sb.append( ct );
                    }
                }
            }
        }

        return new Tweet( sb.toString() );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_TW_Post.class );
    private TwitterClient client;
}
