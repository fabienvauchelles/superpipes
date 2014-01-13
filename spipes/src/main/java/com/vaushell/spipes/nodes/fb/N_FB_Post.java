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

package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post a message to Facebook.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_FB_Post
    extends A_Node
{
    // PUBLIC
    public N_FB_Post()
    {
        super( null ,
               DEFAULT_ANTIBURST );

        this.client = new FacebookClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = getDispatcher().getDatas().resolve( Paths.get( getNodeID() ,
                                                                              "token" ) );

        final String pageName = getConfig( "pagename" );
        if ( pageName == null )
        {
            final String userID = getConfig( "userid" );
            if ( userID == null )
            {
                client.login( getConfig( "key" ) ,
                              getConfig( "secret" ) ,
                              tokenPath ,
                              getDispatcher().getVCodeFactory().create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
            }
            else
            {
                client.loginAsOtherUser( userID ,
                                         getConfig( "key" ) ,
                                         getConfig( "secret" ) ,
                                         tokenPath ,
                                         getDispatcher().getVCodeFactory().create(
                    "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
            }
        }
        else
        {
            client.loginAsPage( pageName ,
                                getConfig( "key" ) ,
                                getConfig( "secret" ) ,
                                tokenPath ,
                                getDispatcher().getVCodeFactory().
                create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
        }
    }

    @Override
    protected void loop()
        throws Exception
    {
        // Receive
        final Message message = getLastMessageOrWait();

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        if ( !message.contains( Message.KeyIndex.URI ) )
        {
            throw new IllegalArgumentException( "message doesn't have an uri" );
        }

        // Send to FB
        final URI uri = (URI) message.getProperty( Message.KeyIndex.URI );
        String uriStr;
        if ( uri == null )
        {
            uriStr = null;
        }
        else
        {
            uriStr = uri.toString();
        }

        final String caption;
        if ( message.contains( Message.KeyIndex.URI_SOURCE ) )
        {
            caption = ( (URI) message.getProperty( Message.KeyIndex.URI_SOURCE ) ).getHost();
        }
        else
        {
            caption = null;
        }

        final DateTime date;
        if ( "true".equals( getConfig( "backdating" ) ) )
        {
            date = (DateTime) message.getProperty( Message.KeyIndex.PUBLISHED_DATE );
        }
        else
        {
            date = null;
        }

        final String ID = client.postLink( null ,
                                           uriStr ,
                                           (String) message.getProperty( Message.KeyIndex.TITLE ) ,
                                           caption ,
                                           (String) message.getProperty( Message.KeyIndex.DESCRIPTION ) ,
                                           date );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        message.setProperty( "id-facebook" ,
                             ID );

        sendMessage( message );
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_FB_Post.class );
    private final FacebookClient client;
}
