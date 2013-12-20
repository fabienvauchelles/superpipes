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

package com.vaushell.spipes.tools.scribe.tumblr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.spipes.tools.scribe.OAuthClient;
import com.vaushell.spipes.tools.scribe.code.I_ValidationCode;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import org.scribe.builder.api.TumblrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tumblr client.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TumblrClient
    extends OAuthClient
{
    // PUBLIC
    public TumblrClient()
    {
        super();
    }

    /**
     * Log in.
     *
     * @param blogname Blog name
     * @param key OAuth key
     * @param secret OAuth secret
     * @param tokenPath Path to save the token
     * @param vCode How to get the verification code
     * @throws IOException
     * @throws java.lang.InterruptedException
     */
    public void login( final String blogname ,
                       final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final I_ValidationCode vCode )
        throws IOException , InterruptedException
    {
        if ( blogname == null )
        {
            throw new IllegalArgumentException();
        }

        this.blogname = blogname;

        loginImpl( TumblrApi.class ,
                   key ,
                   secret ,
                   null ,
                   "http://www.tumblr.com/connect/login_success.html" ,
                   true ,
                   tokenPath ,
                   vCode );
    }

    /**
     * Post link to tumblr.
     *
     * @param message Message
     * @param uri Link
     * @param uriName Link's name
     * @param uriDescription Link's description
     * @param tags Link's set of tags
     * @return Post ID
     * @throws IOException
     * @throws TumblrException
     */
    public long postLink( final String message ,
                          final String uri ,
                          final String uriName ,
                          final String uriDescription ,
                          final Set<String> tags )
        throws IOException , TumblrException
    {
        if ( uri == null || uri.length() <= 0 )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] post() : message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriDescription=" + uriDescription + " / tags=" + tags );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "http://api.tumblr.com/v2/blog/" + blogname + "/post" );

        request.addBodyParameter( "type" ,
                                  "link" );

        request.addBodyParameter( "url" ,
                                  uri );

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

        if ( tags != null && !tags.isEmpty() )
        {
            final TreeSet<String> correctedTags = new TreeSet<>();
            for ( final String tag : tags )
            {
                final String correctedTag = tag.toLowerCase( Locale.ENGLISH );
                correctedTags.add( correctedTag );
            }

            final StringBuilder sbTags = new StringBuilder();
            for ( final String tag : correctedTags )
            {
                if ( sbTags.length() > 0 )
                {
                    sbTags.append( ',' );
                }

                sbTags.append( tag );
            }

            request.addBodyParameter( "tags" ,
                                      sbTags.toString() );
        }

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        final JsonNode nodeResponse = node.get( "response" );

        return nodeResponse.get( "id" ).asLong();
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( FacebookClient.class );
    private String blogname;

    private void checkErrors( final Response response ,
                              final JsonNode root )
        throws TumblrException
    {
        final JsonNode res = root.get( "response" );
        if ( response.getCode() != 201 )
        {
            final JsonNode meta = root.get( "meta" );

            final List<String> listErrors = new ArrayList<>();
            final JsonNode errors = res.get( "errors" );
            if ( errors != null )
            {
                for ( final JsonNode error : errors )
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
