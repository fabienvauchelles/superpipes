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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a RSS feed.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_Shaarli
    extends A_Node
{
    // PUBLIC
    public N_Shaarli()
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
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] read feed " );
        }

        final int max = Integer.parseInt( getConfig( "max" ) );

        int count = 0;
        final Iterator<ShaarliLink> it = client.searchAllIterator();
        while ( it.hasNext() && count < max )
        {
            final ShareLink link = convert( it.next() );
            if ( link != null )
            {
                sendMessage( link );
            }

            ++count;
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_Shaarli.class );
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
}
