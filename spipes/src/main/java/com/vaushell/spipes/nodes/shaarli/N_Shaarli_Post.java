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

package com.vaushell.spipes.nodes.shaarli;

import com.vaushell.shaarlijavaapi.ShaarliClient;
import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import java.net.URI;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a RSS feed.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_Shaarli_Post
    extends A_Node
{
    // PUBLIC
    public N_Shaarli_Post()
    {
        super();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        this.client = new ShaarliClient( getConfig( "url" ) );
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

        if ( !message.contains( Message.KeyIndex.URI )
             || !message.contains( Message.KeyIndex.TITLE )
             || !message.contains( Message.KeyIndex.TAGS ) )
        {
            throw new IllegalArgumentException( "message doesn't have an uri, a title or a set of tags" );
        }

        // Send to Shaarli
        // Log in
        if ( !client.login( getConfig( "login" ) ,
                            getConfig( "password" ) ) )
        {
            throw new IllegalArgumentException( "Login error" );
        }

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

        final String ID = client.createLink( uriStr ,
                                             (String) message.getProperty( Message.KeyIndex.TITLE ) ,
                                             (String) message.getProperty( Message.KeyIndex.DESCRIPTION ) ,
                                             (Set<String>) message.getProperty( Message.KeyIndex.TAGS ) ,
                                             false );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        message.setProperty( "id-shaarli" ,
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
    private static final Logger LOGGER = LoggerFactory.getLogger( N_Shaarli_Post.class );
    private ShaarliClient client;
}
