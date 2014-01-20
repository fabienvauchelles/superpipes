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
import com.vaushell.superpipes.tools.scribe.fb.FacebookClient;
import com.vaushell.superpipes.tools.scribe.fb.FacebookException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.HierarchicalConfiguration;
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
        this.retry = 3;
        this.delayBetweenRetry = new Duration( 5L * 1000L );
        this.forcedTarget = null;
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

        final String pageName = getConfig( "pagename" ,
                                           true );
        if ( pageName == null )
        {
            client.login( getConfig( "key" ,
                                     false ) ,
                          getConfig( "secret" ,
                                     false ) ,
                          tokenPath ,
                          getDispatcher().getVCodeFactory().create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );

            forcedTarget = getConfig( "userid" ,
                                      true );
        }
        else
        {
            client.loginAsPage( pageName ,
                                getConfig( "key" ,
                                           false ) ,
                                getConfig( "secret" ,
                                           false ) ,
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
        String uriStr;
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
        if ( "true".equals( getConfig( "backdating" ,
                                       true ) ) )
        {
            date = (DateTime) getMessage().getProperty( Message.KeyIndex.PUBLISHED_DATE );
        }
        else
        {
            date = null;
        }

        final String ID = postLink( uriStr ,
                                    (String) getMessage().getProperty( Message.KeyIndex.TITLE ) ,
                                    caption ,
                                    (String) getMessage().getProperty( Message.KeyIndex.DESCRIPTION ) ,
                                    date ,
                                    retry );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        if ( ID != null && !ID.isEmpty() )
        {
            getMessage().setProperty( "id-facebook" ,
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
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_FB_Post.class );
    private final FacebookClient client;
    private int retry;
    private Duration delayBetweenRetry;
    private String forcedTarget;

    private String postLink( final String uriStr ,
                             final String title ,
                             final String caption ,
                             final String description ,
                             final DateTime date ,
                             final int remainingRetry )
        throws FacebookException , IOException
    {
        try
        {
            final String ID = client.postLink( forcedTarget ,
                                               null ,
                                               uriStr ,
                                               (String) getMessage().getProperty( Message.KeyIndex.TITLE ) ,
                                               caption ,
                                               (String) getMessage().getProperty( Message.KeyIndex.DESCRIPTION ) ,
                                               date );

            if ( ID == null || ID.isEmpty() )
            {
                if ( remainingRetry <= 0 )
                {
                    return null;
                }
            }
            else
            {
                return ID;
            }
        }
        catch( final Throwable ex )
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

        return postLink( uriStr ,
                         title ,
                         caption ,
                         description ,
                         date ,
                         remainingRetry - 1 );
    }

}
