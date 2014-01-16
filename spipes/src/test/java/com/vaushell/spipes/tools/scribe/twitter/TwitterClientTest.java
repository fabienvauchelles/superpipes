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

package com.vaushell.spipes.tools.scribe.twitter;

import com.vaushell.spipes.dispatch.Dispatcher;
import com.vaushell.spipes.tools.scribe.OAuthException;
import com.vaushell.spipes.tools.scribe.code.VC_FileFactory;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
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
        this.client = new TwitterClient();
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
            conf = "conf-local/configuration.xml";
        }

        String datas = System.getProperty( "datas" );
        if ( datas == null )
        {
            datas = "conf-local/datas";
        }

        final XMLConfiguration config = new XMLConfiguration( conf );

        final Path pDatas = Paths.get( datas );
        dispatcher.init( config ,
                         pDatas ,
                         new VC_FileFactory( pDatas ) );

        // Test if parameters are set
        final Properties properties = dispatcher.getCommon( "twitter" );

        final String key = properties.getProperty( "key" );
        assertNotNull( "Parameter 'key' should exist" ,
                       key );

        final String secret = properties.getProperty( "secret" );
        assertNotNull( "Parameter 'secret' should exist" ,
                       secret );

        // Create tokens & login
        client.login( key ,
                      secret ,
                      dispatcher.getDatas().resolve( "test-tokens/twitter.token" ) ,
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

        final long ID = client.tweet( message );

        assertTrue( "ID should be return" ,
                    ID >= 0 );

        // Read
        final TW_Tweet tweet = client.readTweet( ID );

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
                    client.deleteTweet( ID ) );
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
        final long ID = client.tweet( "Allez voir mon blog #" + new DateTime().getMillis() );

        assertTrue( "ID should be return" ,
                    ID >= 0 );

        // Delete
        assertTrue( "Delete should work" ,
                    client.deleteTweet( ID ) );

        // Read
        client.readTweet( ID );
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
            ID = client.tweetPicture( message ,
                                      is );

            assertTrue( "ID should be return" ,
                        ID >= 0 );
        }

        // Read
        final TW_Tweet tweet = client.readTweet( ID );

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
                    client.deleteTweet( ID ) );
    }

    // PRIVATE
    private final Dispatcher dispatcher;
    private final TwitterClient client;
}
