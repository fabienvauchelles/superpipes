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
import com.vaushell.spipes.transforms.A_Transform;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
            if ( isURIvalid( uri ,
                             RETRY ) )
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
    private static final int RETRY = 2;
    private CloseableHttpClient client;

    private boolean isURIvalid( final URI uri ,
                                final int retry )
        throws IOException
    {
        if ( uri == null )
        {
            throw new IllegalArgumentException();
        }

        HttpEntity responseEntity = null;
        try
        {
            // Exec request
            final HttpGet get = new HttpGet( uri );

            int timeout = 20000;

            final String timeoutStr = getConfig( "timeout" );
            if ( timeoutStr != null )
            {
                try
                {
                    timeout = Integer.parseInt( timeoutStr );
                }
                catch( final NumberFormatException ex )
                {
                    // Ignore
                }
            }

            get.setConfig(
                RequestConfig.custom()
                .setConnectTimeout( timeout )
                .setConnectionRequestTimeout( timeout )
                .setSocketTimeout( timeout )
                .build()
            );

            try( final CloseableHttpResponse response = client.execute( get ) )
            {
                final StatusLine sl = response.getStatusLine();
                responseEntity = response.getEntity();

                if ( sl.getStatusCode() >= 200 && sl.getStatusCode() < 300 )
                {
                    return true;
                }
                else
                {
                    if ( retry > 0 )
                    {
                        return isURIvalid( uri ,
                                           retry - 1 );
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        catch( final IOException ex )
        {
            if ( retry > 0 )
            {
                return isURIvalid( uri ,
                                   retry - 1 );
            }
            else
            {
                throw ex;
            }
        }
        finally
        {
            if ( responseEntity != null )
            {
                EntityUtils.consumeQuietly( responseEntity );
            }
        }
    }
}
