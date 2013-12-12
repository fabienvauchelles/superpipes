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

package com.vaushell.spipes.tools.scribe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OAuth client pattern.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class OAuthClient
{
    // PROTECTED
    protected OAuthClient()
    {
        // Nothing
    }

    /**
     * Log in.
     *
     * @param api Scribe API class
     * @param key OAuth key
     * @param secret OAuth secret
     * @param scope OAuth scope (or null)
     * @param callback OAuth URL callback (or null)
     * @param useRequestToken OAuth use of request token ?
     * @param tokenPath Path to save the token
     * @param loginText Prefix message to request the token to the user
     * @throws IOException
     */
    protected void loginImpl( final Class<? extends Api> api ,
                              final String key ,
                              final String secret ,
                              final String scope ,
                              final String callback ,
                              final boolean useRequestToken ,
                              final Path tokenPath ,
                              final String loginText )
        throws IOException
    {
        if ( api == null || key == null || secret == null || tokenPath == null || loginText == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug(
                "[" + getClass().getSimpleName() + "] loginImpl() : api=" + api + " / key=" + key + " / scope=" + scope + " / callback=" + callback + " / useRequestToken=" + useRequestToken + " / tokenPath=" + tokenPath + " / loginText=" + loginText );
        }

        final ServiceBuilder builder = new ServiceBuilder()
            .provider( api )
            .apiKey( key )
            .apiSecret( secret );

        if ( scope != null )
        {
            builder.scope( scope );
        }

        if ( callback != null )
        {
            builder.callback( callback );
        }

        service = builder.build();

        accessToken = loadToken( tokenPath );
        if ( accessToken == null )
        {
            // Get the request token
            final Token requestToken = useRequestToken ? service.getRequestToken() : null;

            // Making the user validate your request token
            final String authUrl = service.getAuthorizationUrl( requestToken );
            System.out.println( loginText + " Use this URL :" );
            System.out.println( authUrl );

            System.out.println( loginText + " Enter code :" );

            final Scanner sc = new Scanner( System.in ,
                                            "UTF-8" );
            final String code = sc.next();
            System.out.println( loginText + " Read code is '" + code + "'" );

            // Get the access Token
            final Verifier v = new Verifier( code );
            accessToken = service.getAccessToken( requestToken ,
                                                  v );

            saveToken( accessToken ,
                       tokenPath );
        }
    }

    /**
     * Send a signed request.
     *
     * @param request the request
     * @return the response
     */
    protected Response sendSignedRequest( final OAuthRequest request )
    {
        if ( request == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] sendSignedRequest() : request=" + request );
        }

        service.signRequest( accessToken ,
                             request );

        return request.send();
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( OAuthClient.class );
    private OAuthService service;
    private Token accessToken;

    private static Token loadToken( final Path path )
        throws IOException
    {
        if ( path == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + OAuthClient.class.getSimpleName() + "] loadToken() : path=" + path );
        }

        if ( Files.notExists( path ) )
        {
            return null;
        }

        try( final BufferedReader bfr = Files.newBufferedReader( path ,
                                                                 Charset.forName( "utf-8" ) ) )
        {
            final String token = bfr.readLine();
            final String secret = bfr.readLine();
            final String raw = bfr.readLine();

            if ( raw == null )
            {
                return new Token( token ,
                                  secret );
            }
            else
            {
                return new Token( token ,
                                  secret ,
                                  raw );
            }
        }
    }

    private static void saveToken( final Token accessToken ,
                                   final Path path )
        throws IOException
    {
        if ( path == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + OAuthClient.class.getSimpleName() + "] saveToken() : accessToken=" + accessToken + " / path=" + path );
        }

        if ( accessToken == null )
        {
            return;
        }

        if ( Files.notExists( path.getParent() ) )
        {
            Files.createDirectories( path.getParent() );
        }

        try( final BufferedWriter bfr = Files.newBufferedWriter( path ,
                                                                 Charset.forName( "utf-8" ) ) )
        {
            bfr.write( accessToken.getToken() );
            bfr.newLine();

            bfr.write( accessToken.getSecret() );
            bfr.newLine();

            if ( accessToken.getRawResponse() != null )
            {
                bfr.write( accessToken.getRawResponse() );
                bfr.newLine();
            }
        }
    }
}
