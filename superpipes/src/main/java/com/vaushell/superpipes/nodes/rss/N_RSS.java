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

package com.vaushell.superpipes.nodes.rss;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.dispatch.Tags;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.tools.HTMLhelper;
import java.net.URI;
import java.net.URL;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Duration;
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
        super( new Duration( 600000L ) ,
               null );
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
        final URL url = new URL( getConfig( "url" ,
                                            false ) );

        final int max = Integer.parseInt( getConfig( "max" ,
                                                     false ) );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] read feed : " + url + " with max " + max + " elements" );
        }

        final SyndFeedInput input = new SyndFeedInput();
        final SyndFeed feed = input.build( new XmlReader( url ) );

        int count = 0;
        final List<SyndEntry> entries = feed.getEntries();
        for ( final SyndEntry entry : entries )
        {
            if ( entry.getUri() != null )
            {
                // Tags
                final Tags tags = new Tags();

                final List<SyndCategory> categories = entry.getCategories();
                if ( categories != null )
                {
                    for ( final SyndCategory category : categories )
                    {
                        tags.add( category.getName() );
                    }
                }

                setMessage( Message.create(
                    Message.KeyIndex.URI ,
                    new URI( entry.getUri() ) ,
                    Message.KeyIndex.TAGS ,
                    tags
                ) );

                // Title
                if ( entry.getTitle() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.TITLE ,
                                              HTMLhelper.cleanHTML( entry.getTitle() ) );
                }

                // Description
                if ( entry.getDescription() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.DESCRIPTION ,
                                              HTMLhelper.cleanHTML( entry.getDescription().getValue() ) );
                }

                // Author
                if ( entry.getAuthor() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.AUTHOR ,
                                              entry.getAuthor() );
                }

                // Published date
                if ( entry.getPublishedDate() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.PUBLISHED_DATE ,
                                              new DateTime( entry.getPublishedDate() ) );
                }

                sendMessage();
            }

            ++count;

            if ( count >= max )
            {
                break;
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
