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

import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        super();

        this.client = new FacebookClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = Paths.get( getMainConfig( "datas-directory" ) ,
                                          getNodeID() ,
                                          "token" );

        client.login( getConfig( "key" ) ,
                      getConfig( "secret" ) ,
                      "publish_stream" ,
                      tokenPath ,
                      "[" + getClass().getName() + " / " + getNodeID() + "]" );
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

        if ( !message.contains( "uri" ) )
        {
            throw new IllegalArgumentException( "message doesn't have an uri" );
        }

        // Send to FB
        final URI uri = (URI) message.getProperty( "uri" );
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
        if ( message.contains( "uri-source" ) )
        {
            caption = ( (URI) message.getProperty( "uri-source" ) ).getHost();
        }
        else
        {
            caption = null;
        }

        final String ID = client.post( null ,
                                       uriStr ,
                                       (String) message.getProperty( "title" ) ,
                                       caption ,
                                       (String) message.getProperty( "description" ) );

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
