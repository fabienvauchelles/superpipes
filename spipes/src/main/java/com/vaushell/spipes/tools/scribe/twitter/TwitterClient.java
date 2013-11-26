/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools.scribe.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.spipes.tools.scribe.A_OAuthClient;
import com.vaushell.spipes.tools.scribe.OAuthException;
import java.io.IOException;
import java.nio.file.Path;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class TwitterClient
        extends A_OAuthClient
{
    // PUBLIC
    public TwitterClient()
    {
        super();
    }

    public void login( String key ,
                       String secret ,
                       Path tokenPath ,
                       String loginText )
            throws IOException
    {
        loginImpl( TwitterApi.class ,
                   key ,
                   secret ,
                   null ,
                   null ,
                   true ,
                   tokenPath ,
                   loginText );
    }

    public long tweet( String message )
            throws IOException , OAuthException
    {
        if ( message == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] tweet() : message=" + message );
        }

        OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                 "https://api.twitter.com/1.1/statuses/update.json" );
        request.addBodyParameter( "status" ,
                                  message );

        Response response = sendSignedRequest( request );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "id" ).asLong();
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( TwitterClient.class );

    private void checkErrors( Response response ,
                              JsonNode root )
            throws OAuthException
    {
        JsonNode error = root.get( "errors" );
        if ( error != null )
        {
            JsonNode first = error.get( 0 );

            throw new OAuthException( response.getCode() ,
                                      first.get( "code" ).asInt() ,
                                      first.get( "message" ).asText() );
        }
    }
}