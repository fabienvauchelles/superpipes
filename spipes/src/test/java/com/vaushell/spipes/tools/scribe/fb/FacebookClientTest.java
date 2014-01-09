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

package com.vaushell.spipes.tools.scribe.fb;

import com.vaushell.spipes.dispatch.Dispatcher;
import com.vaushell.spipes.tools.scribe.code.VC_FileFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.XMLConfiguration;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see FacebookClient
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class FacebookClientTest
{
    // PUBLIC
    public FacebookClientTest()
    {
        this.dispatcher = new Dispatcher();
        this.client = new FacebookClient();
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
        final Properties properties = dispatcher.getCommon( "facebook" );

        final String key = properties.getProperty( "key" );
        assertNotNull( "Parameter 'key' should exist" ,
                       key );

        final String secret = properties.getProperty( "secret" );
        assertNotNull( "Parameter 'secret' should exist" ,
                       secret );

        // Create tokens & login
        client.login( key ,
                      secret ,
                      dispatcher.getDatas().resolve( "test-tokens/facebook.token" ) ,
                      dispatcher.getVCodeFactory().create( "[" + getClass().getName() + "] " ) );
    }

//    /**
//     * Test postLink.
//     *
//     * @throws java.lang.Exception
//     */
//    @Test
//    public void testPostLink()
//        throws Exception
//    {
//        // Post
//        final String message = "Allez voir ce blog #" + new Date().getTime();
//
//        final String ID = client.postLink( message ,
//                                           "http://fabien.vauchelles.com/" ,
//                                           "Blog de Fabien Vauchelles" ,
//                                           "JAVA ou JAVAPA?" ,
//                                           "Du JAVA, du big data, et de l'entreprenariat" );
//
//        assertTrue( "ID should be return" ,
//                    ID != null && !ID.isEmpty() );
//
//        // Read
//        final FB_Post post = client.readPost( ID );
//
//        assertEquals( "ID should be the same" ,
//                      ID ,
//                      post.getID() );
//        assertEquals( "message should be the same" ,
//                      message ,
//                      post.getMessage() );
//        assertEquals( "URL should be the same" ,
//                      "http://fabien.vauchelles.com/" ,
//                      post.getURL() );
//        assertEquals( "URLname should be the same" ,
//                      "Blog de Fabien Vauchelles" ,
//                      post.getURLname() );
//        assertEquals( "URLcaption should be the same" ,
//                      "JAVA ou JAVAPA?" ,
//                      post.getURLcaption() );
//        assertEquals( "URLdescription should be the same" ,
//                      "Du JAVA, du big data, et de l'entreprenariat" ,
//                      post.getURLdescription() );
//
//        // Like/Unlike
//        assertTrue( "Like should work" ,
//                    client.likePost( ID ) );
//        assertTrue( "Unlike should work" ,
//                    client.unlikePost( ID ) );
//
//        // Delete
//        assertTrue( "Delete should work" ,
//                    client.deletePost( ID ) );
//    }
//
//    /**
//     * Test postMessage.
//     *
//     * @throws java.lang.Exception
//     */
//    @Test
//    public void testPostMessage()
//        throws Exception
//    {
//        // Post
//        final String message = "Allez voir mon blog #" + new Date().getTime();
//        final String ID = client.postMessage( message );
//
//        assertTrue( "ID should be return" ,
//                    ID != null && !ID.isEmpty() );
//
//        // Read
//        final FB_Post post = client.readPost( ID );
//
//        assertEquals( "ID should be the same" ,
//                      ID ,
//                      post.getID() );
//        assertEquals( "message should be the same" ,
//                      message ,
//                      post.getMessage() );
//
//        // Delete
//        assertTrue( "Delete should work" ,
//                    client.deletePost( ID ) );
//    }
//
//    /**
//     * Test deletePost.
//     *
//     * @throws java.lang.Exception
//     */
//    @Test( expectedExceptions =
//    {
//        FacebookException.class
//    } )
//    public void testDeletePost()
//        throws Exception
//    {
//        // Post
//        final String ID = client.postMessage( "Allez voir mon blog #" + new Date().getTime() );
//
//        assertTrue( "ID should be return" ,
//                    ID != null && !ID.isEmpty() );
//
//        // Delete
//        assertTrue( "Delete should work" ,
//                    client.deletePost( ID ) );
//
//        // Read error
//        client.readPost( ID );
//    }
    /**
     * Test testReadFeed.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testReadFeed()
        throws Exception
    {
        // Post 1
        final String message1 = "Allez voir mon blog n°1" + new Date().getTime();
        final String ID1 = client.postMessage( message1 );

        assertTrue( "ID1 should be return" ,
                    ID1 != null && !ID1.isEmpty() );

        // Post 2
        final String message2 = "Allez voir mon blog n°2" + new Date().getTime();
        final String url2 = "http://fabien.vauchelles.com/";
        final String urlName2 = "Blog de Fabien Vauchelles";
        final String urlCaption2 = "du java";
        final String urlDescription2 = "du code, des infos, des trucs";
        final String ID2 = client.postLink( message2 ,
                                            url2 ,
                                            urlName2 ,
                                            urlCaption2 ,
                                            urlDescription2 );

        assertTrue( "ID2 should be return" ,
                    ID2 != null && !ID2.isEmpty() );

        // Post 3
        final String message3 = "Allez voir mon blog n°3" + new Date().getTime();
        final String ID3 = client.postMessage( message3 );

        assertTrue( "ID3 should be return" ,
                    ID3 != null && !ID3.isEmpty() );

        // Retrieve post
        final List<FB_Post> posts = client.readFeed( "me" );
        assertTrue( "We should have minimum 3 posts" ,
                    posts.size() >= 3 );

        // Check Post 3
        final FB_Post post3 = posts.get( 0 );
        assertEquals( "IDs should be the same" ,
                      ID3 ,
                      post3.getID() );
        assertEquals( "Messages should be the same" ,
                      message3 ,
                      post3.getMessage() );

        // Check Post 2
        final FB_Post post2 = posts.get( 1 );
        assertEquals( "IDs should be the same" ,
                      ID2 ,
                      post2.getID() );
        assertEquals( "Messages should be the same" ,
                      message2 ,
                      post2.getMessage() );
        assertEquals( "URLs should be the same" ,
                      url2 ,
                      post2.getURL() );
        assertEquals( "URLs names should be the same" ,
                      urlName2 ,
                      post2.getURLname() );
        assertEquals( "URLs captions should be the same" ,
                      urlCaption2 ,
                      post2.getURLcaption() );
        assertEquals( "URLs descriptions should be the same" ,
                      urlDescription2 ,
                      post2.getURLdescription() );

        // Check Post 1
        final FB_Post post1 = posts.get( 2 );
        assertEquals( "IDs should be the same" ,
                      ID1 ,
                      post1.getID() );
        assertEquals( "Messages should be the same" ,
                      message1 ,
                      post1.getMessage() );

        // Delete Post 3
        assertTrue( "Delete should work" ,
                    client.deletePost( ID3 ) );

        // Delete Post 2
        assertTrue( "Delete should work" ,
                    client.deletePost( ID2 ) );

        // Delete Post 1
        assertTrue( "Delete should work" ,
                    client.deletePost( ID1 ) );
    }

    // PRIVATE
    private final Dispatcher dispatcher;
    private final FacebookClient client;
}
