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
import com.vaushell.shaarlijavaapi.ShaarliTemplates;
import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.nodes.A_Node;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
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
        // Read every 10 minutes
        super( new Duration( 600000L ) ,
               null );

        this.templates = new ShaarliTemplates();
    }

    @Override
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        super.load( cNode );

        final List<HierarchicalConfiguration> cTemplates = cNode.configurationsAt( "templates.template" );
        if ( cTemplates != null )
        {
            for ( final HierarchicalConfiguration cTemplate : cTemplates )
            {
                templates.add( cTemplate.getString( "[@key]" ) ,
                               cTemplate.getString( "[@csspath]" ) ,
                               cTemplate.getString( "[@attribut]" ) ,
                               cTemplate.getString( "[@regex]" ) );
            }
        }
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        this.client = new ShaarliClient( templates ,
                                         getConfig( "url" ) );
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
                // Tags
                final TreeSet<String> tags = new TreeSet<>();
                for ( final String tag : sl.getTags() )
                {
                    tags.add( tag.toLowerCase( Locale.ENGLISH ) );
                }

                final Message message = Message.create(
                    Message.KeyIndex.URI ,
                    new URI( sl.getUrl() ) ,
                    Message.KeyIndex.TITLE ,
                    sl.getTitle() ,
                    Message.KeyIndex.DESCRIPTION ,
                    sl.getDescription() ,
                    "id-shaarli" ,
                    sl.getID() ,
                    "id-permanent" ,
                    sl.getPermaID() ,
                    "uri-permanent" ,
                    sl.getPermaURL( client.getEndpoint() ) ,
                    Message.KeyIndex.TAGS ,
                    tags
                );

                final Date dt = client.convertIDstringToDate( sl.getID() );
                if ( dt != null )
                {
                    message.setProperty( Message.KeyIndex.PUBLISHED_DATE ,
                                         new DateTime( dt ) );
                }

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
    private final ShaarliTemplates templates;
}
