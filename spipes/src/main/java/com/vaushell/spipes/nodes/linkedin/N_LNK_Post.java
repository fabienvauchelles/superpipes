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

package com.vaushell.spipes.nodes.linkedin;

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.twitter.N_TW_Post;
import com.vaushell.spipes.tools.scribe.OAuthException;
import com.vaushell.spipes.tools.scribe.linkedin.LinkedInClient;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post a message to LinkedIn.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_LNK_Post
    extends A_Node
{
    // PUBLIC
    public N_LNK_Post()
    {
        super( null ,
               DEFAULT_ANTIBURST );

        this.client = new LinkedInClient();
        this.retry = 3;
        this.delayBetweenRetry = new Duration( 5L * 1000L );
    }

    @Override
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        super.load( cNode );

        // Load retry count if exists.
        final String retryStr = getConfig( "retry" );
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
        final String delayBetweenRetryStr = getConfig( "delay-between-retry" );
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

        client.login( getConfig( "key" ) ,
                      getConfig( "secret" ) ,
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

        if ( !getMessage().contains( Message.KeyIndex.URI ) )
        {
            throw new IllegalArgumentException( "message doesn't have an uri" );
        }

        // Send to Twitter
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

        final String ID = postLink( uriStr ,
                                    (String) getMessage().getProperty( Message.KeyIndex.TITLE ) ,
                                    retry );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        if ( ID != null && !ID.isEmpty() )
        {
            getMessage().setProperty( "id-linkedin" ,
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
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TW_Post.class );
    private final LinkedInClient client;
    private int retry;
    private Duration delayBetweenRetry;

    private String postLink( final String uriStr ,
                             final String title ,
                             final int remainingRetry )
        throws IOException , OAuthException
    {
        try
        {
            final String ID = client.postLink( null ,
                                               uriStr ,
                                               (String) getMessage().getProperty( Message.KeyIndex.TITLE ) ,
                                               null );

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

        return postLink( uriStr ,
                         title ,
                         remainingRetry - 1 );
    }
}
