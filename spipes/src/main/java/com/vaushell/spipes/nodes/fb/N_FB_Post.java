/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import com.vaushell.spipes.tools.scribe.fb.FacebookException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_FB_Post
        extends A_Node
{
    // PUBLIC
    public N_FB_Post()
    {
        this.client = new FacebookClient();
    }

    @Override
    public void prepare()
            throws Exception
    {
        Path tokenPath = Paths.get( getMainConfig( "datas-directory" ) ,
                                    getNodeID() ,
                                    "token" );

        client.login( getConfig( "key" ) ,
                      getConfig( "secret" ) ,
                      "publish_stream" ,
                      tokenPath ,
                      "[" + getClass().getName() + " / " + getNodeID() + "]" );
    }

    @Override
    public void terminate()
            throws Exception
    {
    }

    // PROTECTED
    @Override
    protected void loop()
            throws InterruptedException , FacebookException , IOException
    {
        // Receive
        Object message = getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        // Convert if possible
        FB_Post post;
        if ( message == null )
        {
            post = null;
        }
        else
        {
            if ( message instanceof News )
            {
                post = convertFromNews( (News) message );
            }
            else
            {
                post = null;
            }
        }

        if ( post == null )
        {
            throw new IllegalArgumentException( "message type is unknown : " + message.getClass().getName() );
        }

        // Send to FB
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] send post to facebook : " + post );
        }

        String uri;
        if ( post.getURI() != null )
        {
            uri = post.getURI().toURL().toString();
        }
        else
        {
            uri = null;
        }

        String ID = client.post( post.getMessage() ,
                                 uri ,
                                 post.getURIname() ,
                                 null ,
                                 post.getURIdescription() );

        post.setID( ID );

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        sendMessage( post );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_FB_Post.class );
    private FacebookClient client;

    private static FB_Post convertFromNews( News news )
    {
        if ( ( news.getTitle() == null || news.getTitle().length() <= 0 )
             && ( news.getURI() == null ) )
        {
            throw new NullPointerException( "Title and URL can not be null" );
        }

        if ( news.getTags() == null )
        {
            throw new NullPointerException();
        }

        String title = cleanHTML( news.getTitle() );

        String description = cleanHTML( news.getDescription() );

        TreeSet<String> correctedTags = new TreeSet<>();
        for ( String tag : news.getTags() )
        {
            String correctedTag = tag.toLowerCase();

            correctedTags.add( correctedTag );
        }

        return new FB_Post( null ,
                            news.getURI() ,
                            title ,
                            description ,
                            correctedTags );
    }

    private static String cleanHTML( String s )
    {
        if ( s == null )
        {
            return null;
        }

        return StringEscapeUtils.unescapeHtml( s.replaceAll( "<[^>]+>" ,
                                                             "" ) );
    }
}
