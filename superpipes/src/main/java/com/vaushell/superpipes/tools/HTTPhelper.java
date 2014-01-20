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

package com.vaushell.superpipes.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.joda.time.Duration;

/**
 * HTTP helper.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class HTTPhelper
{
    // PUBLIC
    /**
     * Create a standard builder, firefox agent and ssl easy support.
     *
     * @return the builder.
     */
    public static HttpClientBuilder createBuilder()
    {
        try
        {
            return HttpClientBuilder
                .create()
                .setDefaultCookieStore( new BasicCookieStore() )
                .setUserAgent( "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/15.0.1" )
                .setSSLSocketFactory(
                    new SSLConnectionSocketFactory(
                        new SSLContextBuilder()
                        .loadTrustMaterial( null ,
                                            new TrustSelfSignedStrategy() )
                        .build()
                    )
                );
        }
        catch( final KeyManagementException |
                     KeyStoreException |
                     NoSuchAlgorithmException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    /**
     * Return all redirected URLs.
     *
     * @param builder Http client builder
     * @param source Source URI
     * @return a list of redirected URLs
     * @throws IOException
     */
    public static List<URI> getRedirected( final HttpClientBuilder builder ,
                                           final URI source )
        throws IOException
    {
        if ( builder == null || source == null )
        {
            throw new IllegalArgumentException();
        }

        final List<URI> uris = new ArrayList<>();

        builder.setRedirectStrategy( new DefaultRedirectStrategy()
        {

            @Override
            public HttpUriRequest getRedirect( final HttpRequest request ,
                                               final HttpResponse response ,
                                               final HttpContext context )
                throws ProtocolException
            {
                final HttpUriRequest r = super.getRedirect( request ,
                                                            response ,
                                                            context );

                uris.add( r.getURI() );

                return r;
            }

        } )
            .build();

        try( final CloseableHttpClient client = builder.build() )
        {
            final HttpGet get = new HttpGet( source );

            client.execute( get );

            get.abort();
        }

        return uris;
    }

    /**
     * Expand all shorten URLs contained inside a message.
     *
     * @param builder Http client builder
     * @param message the message
     * @return the message with expanded URLs
     * @throws IOException
     */
    public static String expandShortenURLinMessage( final HttpClientBuilder builder ,
                                                    final String message )
        throws IOException
    {
        if ( builder == null || message == null )
        {
            throw new IllegalArgumentException();
        }

        final Pattern p = Pattern.compile( "http\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(/\\S*)?" );
        final Matcher m = p.matcher( message );

        String result = message;
        while ( m.find() )
        {
            final URI shorten = URI.create( m.group() );

            final List<URI> redirects = HTTPhelper.getRedirected( builder ,
                                                                  shorten );
            if ( !redirects.isEmpty() )
            {
                final URI expand = redirects.get( redirects.size() - 1 );

                result = result.replace( shorten.toString() ,
                                         expand.toString() );
            }
        }

        return result;
    }

    /**
     * Is a URI exist (HTTP response code between 200 and 299).
     *
     * @param client HTTP client.
     * @param uri the URI.
     * @param timeout how many ms to wait ? (could be null)
     * @return true if it exists.
     * @throws IOException
     */
    public static boolean isURIvalid( final CloseableHttpClient client ,
                                      final URI uri ,
                                      final Duration timeout )
        throws IOException
    {
        if ( client == null || uri == null )
        {
            throw new IllegalArgumentException();
        }

        HttpEntity responseEntity = null;
        try
        {
            // Exec request
            final HttpGet get = new HttpGet( uri );

            if ( timeout != null && timeout.getMillis() > 0L )
            {
                get.setConfig(
                    RequestConfig.custom()
                    .setConnectTimeout( (int) timeout.getMillis() )
                    .setConnectionRequestTimeout( (int) timeout.getMillis() )
                    .setSocketTimeout( (int) timeout.getMillis() )
                    .build()
                );
            }

            try( final CloseableHttpResponse response = client.execute( get ) )
            {
                final StatusLine sl = response.getStatusLine();
                responseEntity = response.getEntity();

                return sl.getStatusCode() >= 200 && sl.getStatusCode() < 300;
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

    /**
     * Load a image.
     *
     * @param client Http client.
     * @param uri the URL.
     * @return the image.
     * @throws IOException
     */
    public static BufferedImage loadPicture( final CloseableHttpClient client ,
                                             final URI uri )
        throws IOException
    {
        if ( client == null || uri == null )
        {
            throw new IllegalArgumentException();
        }

        HttpEntity responseEntity = null;
        try
        {
            // Exec request
            final HttpGet get = new HttpGet( uri );

            try( final CloseableHttpResponse response = client.execute( get ) )
            {
                final StatusLine sl = response.getStatusLine();
                if ( sl.getStatusCode() != 200 )
                {
                    throw new IOException( sl.getReasonPhrase() );
                }

                responseEntity = response.getEntity();

                final Header ct = responseEntity.getContentType();
                if ( ct == null )
                {
                    return null;
                }

                final String type = ct.getValue();
                if ( type == null )
                {
                    return null;
                }

                if ( !type.startsWith( "image/" ) )
                {
                    return null;
                }

                try( final ByteArrayOutputStream bos = new ByteArrayOutputStream() )
                {
                    try( final InputStream is = responseEntity.getContent() )
                    {
                        IOUtils.copy( is ,
                                      bos );
                    }

                    if ( bos.size() <= 0 )
                    {
                        return null;
                    }
                    else
                    {
                        try( final ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() ) )
                        {
                            return ImageIO.read( bis );
                        }
                    }
                }
            }
        }
        finally
        {
            if ( responseEntity != null )
            {
                EntityUtils.consume( responseEntity );
            }
        }
    }

    // PRIVATE
    private HTTPhelper()
    {
        // Nothing
    }
}
