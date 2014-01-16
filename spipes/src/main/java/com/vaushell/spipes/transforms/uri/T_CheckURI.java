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

package com.vaushell.spipes.transforms.uri;

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.tools.HTTPhelper;
import com.vaushell.spipes.transforms.A_Transform;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.http.impl.client.CloseableHttpClient;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check if the URI exists.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_CheckURI
    extends A_Transform
{
    // PUBLIC
    public T_CheckURI()
    {
        super();

        this.client = null;
        this.timeout = new Duration( 20L * 1000L );
        this.retry = 3;
        this.delayBetweenRetry = new Duration( 5L * 1000L );
    }

    @Override
    public void prepare()
        throws Exception
    {
        this.client = HTTPhelper.createBuilder().build();
    }

    @Override
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        super.load( cNode );

        // Load timeout if exists.
        final String timeoutStr = getConfig( "timeout" );
        if ( timeoutStr != null )
        {
            try
            {
                timeout = new Duration( Long.parseLong( timeoutStr ) );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "'timeout' must be a long" ,
                                                    ex );
            }
        }

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

    @Override
    public Message transform( final Message message )
        throws Exception
    {
        // Receive
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNode().getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + Message.
                formatSimple( message ) );
        }

        if ( !message.contains( Message.KeyIndex.URI ) )
        {
            return null;
        }

        final URI uri = (URI) message.getProperty( Message.KeyIndex.URI );

        try
        {
            if ( isURIvalid( uri ,
                             retry ) )
            {
                return message;
            }
            else
            {
                if ( LOGGER.isTraceEnabled() )
                {
                    LOGGER.trace( "[" + getNode().getNodeID() + "/" + getClass().getSimpleName() + "] Invalid URI : " + uri.
                        toString() );
                }

                return null;
            }
        }
        catch( final IOException ex )
        {
            LOGGER.warn( "Error while checking URI : '" + uri.toString() + "'" ,
                         ex );

            return null;
        }
    }

    @Override
    public void terminate()
        throws IOException
    {
        if ( client != null )
        {
            client.close();
        }
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( T_CheckURI.class );
    private CloseableHttpClient client;
    private Duration timeout;
    private int retry;
    private Duration delayBetweenRetry;

    private boolean isURIvalid( final URI uri ,
                                final int remainingRetry )
        throws IOException
    {
        if ( uri == null )
        {
            throw new IllegalArgumentException();
        }

        try
        {
            if ( HTTPhelper.isURIvalid( client ,
                                        uri ,
                                        timeout ) )
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
        catch( final IOException ex )
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

        return isURIvalid( uri ,
                           remainingRetry - 1 );
    }
}
