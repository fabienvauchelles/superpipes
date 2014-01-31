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
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.tools.retry.A_Retry;
import com.vaushell.superpipes.tools.scribe.OAuthException;
import com.vaushell.superpipes.tools.scribe.twitter.TwitterClient;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retweet a tweet from Twitter.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TW_Retweet
    extends A_Node
{
    // PUBLIC
    public N_TW_Retweet()
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

        // Delete from Twitter
        if ( getMessage().contains( "id-twitter" ) )
        {
            final long oldID = (long) getMessage().getProperty( "id-twitter" );

            final long newID = new A_Retry<Long>()
            {
                @Override
                protected Long executeContent()
                    throws IOException , OAuthException
                {
                    final long ID = client.retweet( oldID );

                    if ( ID < 0 )
                    {
                        throw new IOException( "Cannot retweet with id=" + oldID );
                    }
                    else
                    {
                        return ID;
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

            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + getNodeID() + "] receive ID=" + newID );
            }

            getMessage().setProperty( "id-twitter" ,
                                      newID );

            sendMessage();
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TW_Retweet.class );
    private final TwitterClient client;
}
