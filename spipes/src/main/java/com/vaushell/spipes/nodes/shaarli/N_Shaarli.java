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
import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import java.net.URI;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
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
            final ShaarliLink sl = it.next();

            if ( sl.getUrl() != null
                 && sl.getTitle() != null
                 && ( sl.getID() != null || sl.getPermaID() != null ) )
            {
                final Message message = new Message();

                // URI
                message.setProperty( "uri" ,
                                     new URI( sl.getUrl() ) );

                // Title
                message.setProperty( "title" ,
                                     sl.getTitle() );

                // Description
                message.setProperty( "description" ,
                                     sl.getDescription() );

                // Shaarli ID
                message.setProperty( "id-shaarli" ,
                                     sl.getID() );

                // Permanent ID
                message.setProperty( "id-permanent" ,
                                     sl.getPermaID() );

                // Permanent URI
                message.setProperty( "uri-permanent" ,
                                     sl.getPermaURL( client.getEndpoint() ) );

                // Tags
                final TreeSet<String> tags = new TreeSet<>();
                for ( final String tag : sl.getTags() )
                {
                    tags.add( tag.toLowerCase( Locale.ENGLISH ) );
                }
                message.setProperty( "tags" ,
                                     tags );

                sendMessage( message );
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
}
