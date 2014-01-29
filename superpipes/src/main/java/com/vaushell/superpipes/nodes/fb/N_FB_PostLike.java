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

package com.vaushell.superpipes.nodes.fb;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.tools.retry.A_Retry;
import com.vaushell.superpipes.tools.scribe.fb.FacebookClient;
import com.vaushell.superpipes.tools.scribe.fb.FacebookException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Like a message on Facebook.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_FB_PostLike
    extends A_Node
{
    // PUBLIC
    public N_FB_PostLike()
    {
        super( null ,
               DEFAULT_ANTIBURST );

        this.client = new FacebookClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = getDispatcher().getDatas().resolve( Paths.get( getNodeID() ,
                                                                              "token" ) );

        final String pageName = getProperties().getConfigString( "pagename" ,
                                                                 null );
        if ( pageName == null )
        {
            client.login( getProperties().getConfigString( "key" ) ,
                          getProperties().getConfigString( "secret" ) ,
                          tokenPath ,
                          getDispatcher().getVCodeFactory().create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
        }
        else
        {
            client.loginAsPage( pageName ,
                                getProperties().getConfigString( "key" ) ,
                                getProperties().getConfigString( "secret" ) ,
                                tokenPath ,
                                getDispatcher().getVCodeFactory().
                create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
        }
    }

    @Override
    protected void loop()
        throws Exception
    {
        // Receive
        setMessage( (Message) getLastMessageOrWait() );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + Message.formatSimple( getMessage() ) );
        }

        if ( !getMessage().contains( "id-facebook" ) )
        {
            throw new IllegalArgumentException( "message doesn't have an post id" );
        }

        final String ID = (String) getMessage().getProperty( "id-facebook" );

        // Like
        new A_Retry<Void>()
        {

            @Override
            protected Void executeContent()
                throws IOException , FacebookException
            {
                if ( client.likePost( ID ) )
                {
                    return null;
                }
                else
                {
                    throw new IOException( "Can't delete post with ID=" + ID );
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

        sendMessage();
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_FB_PostLike.class );
    private final FacebookClient client;
}
