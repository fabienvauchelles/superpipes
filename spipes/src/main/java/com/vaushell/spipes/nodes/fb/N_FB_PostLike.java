/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import com.vaushell.spipes.tools.scribe.fb.FacebookException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_FB_PostLike
        extends A_Node
{
    // PUBLIC
    public N_FB_PostLike()
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
            throws InterruptedException , IOException , FacebookException
    {
        // Receive
        FB_Post post = (FB_Post) getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive post and like it : " + post );
        }

        // Like
        client.likePost( post.getID() );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_FB_PostLike.class );
    private FacebookClient client;
}
