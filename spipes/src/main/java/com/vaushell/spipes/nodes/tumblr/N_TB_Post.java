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

package com.vaushell.spipes.nodes.tumblr;

import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.code.VC_SystemInput;
import com.vaushell.spipes.tools.scribe.tumblr.TumblrClient;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post a message to Tumblr.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TB_Post
    extends A_Node
{
    // PUBLIC
    public N_TB_Post()
    {
        super();

        this.client = new TumblrClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = Paths.get( getMainConfig( "datas-directory" ) ,
                                          getNodeID() ,
                                          "token" );

        client.login( getConfig( "blogname" ) ,
                      getConfig( "key" ) ,
                      getConfig( "secret" ) ,
                      tokenPath ,
                      new VC_SystemInput( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
    }

    @SuppressWarnings( "unchecked" )
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

        // Send to TB
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

        final long ID = client.postLink( uriStr ,
                                         (String) message.getProperty( Message.KeyIndex.TITLE ) ,
                                         (String) message.getProperty( Message.KeyIndex.DESCRIPTION ) ,
                                         (Set<String>) message.getProperty( Message.KeyIndex.TAGS ) );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        message.setProperty( "id-tumblr" ,
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
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TB_Post.class );
    private final TumblrClient client;
}
