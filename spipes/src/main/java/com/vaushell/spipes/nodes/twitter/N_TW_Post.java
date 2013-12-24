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
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.twitter.TwitterClient;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
        super( 0 ,
               DEFAULT_ANTIBURST_IN_MS );

        this.client = new TwitterClient();
        this.httpClient = null;
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = getDispatcher().getDatas().resolve( Paths.get( getNodeID() ,
                                                                              "token" ) );

        client.login( getConfig( "key" ) ,
                      getConfig( "secret" ) ,
                      tokenPath ,
                      getDispatcher().getVCodeFactory().create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );

        httpClient = HttpClientBuilder
            .create()
            .setDefaultCookieStore( new BasicCookieStore() )
            .setUserAgent( "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/15.0.1" )
            .build();
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
        final long ID;
        if ( message.contains( Message.KeyIndex.URI_PICTURE ) )
        {
            final URI picture = (URI) message.getProperty( Message.KeyIndex.URI_PICTURE );

            HttpEntity responseEntity = null;
            try
            {
                // Exec request
                final HttpGet get = new HttpGet( picture );

                try( final CloseableHttpResponse response = httpClient.execute( get ) )
                {
                    final StatusLine sl = response.getStatusLine();
                    if ( sl.getStatusCode() == 200 )
                    {
                        responseEntity = response.getEntity();

                        try( final InputStream is = responseEntity.getContent() )
                        {
                            ID = client.tweetPicture( createContent( message ,
                                                                     TwitterClient.TWEET_SIZE - TwitterClient.MEDIA_RESERVED ) ,
                                                      is );
                        }
                    }
                    else
                    {
                        ID = client.tweet( createContent( message ,
                                                          TwitterClient.TWEET_SIZE ) );
                    }
                }
            }
            finally
            {
                if ( responseEntity != null )
                {
                    EntityUtils.consume( responseEntity );
                }
            }
        }
        else
        {
            ID = client.tweet( createContent( message ,
                                              TwitterClient.TWEET_SIZE ) );
        }

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
        if ( httpClient != null )
        {
            httpClient.close();
        }
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
        if ( uri.length() > size - 15 )
        {
            sb.append( uri );
        }
        else
        {
            if ( message.contains( Message.KeyIndex.TITLE ) )
            {
                final String title = (String) message.getProperty( Message.KeyIndex.TITLE );

                sb.append( " (" ).append( uri ).append( ')' );
                if ( title.length() + sb.length() > size )
                {
                    sb.insert( 0 ,
                               title.substring( 0 ,
                                                size - sb.length() ) );
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

                    if ( sb.length() + ct.length() <= size )
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
    private CloseableHttpClient httpClient;
}
