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

package com.vaushell.superpipes.transforms.uri;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.tools.HTTPhelper;
import com.vaushell.superpipes.tools.retry.A_Retry;
import com.vaushell.superpipes.tools.retry.RetryException;
import com.vaushell.superpipes.transforms.A_Transform;
import java.io.IOException;
import java.net.URI;
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
    }

    @Override
    public void prepare()
        throws Exception
    {
        this.client = HTTPhelper.createBuilder().build();
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
            new A_Retry<Void>()
            {
                @Override
                protected Void executeContent()
                    throws IOException
                {
                    if ( HTTPhelper.isURIvalid( client ,
                                                uri ,
                                                getProperties().getConfigDuration( "timeout" ,
                                                                                   new Duration( 20L * 1000L ) ) ) )
                    {
                        return null;
                    }
                    else
                    {
                        throw new IOException( "Cannot validate URI=" + uri );
                    }
                }
            }
                .setRetry( getProperties().getConfigInteger( "retry" ,
                                                             3 ) )
                .setWaitTime( getProperties().getConfigDuration( "wait-time" ,
                                                                 new Duration( 2000L ) ) )
                .setWaitTimeMultiplier( getProperties().getConfigDouble( "wait-time-multiplier" ,
                                                                         2.0 ) )
                .setJitterRange( getProperties().getConfigInteger( "jitter-range" ,
                                                                   500 ) )
                .setMaxDuration( getProperties().getConfigDuration( "max-duration" ,
                                                                    new Duration( 10_000L ) ) )
                .execute();

            return message;
        }
        catch( final RetryException ex )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + getNode().getNodeID() + "/" + getClass().getSimpleName() + "] Invalid URI : " + uri.
                    toString() ,
                              ex );
            }

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
}
