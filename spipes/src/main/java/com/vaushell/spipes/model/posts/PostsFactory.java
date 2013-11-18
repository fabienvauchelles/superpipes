/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.model.posts;

import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public enum PostsFactory
{
    // PUBLIC
    INSTANCE();

    public Post create( String message ,
                        String url ,
                        String urlName ,
                        String urlDescription ,
                        Set<String> tags )
    {
        if ( message == null && url == null )
        {
            throw new NullPointerException( "Message and URL can not be null" );
        }

        if ( tags == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] create : message=" + message + " / url=" + url + " / urlName=" + urlName + " / urlDescription=" + urlDescription + " tags.size()=" + tags.
                    size() );
        }

        // Calculate ID
        StringBuilder sb = new StringBuilder();

        if ( message != null && message.length() > 0 )
        {
            sb.append( message );
        }

        if ( url != null && url.length() > 0 )
        {
            sb.append( url );
        }

        if ( urlName != null && urlName.length() > 0 )
        {
            sb.append( urlName );
        }

        if ( urlDescription != null && urlDescription.length() > 0 )
        {
            sb.append( urlDescription );
        }

        for ( String tag : tags )
        {
            sb.append( tag );
        }

        String ID = DigestUtils.md5Hex( sb.toString() );

        return new Post( ID ,
                         message ,
                         url ,
                         urlName ,
                         urlDescription ,
                         tags );
    }
    private final static Logger logger = LoggerFactory.getLogger( PostsFactory.class );
}
