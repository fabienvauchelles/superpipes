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
import com.vaushell.shaarlijavaapi.ShaarliLink;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import com.vaushell.spipes.tools.HTMLhelper;
import java.net.URI;
import java.net.URISyntaxException;
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

    @Override
    protected void loop()
        throws Exception
    {
        // Receive
        final Object message = getLastMessageOrWait();

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        // Convert if possible
        final ShareLink link;
        if ( message == null )
        {
            link = null;
        }
        else
        {
            if ( message instanceof ShareLink )
            {
                link = (ShareLink) message;
            }
            else if ( message instanceof News )
            {
                link = convertFromNews( (News) message );
            }
            else
            {
                link = null;
            }
        }

        if ( link == null )
        {
            throw new IllegalArgumentException( "message type is unknown : " + message.getClass().getName() );
        }

        // Send to Shaarli
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] send post to shaarli : " + link );
        }

        // Log in
        if ( !client.login( getConfig( "login" ) ,
                            getConfig( "password" ) ) )
        {
            throw new IllegalArgumentException( "Login error" );
        }

        final String ID = client.createOrUpdateLink( link.getURI().toString() ,
                                                     link.getTitle() ,
                                                     link.getDescription() ,
                                                     link.getTags() ,
                                                     false );

        link.setID( ID );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        sendMessage( link );
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

    private ShareLink convert( final ShaarliLink entry )
        throws URISyntaxException
    {
        if ( entry == null || entry.getUrl() == null || entry.getTitle() == null || ( entry.getID() == null && entry.getPermaID() == null ) )
        {
            return null;
        }

        String ID;
        if ( entry.getID() == null )
        {
            ID = entry.getPermaID();
        }
        else
        {
            ID = entry.getID();
        }

        return ShareLink.create( ID ,
                                 entry.getTitle() ,
                                 entry.getDescription() ,
                                 new URI( entry.getUrl() ) ,
                                 null ,
                                 new URI( entry.getPermaURL( client.getEndpoint() ) ) ,
                                 entry.getTags() );
    }

    private static ShareLink convertFromNews( final News news )
    {
        if ( news.getTitle() == null || news.getURI() == null || news.getTags() == null )
        {
            throw new IllegalArgumentException();
        }

        return ShareLink.create( HTMLhelper.cleanHTML( news.getTitle() ) ,
                                 HTMLhelper.cleanHTML( news.getDescription() ) ,
                                 news.getURI() ,
                                 news.getURIsource() ,
                                 news.getTags() );
    }
}
