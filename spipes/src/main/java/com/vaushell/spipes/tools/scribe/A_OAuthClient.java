/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public abstract class A_OAuthClient
{
    // PUBLIC
    public A_OAuthClient()
    {
        this.service = null;
        this.accessToken = null;
    }

    // PROTECTED
    protected void loginImpl( Class<? extends Api> api ,
                              String key ,
                              String secret ,
                              String scope ,
                              String callback ,
                              boolean useRequestToken ,
                              Path tokenPath ,
                              String loginText )
            throws IOException
    {
        if ( api == null || key == null || secret == null || tokenPath == null || loginText == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isDebugEnabled() )
        {
            logger.debug(
                    "[" + getClass().getSimpleName() + "] loginImpl() : api=" + api + " / key=" + key + " / scope=" + scope + " / callback=" + callback + " / useRequestToken=" + useRequestToken + " / tokenPath=" + tokenPath + " / loginText=" + loginText );
        }

        ServiceBuilder builder = new ServiceBuilder()
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
            Token requestToken = useRequestToken ? service.getRequestToken() : null;

            // Making the user validate your request token
            String authUrl = service.getAuthorizationUrl( requestToken );
            System.out.println( loginText + " Use this URL :" );
            System.out.println( authUrl );

            System.out.println( loginText + " Enter code :" );

            Scanner sc = new Scanner( System.in );
            String code = sc.next();
            System.out.println( loginText + " Read code is '" + code + "'" );

            // Get the access Token
            Verifier v = new Verifier( code );
            accessToken = service.getAccessToken( requestToken ,
                                                  v );

            saveToken( accessToken ,
                       tokenPath );
        }
    }

    protected Response sendSignedRequest( OAuthRequest request )
    {
        if ( request == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getClass().getSimpleName() + "] sendSignedRequest() : request=" + request );
        }

        service.signRequest( accessToken ,
                             request );

        return request.send();
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( A_OAuthClient.class );
    private OAuthService service;
    private Token accessToken;

    private static Token loadToken( Path path )
            throws IOException
    {
        if ( path == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + A_OAuthClient.class.getSimpleName() + "] loadToken() : path=" + path );
        }

        if ( Files.notExists( path ) )
        {
            return null;
        }

        try( BufferedReader bfr = Files.newBufferedReader( path ,
                                                           Charset.forName( "utf-8" ) ) )
        {
            String token = bfr.readLine();
            String secret = bfr.readLine();
            String raw = bfr.readLine();

            if ( raw != null )
            {
                return new Token( token ,
                                  secret ,
                                  raw );
            }
            else
            {
                return new Token( token ,
                                  secret );
            }
        }
    }

    private static void saveToken( Token accessToken ,
                                   Path path )
            throws IOException
    {
        if ( path == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + A_OAuthClient.class.getSimpleName() + "] saveToken() : accessToken=" + accessToken + " / path=" + path );
        }

        if ( accessToken == null )
        {
            return;
        }

        if ( Files.notExists( path.getParent() ) )
        {
            Files.createDirectories( path.getParent() );
        }

        try( BufferedWriter bfr = Files.newBufferedWriter( path ,
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
