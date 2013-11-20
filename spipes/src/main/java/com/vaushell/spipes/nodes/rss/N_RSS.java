/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.rss;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import com.vaushell.spipes.nodes.A_Node;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_RSS
        extends A_Node
{
    // PUBLIC
    public N_RSS()
    {
    }

    // PROTECTED
    @Override
    protected void prepare()
            throws Exception
    {
    }

    @Override
    protected void loop()
            throws URISyntaxException , IllegalArgumentException , FeedException , IOException
    {
        URL url = new URL( getConfig( "url" ) );

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] read feed : " + url );
        }

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build( new XmlReader( url ) );

        List<SyndEntry> entries = feed.getEntries();
        for ( SyndEntry entry : entries )
        {
            News news = convert( entry );

            sendMessage( news );
        }
    }

    @Override
    protected void terminate()
            throws Exception
    {
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_RSS.class );

    private static News convert( SyndEntry entry )
            throws URISyntaxException
    {
        String uriStr = entry.getUri();
        if ( uriStr == null )
        {
            return null;
        }

        String description;
        if ( entry.getDescription() != null )
        {
            description = entry.getDescription().getValue();
        }
        else
        {
            description = null;
        }

        StringBuilder sb = new StringBuilder();
        List<SyndContent> scontents = entry.getContents();
        if ( scontents != null )
        {
            for ( SyndContent scontent : scontents )
            {
                sb.append( scontent.getValue() );
            }
        }

        String content;
        if ( sb.length() > 0 )
        {
            content = sb.toString();
        }
        else
        {
            content = null;
        }

        HashSet<String> tags = new HashSet<>();

        List<SyndCategory> categories = entry.getCategories();
        if ( categories != null )
        {
            for ( SyndCategory category : categories )
            {
                tags.add( category.getName().toLowerCase() );
            }
        }

        return NewsFactory.INSTANCE.create( entry.getTitle() ,
                                            description ,
                                            new URI( uriStr ) ,
                                            entry.getAuthor() ,
                                            content ,
                                            tags ,
                                            entry.getPublishedDate() );
    }
}
