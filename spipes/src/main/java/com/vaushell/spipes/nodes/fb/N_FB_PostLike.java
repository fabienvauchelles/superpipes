/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.nodes.A_Node;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.conf.ConfigurationBuilder;
import java.net.MalformedURLException;
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
    }

    // PROTECTED
    @Override
    protected void prepare()
            throws Exception
    {
        ConfigurationBuilder cb = new ConfigurationBuilder();
//        cb.setDebugEnabled( true );

        String appID = getConfig( "id" );
        if ( appID != null && appID.length() > 0 )
        {
            cb.setOAuthAppId( appID );
        }

        String appSecret = getConfig( "secret" );
        if ( appSecret != null && appSecret.length() > 0 )
        {
            cb.setOAuthAppSecret( appSecret );
        }

        String appToken = getConfig( "token" );
        if ( appToken != null && appToken.length() > 0 )
        {
            cb.setOAuthAccessToken( appToken );
        }

        FacebookFactory ff = new FacebookFactory( cb.build() );

        this.facebook = ff.getInstance();
    }

    @Override
    protected void loop()
            throws InterruptedException , MalformedURLException , FacebookException
    {
        // Receive
        FB_Post post = (FB_Post) getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive post and like it : " + post );
        }

        // Like
        facebook.likePost( post.getID() );
    }

    @Override
    protected void terminate()
            throws Exception
    {
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_FB_PostLike.class );
    private Facebook facebook;
}
