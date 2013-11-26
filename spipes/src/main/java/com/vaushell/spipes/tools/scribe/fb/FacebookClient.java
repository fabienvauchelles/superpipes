/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools.scribe.fb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.spipes.tools.scribe.A_OAuthClient;
import java.io.IOException;
import java.nio.file.Path;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class FacebookClient
        extends A_OAuthClient
{
    // PUBLIC
    public FacebookClient()
    {
        super();
    }

    public void login( String key ,
                       String secret ,
                       String scope ,
                       Path tokenPath ,
                       String loginText )
            throws IOException
    {
        loginImpl( FacebookApi.class ,
                   key ,
                   secret ,
                   scope ,
                   "http://www.facebook.com/connect/login_success.html" ,
                   false ,
                   tokenPath ,
                   loginText );
    }

    public String post( String message ,
                        String uri ,
                        String uriName ,
                        String uriCaption ,
                        String uriDescription )
            throws FacebookException , IOException
    {
        if ( ( uri == null || uri.length() <= 0 ) && ( message == null || message.length() <= 0 ) )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] post() : message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriCaption=" + uriCaption + " / uriDescription=" + uriDescription );
        }

        OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                 "https://graph.facebook.com/me/feed" );

        if ( message != null && message.length() > 0 )
        {
            request.addBodyParameter( "message" ,
                                      message );
        }

        if ( uri != null && uri.length() > 0 )
        {
            request.addBodyParameter( "link" ,
                                      uri );
        }

        if ( uriName != null && uriName.length() > 0 )
        {
            request.addBodyParameter( "name" ,
                                      uriName );
        }

        if ( uriCaption != null && uriCaption.length() > 0 )
        {
            request.addBodyParameter( "caption" ,
                                      uriCaption );
        }

        if ( uriDescription != null && uriDescription.length() > 0 )
        {
            request.addBodyParameter( "description" ,
                                      uriDescription );
        }

        Response response = sendSignedRequest( request );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "id" ).asText();
    }

    public boolean likePost( String postID )
            throws IOException , FacebookException
    {
        if ( postID == null || postID.length() <= 0 )
        {
            throw new NullPointerException();
        }

        OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                 "https://graph.facebook.com/" + postID + "/likes" );

        Response response = sendSignedRequest( request );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.asBoolean();
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( FacebookClient.class );

    private void checkErrors( Response response ,
                              JsonNode root )
            throws FacebookException
    {
        JsonNode error = root.get( "error" );
        if ( error != null )
        {
            throw new FacebookException( response.getCode() ,
                                         error.get( "code" ).asInt() ,
                                         error.get( "type" ).asText() ,
                                         error.get( "message" ).asText() );
        }
    }
}
