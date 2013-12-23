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
import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.HTMLhelper;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
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
        // Read every 10 minutes
        super( 600000L ,
               0L );
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
            if ( entry.getUri() != null )
            {
                // Tags
                final TreeSet<String> tags = new TreeSet<>();

                final List<SyndCategory> categories = entry.getCategories();
                if ( categories != null )
                {
                    for ( final SyndCategory category : categories )
                    {
                        tags.add( category.getName().toLowerCase( Locale.ENGLISH ) );
                    }
                }

                final Message message = Message.create(
                    Message.KeyIndex.URI ,
                    new URI( entry.getUri() ) ,
                    Message.KeyIndex.TAGS ,
                    tags
                );

                // Title
                if ( entry.getTitle() != null )
                {
                    message.setProperty( Message.KeyIndex.TITLE ,
                                         HTMLhelper.cleanHTML( entry.getTitle() ) );
                }

                // Description
                if ( entry.getDescription() != null )
                {
                    message.setProperty( Message.KeyIndex.DESCRIPTION ,
                                         HTMLhelper.cleanHTML( entry.getDescription().getValue() ) );
                }

                // Author
                if ( entry.getAuthor() != null )
                {
                    message.setProperty( Message.KeyIndex.AUTHOR ,
                                         entry.getAuthor() );
                }

                // Published date
                if ( entry.getPublishedDate() != null )
                {
                    message.setProperty( Message.KeyIndex.PUBLISHED_DATE ,
                                         entry.getPublishedDate().getTime() );
                }

                // Content
                final List<SyndContent> scontents = entry.getContents();
                if ( scontents != null )
                {
                    final StringBuilder sb = new StringBuilder();
                    for ( final SyndContent scontent : scontents )
                    {
                        sb.append( scontent.getValue() );
                    }

                    if ( sb.length() > 0 )
                    {
                        message.setProperty( Message.KeyIndex.CONTENT ,
                                             sb.toString() );
                    }
                }

                sendMessage( message );
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
}
