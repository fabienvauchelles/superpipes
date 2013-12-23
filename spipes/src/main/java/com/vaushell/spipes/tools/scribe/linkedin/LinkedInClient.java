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

package com.vaushell.spipes.tools.scribe.linkedin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaushell.spipes.tools.scribe.OAuthClient;
import com.vaushell.spipes.tools.scribe.OAuthException;
import com.vaushell.spipes.tools.scribe.code.A_ValidatorCode;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LinkedIn client. Remark: Delete a share is not implemented in LinkedIn API
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class LinkedInClient
    extends OAuthClient
{
    // PUBLIC
    public LinkedInClient()
    {
        super();
    }

    /**
     * Log in.
     *
     * @param key OAuth keyd
     * @param secret OAuth secret
     * @param tokenPath Path to save the token
     * @param vCode How to get the verification code
     * @throws IOException
     * @throws java.lang.InterruptedException
     */
    public void login( final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final A_ValidatorCode vCode )
        throws IOException , InterruptedException
    {
        loginImpl( LinkedInApi.class ,
                   key ,
                   secret ,
                   "r_basicprofile,rw_nus" ,
                   "http://www.linkedin.com/connect/login_success.html" ,
                   true ,
                   tokenPath ,
                   vCode );
    }

    /**
     * Post link.
     *
     * @param message Status's message
     * @param uri Status's link
     * @param uriName Link's name
     * @param uriDescription Link's description
     * @return Status ID
     * @throws IOException
     * @throws OAuthException
     */
    public String postLink( final String message ,
                            final String uri ,
                            final String uriName ,
                            final String uriDescription )
        throws IOException , OAuthException
    {
        if ( uri == null || uri.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] updateStatus() : message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriDescription=" + uriDescription );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "http://api.linkedin.com/v1/people/~/shares?format=json" );

        final Element share = new Element( "share" );

        // Message
        if ( message != null && message.length() > 0 )
        {
            share.addContent( new Element( "comment" ).setText( message ) );
        }

        // Content
        final Element content = new Element( "content" );

        if ( uriName != null && uriName.length() > 0 )
        {
            content.addContent( new Element( "title" ).setText( uriName ) );
        }

        if ( uriDescription != null && uriDescription.length() > 0 )
        {
            content.addContent( new Element( "description" ).setText( uriDescription ) );
        }

        content.addContent( new Element( "submitted-url" ).setText( uri ) );

        share.addContent( content );

        // Visiblity
        final Element visiblity = new Element( "visibility" );
        visiblity.addContent( new Element( "code" ).setText( "anyone" ) );
        share.addContent( visiblity );

        final String xmlPayload = new XMLOutputter( Format.getPrettyFormat() ).outputString( share );

        request.addHeader( "Content-Type" ,
                           "application/xml" );
        request.addPayload( xmlPayload );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "updateKey" ).asText();
    }

    /**
     * Post message.
     *
     * @param message Status's message
     * @return Status ID
     * @throws IOException
     * @throws LinkedInException
     */
    public String postMessage( final String message )
        throws IOException , LinkedInException
    {
        if ( message == null || message.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] updateStatus() : message=" + message );
        }

        final OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                       "http://api.linkedin.com/v1/people/~/shares?format=json" );

        final Element share = new Element( "share" );

        // Message
        share.addContent( new Element( "comment" ).setText( message ) );

        // Visiblity
        final Element visiblity = new Element( "visibility" );
        visiblity.addContent( new Element( "code" ).setText( "anyone" ) );
        share.addContent( visiblity );

        final String xmlPayload = new XMLOutputter( Format.getPrettyFormat() ).outputString( share );

        request.addHeader( "Content-Type" ,
                           "application/xml" );
        request.addPayload( xmlPayload );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "updateKey" ).asText();
    }

    /**
     * Read a LinkedIn status.
     *
     * @param ID Status's ID
     * @return the Status
     * @throws IOException
     * @throws LinkedInException
     * @throws URISyntaxException
     */
    public LNK_Status readStatus( final String ID )
        throws IOException , LinkedInException , URISyntaxException
    {
        if ( ID == null || ID.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readStatus() : ID=" + ID );
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       "http://api.linkedin.com/v1/people/~/network/updates/key=" + ID + "?type=SHAR&format=json" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        final JsonNode nodeCurrent = node.get( "updateContent" ).get( "person" ).get( "currentShare" );
        final JsonNode nodeAuthor = nodeCurrent.get( "author" );

        final JsonNode nodeContent = nodeCurrent.get( "content" );
        final String submittedUrl;
        final String shortenedUrl;
        final String title;
        final String description;
        if ( nodeContent == null )
        {
            submittedUrl = null;
            shortenedUrl = null;
            title = null;
            description = null;
        }
        else
        {
            submittedUrl = convertNodeToString( nodeContent.get( "submittedUrl" ) );
            shortenedUrl = convertNodeToString( nodeContent.get( "shortenedUrl" ) );
            title = convertNodeToString( nodeContent.get( "title" ) );
            description = convertNodeToString( nodeContent.get( "description" ) );
        }

        return new LNK_Status( node.get( "updateKey" ).asText() ,
                               convertNodeToString( nodeCurrent.get( "comment" ) ) ,
                               submittedUrl ,
                               shortenedUrl ,
                               title ,
                               description ,
                               new LNK_User( nodeAuthor.get( "id" ).asText() ,
                                             convertNodeToString( nodeAuthor.get( "firstName" ) ) ,
                                             convertNodeToString( nodeAuthor.get( "lastName" ) ) ,
                                             convertNodeToString( nodeAuthor.get( "headline" ) ) ) ,
                               nodeCurrent.get( "timestamp" ).asLong() );
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( LinkedInClient.class );

    private void checkErrors( final Response response ,
                              final JsonNode root )
        throws LinkedInException
    {
        final JsonNode error = root.get( "errorCode" );
        if ( error != null )
        {
            throw new LinkedInException( response.getCode() ,
                                         error.asInt() ,
                                         root.get( "message" ).asText() ,
                                         root.get( "status" ).asInt() );
        }
    }
}
