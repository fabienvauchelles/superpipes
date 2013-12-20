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

import com.vaushell.spipes.Dispatcher;
import com.vaushell.spipes.tools.scribe.code.VC_File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
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

        final XMLConfiguration config = new XMLConfiguration( conf );
        dispatcher.load( config );

        // Test if parameters are set
        final Properties properties = dispatcher.getCommon( "facebook" );

        final String key = properties.getProperty( "key" );
        assertNotNull( "Parameter 'key' should exist" ,
                       key );

        final String secret = properties.getProperty( "secret" );
        assertNotNull( "Parameter 'secret' should exist" ,
                       secret );

        final Path tokenPath = Paths.get( dispatcher.getConfig( "datas-directory" ) ,
                                          "test-tokens" ,
                                          "facebook.token" );

        // Create tokens & login
        client.login( key ,
                      secret ,
                      "publish_stream" ,
                      tokenPath ,
                      new VC_File( "[" + getClass().getName() + "] " ,
                                   Paths.get( tokenPath.toString() + ".code" ) ) );
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
        final String message = "Allez voir ce blog #" + new Date().getTime();

        final String ID = client.postLink( message ,
                                           "http://fabien.vauchelles.com/" ,
                                           "Blog de Fabien Vauchelles" ,
                                           "JAVA ou JAVAPA?" ,
                                           "Du JAVA, du big data, et de l'entreprenariat" );

        assertTrue( "ID should be return" ,
                    ID != null && !ID.isEmpty() );

        // Read
        final FB_Post post = client.readPost( ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      post.getID() );
        assertEquals( "message should be the same" ,
                      message ,
                      post.getMessage() );
        assertEquals( "URL should be the same" ,
                      "http://fabien.vauchelles.com/" ,
                      post.getURL() );
        assertEquals( "URLname should be the same" ,
                      "Blog de Fabien Vauchelles" ,
                      post.getURLname() );
        assertEquals( "URLcaption should be the same" ,
                      "JAVA ou JAVAPA?" ,
                      post.getURLcaption() );
        assertEquals( "URLdescription should be the same" ,
                      "Du JAVA, du big data, et de l'entreprenariat" ,
                      post.getURLdescription() );

        // Like/Unlike
        assertTrue( "Like should work" ,
                    client.likePost( ID ) );
        assertTrue( "Unlike should work" ,
                    client.unlikePost( ID ) );

        // Delete
        assertTrue( "Delete should work" ,
                    client.deletePost( ID ) );
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
        final String message = "Allez voir mon blog #" + new Date().getTime();
        final String ID = client.postMessage( message );

        assertTrue( "ID should be return" ,
                    ID != null && !ID.isEmpty() );

        // Read
        final FB_Post post = client.readPost( ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      post.getID() );
        assertEquals( "message should be the same" ,
                      message ,
                      post.getMessage() );

        // Delete
        assertTrue( "Delete should work" ,
                    client.deletePost( ID ) );
    }

    /**
     * Test deletePost.
     *
     * @throws java.lang.Exception
     */
    @Test( expectedExceptions =
    {
        FacebookException.class
    } )
    public void testDeletePost()
        throws Exception
    {
        // Post
        final String ID = client.postMessage( "Allez voir mon blog #" + new Date().getTime() );

        assertTrue( "ID should be return" ,
                    ID != null && !ID.isEmpty() );

        // Delete
        assertTrue( "Delete should work" ,
                    client.deletePost( ID ) );

        // Read error
        client.readPost( ID );
    }

    // PRIVATE
    private final Dispatcher dispatcher;
    private final FacebookClient client;
}
