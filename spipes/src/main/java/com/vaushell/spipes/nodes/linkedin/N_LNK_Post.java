/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.linkedin;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import com.vaushell.spipes.nodes.twitter.N_TW_Post;
import com.vaushell.spipes.tools.HTMLhelper;
import com.vaushell.spipes.tools.scribe.OAuthException;
import com.vaushell.spipes.tools.scribe.linkedin.LinkedInClient;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_LNK_Post
        extends A_Node
{
    // PUBLIC
    public N_LNK_Post()
    {
        this.client = new LinkedInClient();
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
            throws InterruptedException , IOException , OAuthException
    {
        // Receive
        Object message = getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        // Convert if possible
        LNK_Post post;
        if ( message == null )
        {
            post = null;
        }
        else
        {
            if ( message instanceof LNK_Post )
            {
                post = (LNK_Post) message;
            }
            else if ( message instanceof News )
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

        // Send to Twitter
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] send update to LinkedIn : " + post );
        }

        String uri;
        if ( post.getURI() != null )
        {
            uri = post.getURI().toString();
        }
        else
        {
            uri = null;
        }

        String ID = client.updateStatus( post.getMessage() ,
                                         uri ,
                                         post.getUriName() ,
                                         post.getUriDescription() );

        post.setID( ID );

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        sendMessage( post );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_TW_Post.class );
    private LinkedInClient client;

    private static LNK_Post convertFromNews( News news )
    {
        if ( news.getURI() == null )
        {
            throw new NullPointerException( "URI can not be null" );
        }

        return new LNK_Post( null ,
                             news.getURI() ,
                             HTMLhelper.cleanHTML( news.getTitle() ) ,
                             " " );
    }
}
