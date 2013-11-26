/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools.scribe.tumblr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.spipes.tools.scribe.A_OAuthClient;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class TumblrClient
        extends A_OAuthClient
{
    // PUBLIC
    public TumblrClient()
    {
        super();

        this.blogname = null;
    }

    public void login( String blogname ,
                       String key ,
                       String secret ,
                       Path tokenPath ,
                       String loginText )
            throws IOException
    {
        if ( blogname == null )
        {
            throw new NullPointerException();
        }

        this.blogname = blogname;

        loginImpl( TumblrApi.class ,
                   key ,
                   secret ,
                   null ,
                   "http://www.tumblr.com/connect/login_success.html" ,
                   true ,
                   tokenPath ,
                   loginText );
    }

    public long postLink( String message ,
                          String uri ,
                          String uriName ,
                          String uriDescription ,
                          Set<String> tags )
            throws IOException , TumblrException
    {
        if ( uri == null || uri.length() <= 0 || tags == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] post() : message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriDescription=" + uriDescription + " / tags=" + tags );
        }

        OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                 "http://api.tumblr.com/v2/blog/" + blogname + "/post" );

        request.addBodyParameter( "type" ,
                                  "link" );

        if ( uriName != null && uriName.length() > 0 )
        {
            request.addBodyParameter( "title" ,
                                      uriName );
        }

        if ( uriDescription != null && uriDescription.length() > 0 )
        {
            request.addBodyParameter( "description" ,
                                      uriDescription );
        }

        request.addBodyParameter( "url" ,
                                  uri );

        if ( tags.size() > 0 )
        {
            TreeSet<String> correctedTags = new TreeSet<>();
            for ( String tag : tags )
            {
                String correctedTag = tag.toLowerCase();
                correctedTags.add( correctedTag );
            }

            StringBuilder sbTags = new StringBuilder();
            for ( String tag : correctedTags )
            {
                if ( sbTags.length() > 0 )
                {
                    sbTags.append( "," );
                }

                sbTags.append( tag );
            }

            request.addBodyParameter( "tags" ,
                                      sbTags.toString() );
        }

        Response response = sendSignedRequest( request );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        JsonNode nodeResponse = node.get( "response" );

        return nodeResponse.get( "id" ).asLong();
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( FacebookClient.class );
    private String blogname;

    private void checkErrors( Response response ,
                              JsonNode root )
            throws TumblrException
    {
        JsonNode res = root.get( "response" );
        if ( response.getCode() != 201 )
        {
            JsonNode meta = root.get( "meta" );

            List<String> listErrors = new ArrayList<>();
            JsonNode errors = res.get( "errors" );
            if ( errors != null )
            {
                for ( JsonNode error : errors )
                {
                    listErrors.add( error.asText() );
                }
            }

            throw new TumblrException( response.getCode() ,
                                       meta.get( "status" ).asInt() ,
                                       meta.get( "msg" ).asText() ,
                                       listErrors );
        }
    }
}
