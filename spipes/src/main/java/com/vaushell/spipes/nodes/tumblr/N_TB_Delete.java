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

package com.vaushell.spipes.nodes.tumblr;

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.tumblr.TumblrClient;
import com.vaushell.spipes.tools.scribe.tumblr.TumblrException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delete a message from Tumblr. Return message if successfully delete.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TB_Delete
    extends A_Node
{
    // PUBLIC
    public N_TB_Delete()
    {
        super( null ,
               LIGHT_ANTIBURST );

        this.client = new TumblrClient();
        this.retry = 3;
        this.delayBetweenRetry = new Duration( 5L * 1000L );
        this.blogname = null;
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

        blogname = getConfig( "blogname" );
        if ( blogname == null )
        {
            throw new IllegalArgumentException( "'blogname' not found in configuration" );
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

    @SuppressWarnings( "unchecked" )
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
        if ( getMessage().contains( "id-tumblr" ) )
        {
            final long ID = (long) getMessage().getProperty( "id-tumblr" );

            if ( deletePost( ID ,
                             retry ) )
            {
                if ( LOGGER.isTraceEnabled() )
                {
                    LOGGER.trace( "[" + getNodeID() + "] can delete post ID=" + ID );
                }

                sendMessage();
            }
            else
            {
                if ( LOGGER.isTraceEnabled() )
                {
                    LOGGER.trace( "[" + getNodeID() + "] cannot delete post ID=" + ID );
                }
            }
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TB_Delete.class );
    private final TumblrClient client;
    private int retry;
    private Duration delayBetweenRetry;
    private String blogname;

    private boolean deletePost( final long ID ,
                                final int remainingRetry )
        throws IOException , TumblrException
    {
        try
        {
            if ( client.deletePost( blogname ,
                                    ID ) )
            {
                return true;
            }
            else
            {
                if ( remainingRetry <= 0 )
                {
                    return false;
                }
            }
        }
        catch( final IOException |
                     TumblrException ex )
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

        return deletePost( ID ,
                           remainingRetry - 1 );
    }
}
