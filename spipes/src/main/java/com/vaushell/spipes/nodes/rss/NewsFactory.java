/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.rss;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public enum NewsFactory
{
    // PUBLIC
    INSTANCE();

    public News create( String title ,
                        String description ,
                        String url ,
                        String author ,
                        String content ,
                        Set<String> tags ,
                        Date date )
    {
        if ( title == null || title.length() <= 0 || url == null || url.length() <= 0 )
        {
            throw new NullPointerException( "Title and URL can not be null" );
        }

        if ( tags == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] create : title=" + title + " / description=" + description + " / url=" + url + " / url=" + url + " / author=" + author + " / tags.size()=" + tags.
                    size() );
        }

        // Calculate ID
        StringBuilder sb = new StringBuilder();
        sb.append( title );

        if ( description != null && description.length() > 0 )
        {
            sb.append( description );
        }

        sb.append( url );

        if ( author != null && author.length() > 0 )
        {
            sb.append( author );
        }

        if ( content != null && content.length() > 0 )
        {
            sb.append( content );
        }

        if ( date != null )
        {
            sb.append( date.toString() );
        }

        TreeSet<String> correctedTags = new TreeSet<>();
        for ( String tag : tags )
        {
            String correctedTag = tag.toLowerCase();

            correctedTags.add( correctedTag );
            sb.append( correctedTag );
        }

        String ID = DigestUtils.md5Hex( sb.toString() );

        return new News( ID ,
                         title ,
                         description ,
                         url ,
                         author ,
                         content ,
                         correctedTags ,
                         date );
    }
    private final static Logger logger = LoggerFactory.getLogger( NewsFactory.class );
}
