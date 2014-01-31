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

package com.vaushell.superpipes.tools.scribe.twitter;

import com.vaushell.superpipes.dispatch.ConfigProperties;
import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.tools.scribe.OAuthException;
import com.vaushell.superpipes.tools.scribe.code.VC_FileFactory;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.configuration.XMLConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see TwitterClient
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TwitterClientTest
{
    // PUBLIC
    public TwitterClientTest()
    {
        this.dispatcher = new Dispatcher();
        this.client1 = new TwitterClient();
        this.client2 = new TwitterClient();
    }

    /**
     * Initialize the test.
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUp()
        throws Exception
    {
        // My config
        String conf = System.getProperty( "conf" );
        if ( conf == null )
        {
            conf = "conf-local/test/configuration.xml";
        }

        String datas = System.getProperty( "datas" );
        if ( datas == null )
        {
            datas = "conf-local/test/datas";
        }

        final XMLConfiguration config = new XMLConfiguration( conf );

        final Path pDatas = Paths.get( datas );
        dispatcher.init( config ,
                         pDatas ,
                         new VC_FileFactory( pDatas ) );

        final ConfigProperties properties = dispatcher.getCommon( "twitter" );

        // Create tokens & login for client 1
        client1.login( properties.getConfigString( "key" ) ,
                       properties.getConfigString( "secret" ) ,
                       dispatcher.getDatas().resolve( "test-tokens/twitter.token" ) ,
                       dispatcher.getVCodeFactory().create( "[" + getClass().getName() + "] " ) );

        // Create tokens & login for client 2
        client2.login( properties.getConfigString( "key" ) ,
                       properties.getConfigString( "secret" ) ,
                       dispatcher.getDatas().resolve( "test-tokens/twitter2.token" ) ,
                       dispatcher.getVCodeFactory().create( "[" + getClass().getName() + "] " ) );
    }

    /**
     * Test tweet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testTweet()
        throws Exception
    {
        // Post
        final String message = "Bloggé de codé de Fabien Vauchelles (http://bit.ly/Ijk3of) #java at " + new DateTime().getMillis();

        final long ID = client1.tweet( message );

        assertTrue( "ID should be return" ,
                    ID >= 0 );

        // Read
        final TW_Tweet tweet = client1.readTweet( ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      tweet.getID() );
        assertEquals( "message should be the same" ,
                      message ,
                      tweet.getMessage() );
        assertTrue( "Post should have been created less than 1 minute" ,
                    new Duration( tweet.getCreatedTime() ,
                                  null ).getMillis() < 60000L );

        // Delete
        assertTrue( "Delete should work" ,
                    client1.deleteTweet( ID ) );
    }

    /**
     * Test deleteTweet.
     *
     * @throws java.lang.Exception
     */
    @Test( expectedExceptions =
    {
        OAuthException.class
    } )
    public void testDeletePost()
        throws Exception
    {
        // Post
        final long ID = client1.tweet( "Allez voir mon blog #" + new DateTime().getMillis() );

        assertTrue( "ID should be return" ,
                    ID >= 0 );

        // Delete
        assertTrue( "Delete should work" ,
                    client1.deleteTweet( ID ) );

        // Read
        client1.readTweet( ID );
    }

    /**
     * Test tweetPicture.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testTweetPicture()
        throws Exception
    {
        // Post
        final String message = "Bloggé de codé de Fabien Vauchelles (http://bit.ly/Ijk3of) #java at " + new DateTime().getMillis();

        final long ID;
        try( InputStream is = getClass().getResourceAsStream( "/media.png" ) )
        {
            ID = client1.tweetPicture( message ,
                                       is );

            assertTrue( "ID should be return" ,
                        ID >= 0 );
        }

        // Read
        final TW_Tweet tweet = client1.readTweet( ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      tweet.getID() );
        assertTrue( "message should be the same (except the image link which is end added)" ,
                    tweet.getMessage().startsWith( message ) );
        assertTrue( "Post should have been created less than 1 minute" ,
                    new Duration( tweet.getCreatedTime() ,
                                  null ).getMillis() < 60000L );

        // Delete
        assertTrue( "Delete should work" ,
                    client1.deleteTweet( ID ) );
    }

    /**
     * Test readTimeline.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadTimeline()
        throws Exception
    {
        // Tweet 1
        final String message1 = "Allez voir mon blog n°1" + new DateTime().getMillis();
        final long ID1 = client1.tweet( message1 );

        assertTrue( "ID1 should be return" ,
                    ID1 >= 0 );

        // Tweet 2
        final String message2 = "Allez voir mon blog n°2" + new DateTime().getMillis();
        final long ID2 = client1.tweet( message2 );

        assertTrue( "ID2 should be return" ,
                    ID2 >= 0 );

        // Tweet 3
        final String message3 = "Allez voir mon blog n°3" + new DateTime().getMillis();
        final long ID3 = client1.tweet( message3 );

        assertTrue( "ID3 should be return" ,
                    ID3 >= 0 );

        // Retrieve Tweet
        final List<TW_Tweet> tweets = client1.readTimeline( null ,
                                                            3 );
        assertEquals( "We should have 3 tweets" ,
                      3 ,
                      tweets.size() );

        // Check Tweet 3
        final TW_Tweet tweet3 = tweets.get( 0 );
        assertEquals( "IDs should be the same" ,
                      ID3 ,
                      tweet3.getID() );
        assertEquals( "Messages should be the same" ,
                      message3 ,
                      tweet3.getMessage() );

        // Check Tweet 2
        final TW_Tweet tweet2 = tweets.get( 1 );
        assertEquals( "IDs should be the same" ,
                      ID2 ,
                      tweet2.getID() );
        assertEquals( "Messages should be the same" ,
                      message2 ,
                      tweet2.getMessage() );

        // Check Tweet 1
        final TW_Tweet tweet1 = tweets.get( 2 );
        assertEquals( "IDs should be the same" ,
                      ID1 ,
                      tweet1.getID() );
        assertEquals( "Messages should be the same" ,
                      message1 ,
                      tweet1.getMessage() );

        // Check iterator
        final Iterator<TW_Tweet> it = client1.iteratorTimeline( null ,
                                                                1 );
        assertTrue( "We should have result" ,
                    it.hasNext() );
        assertEquals( "IDs should be the same" ,
                      ID3 ,
                      it.next().getID() );

        assertTrue( "We should have result" ,
                    it.hasNext() );
        assertEquals( "IDs should be the same" ,
                      ID2 ,
                      it.next().getID() );

        assertTrue( "We should have result" ,
                    it.hasNext() );
        assertEquals( "IDs should be the same" ,
                      ID1 ,
                      it.next().getID() );

        // Delete Tweet 3
        assertTrue( "Delete should work" ,
                    client1.deleteTweet( ID3 ) );

        // Delete Tweet 2
        assertTrue( "Delete should work" ,
                    client1.deleteTweet( ID2 ) );

        // Delete Tweet 1
        assertTrue( "Delete should work" ,
                    client1.deleteTweet( ID1 ) );
    }

    /**
     * Test retweet.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testRetweet()
        throws Exception
    {
        // Post
        final String message = "Blogge codé de Fabien Vauchelles (http://bit.ly/Ijk3of) #java at " + new DateTime().getMillis();

        final long ID = client1.tweet( message );

        assertTrue( "ID should be return" ,
                    ID >= 0 );

        // Retweet
        final long ID2 = client2.retweet( ID );

        assertTrue( "ID should be return" ,
                    ID2 >= 0 );

        // Delete Retweet
        assertTrue( "Delete should work" ,
                    client2.deleteTweet( ID2 ) );

        // Delete Tweet
        assertTrue( "Delete should work" ,
                    client1.deleteTweet( ID ) );
    }

    // PRIVATE
    private final Dispatcher dispatcher;
    private final TwitterClient client1;
    private final TwitterClient client2;
}
