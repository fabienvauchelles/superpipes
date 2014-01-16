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
import com.vaushell.spipes.dispatch.Tags;
import com.vaushell.spipes.tools.scribe.OAuthClient;
import com.vaushell.spipes.tools.scribe.code.A_ValidatorCode;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
                       final A_ValidatorCode vCode )
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
     * Post link to Tumblr.
     *
     * @param uri Link
     * @param uriName Link's name
     * @param uriDescription Link's description
     * @param tags Link's tags
     * @param date Force message's date
     * @return Post ID
     * @throws IOException
     * @throws TumblrException
     */
    public long postLink( final String uri ,
                          final String uriName ,
                          final String uriDescription ,
                          final Tags tags ,
                          final DateTime date )
        throws IOException , TumblrException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] postLink() : uri=" + uri + " / uriName=" + uriName + " / uriDescription=" + uriDescription + " / tags=" + tags + " / date=" + date );
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
            final StringBuilder sbTags = new StringBuilder();
            for ( final String tag : tags.getAll() )
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

        if ( date != null )
        {
            request.addBodyParameter( "date" ,
                                      FORMAT_DATE.print( date ) );
        }

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node ,
                     201 );

        final JsonNode nodeResponse = node.get( "response" );

        return nodeResponse.get( "id" ).asLong();
    }

    /**
     * Post message to Tumblr.
     *
     * @param message Message
     * @param tags Message's tags
     * @param date Force message's date
     * @return Post ID
     * @throws IOException
     * @throws TumblrException
     */
    public long postMessage( final String message ,
                             final Tags tags ,
                             final DateTime date )
        throws IOException , TumblrException
    {
        if ( message == null || message.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] postMessage() : message=" + message + " / tags=" + tags + " / date=" + date );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "http://api.tumblr.com/v2/blog/" + blogname + "/post" );

        request.addBodyParameter( "type" ,
                                  "text" );

        request.addBodyParameter( "title" ,
                                  message );

        if ( tags != null && !tags.isEmpty() )
        {

            final StringBuilder sbTags = new StringBuilder();
            for ( final String tag : tags.getAll() )
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

        if ( date != null )
        {
            request.addBodyParameter( "date" ,
                                      FORMAT_DATE.print( date ) );
        }

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node ,
                     201 );

        final JsonNode nodeResponse = node.get( "response" );

        return nodeResponse.get( "id" ).asLong();
    }

    /**
     * Read a Tumblr Post.
     *
     * @param ID Post ID
     * @return the Post
     * @throws IOException
     * @throws TumblrException
     */
    public TB_Post readPost( final long ID )
        throws IOException , TumblrException
    {
        if ( ID < 0 )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readPost() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       "http://api.tumblr.com/v2/blog/" + blogname + "/posts/?api_key=" + getKey() + "&filter=raw&id=" + Long.
            toString( ID ) );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node ,
                     200 );

        final JsonNode nodeResponse = node.get( "response" );

        final JsonNode nodePosts = nodeResponse.get( "posts" );
        if ( nodePosts == null || nodePosts.size() <= 0 )
        {
            return null;
        }
        final JsonNode nodePost = nodePosts.get( 0 );

        final Tags tags = new Tags();
        final JsonNode nodeTags = nodePost.get( "tags" );
        for ( final JsonNode nodeTag : nodeTags )
        {
            tags.add( nodeTag.asText() );
        }

        final String message;
        final String urlName;
        final String type = nodePost.get( "type" ).asText();
        if ( "link".equalsIgnoreCase( type ) )
        {
            message = null;
            urlName = convertNodeToString( nodePost.get( "title" ) );
        }
        else
        {
            message = convertNodeToString( nodePost.get( "title" ) );
            urlName = null;
        }

        final JsonNode nodeBlog = nodeResponse.get( "blog" );

        return new TB_Post( nodePost.get( "id" ).asLong() ,
                            message ,

                            convertNodeToString( nodePost.get( "url" ) ) ,
                            urlName ,
                            convertNodeToString( nodePost.get( "description" ) ) ,
                            type ,
                            nodePost.get( "slug" ).asText() ,
                            new DateTime( nodePost.get( "timestamp" ).asLong() * 1000L ) ,
                            tags ,
                            new TB_Blog( nodeBlog.get( "name" ).asText() ,
                                         convertNodeToString( nodeBlog.get( "title" ) ) ,
                                         convertNodeToString( nodeBlog.get( "description" ) ) ,
                                         convertNodeToString( nodeBlog.get( "url" ) ) ) );
    }

    /**
     * Delete a Tumblr Post.
     *
     * @param ID Post ID
     * @return True if successfull
     * @throws IOException
     * @throws TumblrException
     */
    public boolean deletePost( final long ID )
        throws IOException , TumblrException
    {
        if ( ID < 0 )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] deletePost() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "http://api.tumblr.com/v2/blog/" + blogname + "/post/delete" );

        request.addBodyParameter( "id" ,
                                  Long.toString( ID ) );

        final Response response = sendSignedRequest( request );

        return response.getCode() == 200;
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( FacebookClient.class );
    private String blogname;
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH:mm:ssZ" );

    private void checkErrors( final Response response ,
                              final JsonNode root ,
                              final int validResponseCode )
        throws TumblrException
    {
        final JsonNode res = root.get( "response" );
        if ( response.getCode() != validResponseCode )
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
