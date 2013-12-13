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
import java.io.IOException;
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
 * LinkedIn client.
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
     * @param loginText Prefix message to request the token to the user
     * @throws IOException
     */
    public void login( final String key ,
                       final String secret ,
                       final Path tokenPath ,
                       final String loginText )
        throws IOException
    {
        loginImpl( LinkedInApi.class ,
                   key ,
                   secret ,
                   "r_basicprofile,rw_nus" ,
                   "http://www.linkedin.com/connect/login_success.html" ,
                   true ,
                   tokenPath ,
                   loginText );
    }

    /**
     * Update status.
     *
     * @param message Status's message
     * @param uri Status's link
     * @param uriName Link's name
     * @param uriDescription Link's description
     * @return Status ID
     * @throws IOException
     * @throws OAuthException
     */
    public String updateStatus( final String message ,
                                final String uri ,
                                final String uriName ,
                                final String uriDescription )
        throws IOException , OAuthException
    {
        if ( ( uri == null || uri.length() <= 0 ) && ( message == null || message.length() <= 0 ) )
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
        if ( uri != null && uri.length() > 0 )
        {
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
        }

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
