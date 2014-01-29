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
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post a message to Facebook.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_FB_Post
    extends A_Node
{
    // PUBLIC
    public N_FB_Post()
    {
        super( null ,
               SECURE_ANTIBURST );

        this.client = new FacebookClient();
        this.forcedTarget = null;
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

            forcedTarget = getProperties().getConfigString( "userid" ,
                                                            null );
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
        setMessage( getLastMessageOrWait() );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + Message.formatSimple( getMessage() ) );
        }

        if ( !getMessage().contains( Message.KeyIndex.URI ) )
        {
            throw new IllegalArgumentException( "message doesn't have an uri" );
        }

        // Send to FB
        final URI uri = (URI) getMessage().getProperty( Message.KeyIndex.URI );
        final String uriStr;
        if ( uri == null )
        {
            uriStr = null;
        }
        else
        {
            uriStr = uri.toString();
        }

        final String caption;
        if ( getMessage().contains( Message.KeyIndex.URI_SOURCE ) )
        {
            caption = ( (URI) getMessage().getProperty( Message.KeyIndex.URI_SOURCE ) ).getHost();
        }
        else
        {
            caption = null;
        }

        final DateTime date;
        if ( getProperties().getConfigBoolean( "backdating" ,
                                               Boolean.FALSE ) )
        {
            date = (DateTime) getMessage().getProperty( Message.KeyIndex.PUBLISHED_DATE );
        }
        else
        {
            date = null;
        }

        final String ID = new A_Retry<String>()
        {
            @Override
            protected String executeContent()
                throws FacebookException , IOException
            {
                final String title = (String) getMessage().getProperty( Message.KeyIndex.TITLE );

                final String ID = client.postLink( forcedTarget ,
                                                   (String) getMessage().getProperty( Message.KeyIndex.CONTENT ) ,
                                                   uriStr ,
                                                   title ,
                                                   caption ,
                                                   (String) getMessage().getProperty( Message.KeyIndex.DESCRIPTION ) ,
                                                   date );

                if ( ID == null || ID.isEmpty() )
                {
                    throw new IOException( "Cannot post link with title=" + title );
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
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        getMessage().setProperty( "id-facebook" ,
                                  ID );

        sendMessage();
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_FB_Post.class );
    private final FacebookClient client;
    private String forcedTarget;
}
