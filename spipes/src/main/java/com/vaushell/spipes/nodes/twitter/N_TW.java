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
import com.vaushell.spipes.tools.scribe.twitter.TW_Tweet;
import com.vaushell.spipes.tools.scribe.twitter.TwitterClient;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a Twitter timeline.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TW
    extends A_Node
{
    // PUBLIC
    public N_TW()
    {
        // Read every 10 minutes
        super( new Duration( 600000L ) ,
               null );

        this.client = new TwitterClient();
        this.forcedTarget = null;
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

        final String userIDstr = getConfig( "userid" );
        if ( userIDstr != null )
        {
            try
            {
                forcedTarget = Long.parseLong( userIDstr );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "'userid' is not a long" ,
                                                    ex );
            }
        }
    }

    @Override
    protected void loop()
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] read timeline " );
        }

        final int max = Integer.parseInt( getConfig( "max" ) );

        int count = 0;
        final Iterator<TW_Tweet> it = client.iteratorTimeline( forcedTarget ,
                                                               Math.min( POST_MAX_COUNT ,
                                                                         max ) );

        while ( it.hasNext() && count < max )
        {
            final TW_Tweet tweet = it.next();

            if ( tweet.getID() >= 0 )
            {
                setMessage( Message.create(
                    "id-twitter" ,
                    tweet.getID() ,
                    Message.KeyIndex.PUBLISHED_DATE ,
                    tweet.getCreatedTime()
                ) );

                if ( tweet.getUser() != null && tweet.getUser().getName() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.AUTHOR ,
                                              tweet.getUser().getName() );
                }

                if ( tweet.getMessage() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.CONTENT ,
                                              tweet.getMessage() );
                }

                sendMessage();
            }

            ++count;
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TW.class );
    private final TwitterClient client;
    private static final int POST_MAX_COUNT = 200;
    private Long forcedTarget;
}
