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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.joda.time.DateTime;
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

        return extractID( node.get( "updateKey" ).asText() );
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

        return extractID( node.get( "updateKey" ).asText() );
    }

    /**
     * Read a LinkedIn status.
     *
     * @param ID Status's ID
     * @return the Status
     * @throws IOException
     * @throws LinkedInException
     */
    public LNK_Status readStatus( final String ID )
        throws IOException , LinkedInException
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
                                                       "http://api.linkedin.com/v1/people/~/network/updates/key=UNIU-" + ID + "-SHARE?type=SHAR&format=json" );

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        return convertJsonToStatus( node );
    }

    /**
     * Read a LinkedIn Feed.
     *
     * @param forcedTarget Target's ID. Could be null to use login target.
     * @param count Max status by call. Could be null to use default.
     * @return a list of status.
     * @throws IOException
     * @throws LinkedInException
     */
    public List<LNK_Status> readFeed( final String forcedTarget ,
                                      final Integer count )
        throws IOException , LinkedInException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] readFeed() : forcedTarget=" + forcedTarget + " / count=" + count );
        }

        final Properties properties = new Properties();
        properties.setProperty( "scope" ,
                                "self" );
        properties.setProperty( "type" ,
                                "SHAR" );
        properties.setProperty( "format" ,
                                "json" );
        if ( count != null )
        {
            properties.setProperty( "count" ,
                                    Integer.toString( count ) );
        }

        if ( forcedTarget == null )
        {
            return readFeedImpl( "http://api.linkedin.com/v1/people/~/network/updates" ,
                                 properties );
        }
        else
        {
            return readFeedImpl( "http://api.linkedin.com/v1/people/id=" + forcedTarget + "/network/updates" ,
                                 properties );
        }
    }

    /**
     * Iterate a LinkedIn Feed.
     *
     * @param forcedTarget Target's ID. Could be null to use login target.
     * @param count Max status by call. Could be null to use default.
     * @return a status iterator.
     */
    public Iterator<LNK_Status> iteratorFeed( final String forcedTarget ,
                                              final Integer count )
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] iteratorFeed() : forcedTarget=" + forcedTarget + " / count=" + count );
        }

        return new Iterator<LNK_Status>()
        {
            @Override
            public boolean hasNext()
            {
                try
                {
                    if ( bufferCursor < buffer.size() )
                    {
                        return true;
                    }
                    else
                    {
                        buffer.clear();
                        bufferCursor = 0;

                        final String url;
                        final Properties properties = new Properties();
                        properties.setProperty( "scope" ,
                                                "self" );
                        properties.setProperty( "type" ,
                                                "SHAR" );
                        properties.setProperty( "format" ,
                                                "json" );
                        if ( count != null )
                        {
                            properties.setProperty( "count" ,
                                                    Integer.toString( count ) );
                        }

                        if ( forcedTarget == null )
                        {
                            url = "http://api.linkedin.com/v1/people/~/network/updates";
                        }
                        else
                        {
                            url = "http://api.linkedin.com/v1/people/id=" + forcedTarget + "/network/updates";
                        }

                        if ( lastTimestamp != null )
                        {
                            properties.setProperty( "before" ,
                                                    Long.toString( lastTimestamp.getMillis() - 1L ) );
                        }

                        final List<LNK_Status> posts = readFeedImpl( url ,
                                                                     properties );
                        if ( posts.isEmpty() )
                        {
                            return false;
                        }
                        else
                        {
                            lastTimestamp = posts.get( posts.size() - 1 ).getTimestamp();

                            buffer.addAll( posts );

                            return true;
                        }
                    }
                }
                catch( final LinkedInException |
                             IOException ex )
                {
                    throw new RuntimeException( ex );
                }
            }

            @Override
            public LNK_Status next()
            {
                return buffer.get( bufferCursor++ );
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            // PRIVATE
            private final List<LNK_Status> buffer = new ArrayList<>();
            private int bufferCursor;
            private DateTime lastTimestamp;
        };
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( LinkedInClient.class );
    private static final Pattern PATTERN_ID = Pattern.compile( "\\d+-\\d+" );

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

    private String extractID( final String fullID )
    {
        if ( fullID == null )
        {
            return null;
        }

        final Matcher m = PATTERN_ID.matcher( fullID );
        if ( m.find() )
        {
            return m.group();
        }
        else
        {
            return null;
        }
    }

    private List<LNK_Status> readFeedImpl( final String url ,
                                           final Properties properties )
        throws IOException , LinkedInException
    {
        if ( url == null || properties == null )
        {
            throw new IllegalArgumentException();
        }

        final OAuthRequest request = new OAuthRequest( Verb.GET ,
                                                       url );

        for ( final Map.Entry<Object , Object> entry : properties.entrySet() )
        {
            request.addQuerystringParameter( (String) entry.getKey() ,
                                             (String) entry.getValue() );
        }

        final Response response = sendSignedRequest( request );

        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode node = (JsonNode) mapper.readTree( response.getStream() );

        checkErrors( response ,
                     node );

        final List<LNK_Status> status = new ArrayList<>();

        final JsonNode nValues = node.get( "values" );
        if ( nValues != null && nValues.size() > 0 )
        {
            for ( final JsonNode nValue : nValues )
            {
                status.add( convertJsonToStatus( nValue ) );
            }
        }

        return status;
    }

    private LNK_Status convertJsonToStatus( final JsonNode node )
    {
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

        return new LNK_Status( extractID( node.get( "updateKey" ).asText() ) ,
                               convertNodeToString( nodeCurrent.get( "comment" ) ) ,
                               submittedUrl ,
                               shortenedUrl ,
                               title ,
                               description ,
                               new LNK_User( nodeAuthor.get( "id" ).asText() ,
                                             convertNodeToString( nodeAuthor.get( "firstName" ) ) ,
                                             convertNodeToString( nodeAuthor.get( "lastName" ) ) ,
                                             convertNodeToString( nodeAuthor.get( "headline" ) ) ) ,
                               new DateTime( nodeCurrent.get( "timestamp" ).asLong() ) );
    }
}
