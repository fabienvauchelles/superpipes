/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaushell.spipes.tools;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
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
     */
    public static List<URI> getRedirected( final URI source )
        throws IOException
    {
        final HttpClientBuilder builder = HttpClientBuilder
            .create()
            .setDefaultCookieStore( new BasicCookieStore() )
            .setUserAgent( "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/15.0.1" );

        return getRedirected( builder ,
                              source );
    }

    /**
     * Expand all shorten URLs in a message.
     *
     * @param builder Http client builder
     * @param message the message
     * @return the message with expanded URLs
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String expandShortenURLinMessage( final HttpClientBuilder builder ,
                                                    final String message )
        throws URISyntaxException , IOException
    {
        final Pattern p = Pattern.compile( "http\\://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(/\\S*)?" );
        final Matcher m = p.matcher( message );

        String result = message;
        while ( m.find() )
        {
            final URI shorten = new URI( m.group() );

            final List<URI> redirects = HTTPhelper.getRedirected( shorten );
            if ( redirects.size() > 0 )
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
     * @throws URISyntaxException
     * @throws IOException
     */
    public static String expandShortenURLinMessage( final String message )
        throws URISyntaxException , IOException
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
