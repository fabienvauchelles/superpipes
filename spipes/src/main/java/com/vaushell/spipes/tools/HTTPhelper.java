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

package com.vaushell.spipes.tools;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
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

/**
 * HTTP helper.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class HTTPhelper
{
    // PUBLIC
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
            final HttpGet httpget = new HttpGet( source );

            client.execute( httpget );

            httpget.abort();
        }

        return uris;
    }

    /**
     * Return all redirected URLs.
     *
     * @param source Source URI
     * @return a list of redirected URLs
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static List<URI> getRedirected( final URI source )
        throws IOException , GeneralSecurityException
    {
        final HttpClientBuilder builder = HttpClientBuilder
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

        return getRedirected( builder ,
                              source );
    }

    /**
     * Expand all shorten URLs in a message.
     *
     * @param builder Http client builder
     * @param message the message
     * @return the message with expanded URLs
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static String expandShortenURLinMessage( final HttpClientBuilder builder ,
                                                    final String message )
        throws IOException , GeneralSecurityException
    {
        final Pattern p = Pattern.compile( "http\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(/\\S*)?" );
        final Matcher m = p.matcher( message );

        String result = message;
        while ( m.find() )
        {
            final URI shorten = URI.create( m.group() );

            final List<URI> redirects = HTTPhelper.getRedirected( shorten );
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
     * Expand all shorten URLs in a message.
     *
     * @param message the message
     * @return the message with expanded URLs
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public static String expandShortenURLinMessage( final String message )
        throws IOException , GeneralSecurityException
    {
        final HttpClientBuilder builder = HttpClientBuilder
            .create()
            .setDefaultCookieStore( new BasicCookieStore() )
            .setUserAgent( "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/15.0.1" );

        return expandShortenURLinMessage( builder ,
                                          message );
    }

    // PRIVATE
    private HTTPhelper()
    {
        // Nothing
    }
}
