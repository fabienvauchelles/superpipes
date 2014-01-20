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

package com.vaushell.superpipes.tools.scribe.tumblr;

import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.dispatch.Tags;
import com.vaushell.superpipes.tools.scribe.code.VC_FileFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.XMLConfiguration;
import org.joda.time.DateTime;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see TumblrClient
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TumblrClientTest
{
    // PUBLIC
    public TumblrClientTest()
    {
        this.dispatcher = new Dispatcher();
        this.client = new TumblrClient();
        this.blogname = null;
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

        // Test if parameters are set
        final Properties properties = dispatcher.getCommon( "tumblr" );

        blogname = properties.getProperty( "blogname" );
        assertNotNull( "Parameter 'blogname' should exist" ,
                       blogname );

        final String key = properties.getProperty( "key" );
        assertNotNull( "Parameter 'key' should exist" ,
                       key );

        final String secret = properties.getProperty( "secret" );
        assertNotNull( "Parameter 'secret' should exist" ,
                       secret );

        // Create tokens & login
        client.login( key ,
                      secret ,
                      dispatcher.getDatas().resolve( "test-tokens/tumblr.token" ) ,
                      dispatcher.getVCodeFactory().create( "[" + getClass().getName() + "] " ) );
    }

    /**
     * Test postLink.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPostLink()
        throws Exception
    {
        // Post
        final Tags tags = new Tags( "blog" ,
                                    "coding" ,
                                    "java" );

        // Force post to be post one month ago
        final DateTime dt = new DateTime().minusMonths( 1 ).withMillisOfSecond( 0 );

        final long ID = client.postLink( blogname ,
                                         "http://bit.ly/Ijk3of" ,
                                         "Blog de Fabien Vauchelles" ,
                                         "Du JAVA, du big data, et de l'entreprenariat" ,
                                         tags ,
                                         dt );

        assertTrue( "ID should be return" ,
                    ID > 0 );

        // Read
        final TB_Post post = client.readPost( blogname ,
                                              ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      post.getID() );
        assertNull( "Message should be empty" ,
                    post.getMessage() );
        assertEquals( "URL should be the same" ,
                      "http://bit.ly/Ijk3of" ,
                      post.getURL() );
        assertEquals( "URLname should be the same" ,
                      "Blog de Fabien Vauchelles" ,
                      post.getURLname() );
        assertEquals( "URLdescription should be the same" ,
                      "Du JAVA, du big data, et de l'entreprenariat" ,
                      post.getURLdescription() );
        assertArrayEquals( "tags should be the same" ,
                           tags.toArray() ,
                           post.getTags().toArray() );
        assertEquals( "Create date should be the same" ,
                      dt.getMillis() ,
                      post.getTimestamp().getMillis() );

        // Delete
        assertTrue( "Delete should work" ,
                    client.deletePost( blogname ,
                                       ID ) );
    }

    /**
     * Test postMessage.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPostMessage()
        throws Exception
    {
        // Post
        final String message = "Allez voir mon blog #" + new DateTime().getMillis();

        final Tags tags = new Tags( "myblog" ,
                                    "framework" ,
                                    "vauchelles" );

        // Force post to be post one month ago
        final DateTime dt = new DateTime().minusMonths( 1 ).withMillisOfSecond( 0 );

        final long ID = client.postMessage( blogname ,
                                            message ,
                                            tags ,
                                            dt );

        assertTrue( "ID should be return" ,
                    ID > 0 );

        // Read
        final TB_Post post = client.readPost( blogname ,
                                              ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      post.getID() );
        assertEquals( "message should be the same" ,
                      message ,
                      post.getMessage() );
        assertNull( "URL should be empty" ,
                    post.getURL() );
        assertNull( "URLname should be empty" ,
                    post.getURLname() );
        assertArrayEquals( "tags should be the same" ,
                           tags.toArray() ,
                           post.getTags().toArray() );
        assertEquals( "Create date should be the same" ,
                      dt.getMillis() ,
                      post.getTimestamp().getMillis() );

        // Delete
        assertTrue( "Delete should work" ,
                    client.deletePost( blogname ,
                                       ID ) );
    }

    /**
     * Test deletePost.
     *
     * @throws java.lang.Exception
     */
    @SuppressWarnings( "unchecked" )
    @Test( expectedExceptions =
    {
        TumblrException.class
    } )
    public void testDeletePost()
        throws Exception
    {
        // Post
        final long ID = client.postMessage( blogname ,
                                            "Allez voir mon blog #" + new DateTime().getMillis() ,
                                            new Tags() ,
                                            null );

        assertTrue( "ID should be return" ,
                    ID > 0 );

        // Delete
        assertTrue( "Delete should work" ,
                    client.deletePost( blogname ,
                                       ID ) );

        // Read error
        client.readPost( blogname ,
                         ID );
    }

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
        final String message1 = "Allez voir mon blog n°1" + new DateTime().getMillis();
        final Tags tags1 = new Tags( "myblog" ,
                                     "framework" ,
                                     "vauchelles" );

        final long ID1 = client.postMessage( blogname ,
                                             message1 ,
                                             tags1 ,
                                             null );

        assertTrue( "ID1 should be return" ,
                    ID1 >= 0 );

        // Post 2
        final String url2 = "http://fabien.vauchelles.com/";
        final String urlName2 = "Blog de Fabien Vauchelles";
        final String urlDescription2 = "du code, des infos, des trucs";
        final Tags tags2 = new Tags( "coding" ,
                                     "blogging" ,
                                     "java" );
        final long ID2 = client.postLink( blogname ,
                                          url2 ,
                                          urlName2 ,
                                          urlDescription2 ,
                                          tags2 ,
                                          null );

        assertTrue( "ID2 should be return" ,
                    ID2 >= 0 );

        // Post 3
        final String message3 = "Allez voir mon blog n°3" + new DateTime().getMillis();
        final Tags tags3 = new Tags( "cowboy" ,
                                     "space" ,
                                     "cosmicblog" );
        final long ID3 = client.postMessage( blogname ,
                                             message3 ,
                                             tags3 ,
                                             null );

        assertTrue( "ID3 should be return" ,
                    ID3 >= 0 );

        // Retrieve post
        final List<TB_Post> posts = client.readFeed( blogname ,
                                                     3 );
        assertEquals( "We should have 3 posts" ,
                      3 ,
                      posts.size() );

        // Check Post 3
        final TB_Post post3 = posts.get( 0 );
        assertEquals( "IDs should be the same" ,
                      ID3 ,
                      post3.getID() );
        assertEquals( "Messages should be the same" ,
                      message3 ,
                      post3.getMessage() );
        assertArrayEquals( "Tags should be the same" ,
                           tags3.toArray() ,
                           post3.getTags().toArray() );

        // Check Post 2
        final TB_Post post2 = posts.get( 1 );
        assertEquals( "IDs should be the same" ,
                      ID2 ,
                      post2.getID() );
        assertEquals( "URLs should be the same" ,
                      url2 ,
                      post2.getURL() );
        assertEquals( "URLs names should be the same" ,
                      urlName2 ,
                      post2.getURLname() );
        assertEquals( "URLs descriptions should be the same" ,
                      urlDescription2 ,
                      post2.getURLdescription() );
        assertArrayEquals( "Tags should be the same" ,
                           tags2.toArray() ,
                           post2.getTags().toArray() );

        // Check Post 1
        final TB_Post post1 = posts.get( 2 );
        assertEquals( "IDs should be the same" ,
                      ID1 ,
                      post1.getID() );
        assertEquals( "Messages should be the same" ,
                      message1 ,
                      post1.getMessage() );
        assertArrayEquals( "Tags should be the same" ,
                           tags1.toArray() ,
                           post1.getTags().toArray() );

        // Check iterator
        final Iterator<TB_Post> it = client.iteratorFeed( blogname ,
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

        // Delete Post 3
        assertTrue( "Delete should work" ,
                    client.deletePost( blogname ,
                                       ID3 ) );

        // Delete Post 2
        assertTrue( "Delete should work" ,
                    client.deletePost( blogname ,
                                       ID2 ) );

        // Delete Post 1
        assertTrue( "Delete should work" ,
                    client.deletePost( blogname ,
                                       ID1 ) );
    }
    // PRIVATE
    private final Dispatcher dispatcher;
    private final TumblrClient client;
    private String blogname;
}
