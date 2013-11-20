/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.twitter;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import java.util.TreeSet;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_TW_Post
        extends A_Node
{
    // PUBLIC
    public N_TW_Post()
    {
        this.twitter = null;
    }

    // PROTECTED
    @Override
    protected void prepare()
            throws Exception
    {
        this.twitter = TwitterFactory.getSingleton();

        twitter.setOAuthConsumer( getConfig( "key" ) ,
                                  getConfig( "secret" ) ); // Consumer secret

        twitter.setOAuthAccessToken( new AccessToken( getConfig( "token" ) ,
                                                      getConfig( "token-secret" ) ) );
    }

    @Override
    protected void loop()
            throws TwitterException , InterruptedException
    {
        // Receive
        Object message = getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        // Convert if possible
        Tweet tweet;
        if ( message == null )
        {
            tweet = null;
        }
        else
        {
            if ( message instanceof News )
            {
                tweet = convertFromNews( (News) message );
            }
            else
            {
                tweet = null;
            }
        }

        if ( tweet == null )
        {
            throw new IllegalArgumentException( "message type is unknown : " + message.getClass().getName() );
        }

        // Send to Twitter
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] send tweet to twitter : " + tweet );
        }

        Status status = twitter.updateStatus( tweet.getMessage() );

        tweet.setTweetID( status.getId() );

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive ID : " + status.getId() );
        }

        sendMessage( tweet );
    }

    @Override
    protected void terminate()
            throws Exception
    {
    }
    // DEFAULT
    final static int TWEET_SIZE = 140;

    static Tweet convertFromNews( News news )
    {
        if ( news.getTitle() == null || news.getTitle().length() <= 0
             || news.getUri() == null || news.getUri().length() <= 0 )
        {
            throw new NullPointerException( "Title or URL can not be null" );
        }

        if ( news.getTags() == null )
        {
            throw new NullPointerException();
        }

        if ( news.getUri().length() > TWEET_SIZE )
        {
            throw new IllegalArgumentException( "URL is too long" );
        }

        StringBuilder sb = new StringBuilder();
        if ( news.getUri().length() > TWEET_SIZE - 15 )
        {
            sb.append( news.getUri() );
        }
        else
        {
            String title = cleanHTML( news.getTitle() );

            sb.append( " (" ).append( news.getUri() ).append( ")" );
            if ( title.length() + sb.length() > TWEET_SIZE )
            {
                sb.insert( 0 ,
                           title.substring( 0 ,
                                            TWEET_SIZE - sb.length() ) );
            }
            else
            {
                sb.insert( 0 ,
                           title );

                TreeSet<String> correctedTags = new TreeSet<>();
                for ( String tag : news.getTags() )
                {
                    String correctedTag = tag.toLowerCase();

                    correctedTags.add( correctedTag );
                }

                for ( String correctedTag : correctedTags )
                {
                    String ct = " #" + correctedTag;

                    if ( sb.length() + ct.length() <= TWEET_SIZE )
                    {
                        sb.append( ct );
                    }
                }
            }
        }

        return new Tweet( sb.toString() );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_TW_Post.class );
    private Twitter twitter;

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
