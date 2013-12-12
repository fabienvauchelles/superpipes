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
import com.vaushell.spipes.tools.scribe.A_OAuthClient;
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
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class LinkedInClient
    extends A_OAuthClient
{
    // PUBLIC
    public LinkedInClient()
    {
        super();
    }

    public void login( String key ,
                       String secret ,
                       Path tokenPath ,
                       String loginText )
        throws IOException
    {
        loginImpl( LinkedInApi.class ,
                   key ,
                   secret ,
                   "r_basicprofile,rw_nus" ,
                   null ,
                   true ,
                   tokenPath ,
                   loginText );
    }

    public String updateStatus( String message ,
                                String uri ,
                                String uriName ,
                                String uriDescription )
        throws IOException , OAuthException
    {
        if ( ( uri == null || uri.length() <= 0 ) && ( message == null || message.length() <= 0 ) )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                "[" + getClass().getSimpleName() + "] updateStatus() : message=" + message + " / uri=" + uri + " / uriName=" + uriName + " / uriDescription=" + uriDescription );
        }

        OAuthRequest request = new OAuthRequest( Verb.POST ,
                                                 "http://api.linkedin.com/v1/people/~/shares?format=json" );

        Element share = new Element( "share" );

        // Message
        if ( message != null && message.length() > 0 )
        {
            share.addContent( new Element( "comment" ).setText( message ) );
        }

        // Content
        if ( uri != null && uri.length() > 0 )
        {
            Element content = new Element( "content" );

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
        Element visiblity = new Element( "visibility" );
        visiblity.addContent( new Element( "code" ).setText( "anyone" ) );
        share.addContent( visiblity );

        String xmlPayload = new XMLOutputter( Format.getPrettyFormat() ).outputString( share );

        request.addHeader( "Content-Type" ,
                           "application/xml" );
        request.addPayload( xmlPayload );

        Response response = sendSignedRequest( request );

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return node.get( "updateKey" ).asText();
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( LinkedInClient.class );

    private void checkErrors( Response response ,
                              JsonNode root )
        throws LinkedInException
    {
        JsonNode error = root.get( "errorCode" );
        if ( error != null )
        {
            throw new LinkedInException( response.getCode() ,
                                         error.asInt() ,
                                         root.get( "message" ).asText() ,
                                         root.get( "status" ).asInt() );
        }
    }
}