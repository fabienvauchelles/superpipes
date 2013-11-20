/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PostUpdate;
import facebook4j.conf.ConfigurationBuilder;
import java.net.MalformedURLException;
import java.net.URL;
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

        PostUpdate pu;
        if ( post.getUrl() != null && post.getUrl().length() > 0 )
        {
            // We have an url
            pu = new PostUpdate( new URL( post.getUrl() ) );

            if ( post.getUrlName() != null && post.getUrlName().length() > 0 )
            {
                pu.name( post.getUrlName() );
            }

            if ( post.getUrlDescription() != null && post.getUrlDescription().length() > 0 )
            {
                pu.description( post.getUrlDescription() );
            }

            if ( post.getMessage() != null && post.getMessage().length() > 0 )
            {
                pu.message( post.getMessage() );
            }
        }
        else
        {
            pu = new PostUpdate( post.getMessage() );
        }

        String ID = facebook.postFeed( pu );

        post.setID( ID );

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        sendMessage( post );
    }

    @Override
    protected void terminate()
            throws Exception
    {
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_FB_Post.class );
    private Facebook facebook;

    private static FB_Post convertFromNews( News news )
    {
        if ( ( news.getTitle() == null || news.getTitle().length() <= 0 )
             && ( news.getUri() == null || news.getUri().length() <= 0 ) )
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
                            news.getUri() ,
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
