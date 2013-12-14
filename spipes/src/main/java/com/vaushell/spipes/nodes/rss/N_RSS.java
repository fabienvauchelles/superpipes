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

package com.vaushell.spipes.nodes.rss;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.vaushell.spipes.nodes.A_Node;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a RSS feed.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_RSS
    extends A_Node
{
    // PUBLIC
    public N_RSS()
    {
        super();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        // Nothing
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected void loop()
        throws Exception
    {
        final URL url = new URL( getConfig( "url" ) );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] read feed : " + url );
        }

        final SyndFeedInput input = new SyndFeedInput();
        final SyndFeed feed = input.build( new XmlReader( url ) );

        final List<SyndEntry> entries = feed.getEntries();
        for ( final SyndEntry entry : entries )
        {
            final News news = convert( entry );
            if ( news != null )
            {
                sendMessage( news );
            }
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_RSS.class );

    @SuppressWarnings( "unchecked" )
    private static News convert( final SyndEntry entry )
        throws URISyntaxException
    {
        final String uriStr = entry.getUri();
        if ( uriStr == null )
        {
            return null;
        }

        final String description;
        if ( entry.getDescription() == null )
        {
            description = null;
        }
        else
        {
            description = entry.getDescription().getValue();
        }

        final StringBuilder sb = new StringBuilder();

        final List<SyndContent> scontents = entry.getContents();
        if ( scontents != null )
        {
            for ( final SyndContent scontent : scontents )
            {
                sb.append( scontent.getValue() );
            }
        }

        final String content;
        if ( sb.length() > 0 )
        {
            content = sb.toString();
        }
        else
        {
            content = null;
        }

        final HashSet<String> tags;

        final List<SyndCategory> categories = entry.getCategories();
        if ( categories == null )
        {
            tags = null;
        }
        else
        {
            tags = new HashSet<>();

            for ( final SyndCategory category : categories )
            {
                tags.add( category.getName().toLowerCase( Locale.ENGLISH ) );
            }
        }

        return News.create( entry.getTitle() ,
                            description ,
                            new URI( uriStr ) ,
                            new URI( uriStr ) ,
                            entry.getAuthor() ,
                            content ,
                            tags ,
                            entry.getPublishedDate() );
    }
}
