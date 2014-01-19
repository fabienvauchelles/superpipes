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

package com.vaushell.spipes.tools.scribe.linkedin;

import com.vaushell.spipes.dispatch.Dispatcher;
import com.vaushell.spipes.tools.scribe.code.VC_FileFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
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
 * @see LinkedInClient
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class LinkedInClientTest
{
    // PUBLIC
    public LinkedInClientTest()
    {
        this.dispatcher = new Dispatcher();
        this.client = new LinkedInClient();
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
        final Properties properties = dispatcher.getCommon( "linkedin" );

        final String key = properties.getProperty( "key" );
        assertNotNull( "Parameter 'key' should exist" ,
                       key );

        final String secret = properties.getProperty( "secret" );
        assertNotNull( "Parameter 'secret' should exist" ,
                       secret );

        // Create tokens & login
        client.login( key ,
                      secret ,
                      dispatcher.getDatas().resolve( "test-tokens/linkedin.token" ) ,
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
        final String message = "Allez voir ce blog #" + new DateTime().getMillis();

        final String ID = client.postLink( message ,
                                           "http://fabien.vauchelles.com/" ,
                                           "Blog de Fabien Vauchelles" ,
                                           "du java, du big data et de l'entreprenariat" );

        assertTrue( "ID should be return" ,
                    ID != null && !ID.isEmpty() );

        // Read
        final LNK_Status status = client.readStatus( ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      status.getID() );
        assertEquals( "message should be the same" ,
                      message ,
                      status.getMessage() );
        assertEquals( "URL should be the same" ,
                      "http://fabien.vauchelles.com/" ,
                      status.getURL() );
        assertEquals( "URLname should be the same" ,
                      "Blog de Fabien Vauchelles" ,
                      status.getURLname() );
        assertEquals( "URLdescription should be the same" ,
                      "du java, du big data et de l'entreprenariat" ,
                      status.getURLdescription() );
        assertTrue( "Post should have been created less than 1 minute" ,
                    new Duration( status.getTimestamp() ,
                                  null ).getMillis() < 60000L );
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
        final String ID = client.postMessage( message );

        assertTrue( "ID should be return" ,
                    ID != null && !ID.isEmpty() );

        // Read
        final LNK_Status status = client.readStatus( ID );
        assertEquals( "ID should be the same" ,
                      ID ,
                      status.getID() );
        assertEquals( "message should be the same" ,
                      message ,
                      status.getMessage() );
        assertTrue( "Post should have been created less than 1 minute" ,
                    new Duration( status.getTimestamp() ,
                                  null ).getMillis() < 60000L );
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
        // Status 1
        final String message1 = "Allez voir mon blog n°1" + new DateTime().getMillis();
        final String ID1 = client.postMessage( message1 );

        assertTrue( "ID1 should be return" ,
                    ID1 != null && !ID1.isEmpty() );

        // Status 2
        final String message2 = "Allez voir mon blog n°2" + new DateTime().getMillis();
        final String url2 = "http://fabien.vauchelles.com/";
        final String urlName2 = "Blog de Fabien Vauchelles";
        final String urlDescription2 = "du code, des infos, des trucs";
        final String ID2 = client.postLink( message2 ,
                                            url2 ,
                                            urlName2 ,
                                            urlDescription2 );

        assertTrue( "ID2 should be return" ,
                    ID2 != null && !ID2.isEmpty() );

        // Status 3
        final String message3 = "Allez voir mon blog n°3" + new DateTime().getMillis();
        final String ID3 = client.postMessage( message3 );

        assertTrue( "ID3 should be return" ,
                    ID3 != null && !ID3.isEmpty() );

        // Retrieve Status
        final List<LNK_Status> status = client.readFeed( null ,
                                                         3 );
        assertEquals( "We should have 3 posts" ,
                      3 ,
                      status.size() );

        // Check Status 3
        final LNK_Status status3 = status.get( 0 );
        assertEquals( "IDs should be the same" ,
                      ID3 ,
                      status3.getID() );
        assertEquals( "Messages should be the same" ,
                      message3 ,
                      status3.getMessage() );

        // Check Status 2
        final LNK_Status status2 = status.get( 1 );
        assertEquals( "IDs should be the same" ,
                      ID2 ,
                      status2.getID() );
        assertEquals( "Messages should be the same" ,
                      message2 ,
                      status2.getMessage() );
        assertEquals( "URLs should be the same" ,
                      url2 ,
                      status2.getURL() );
        assertEquals( "URLs names should be the same" ,
                      urlName2 ,
                      status2.getURLname() );
        assertEquals( "URLs descriptions should be the same" ,
                      urlDescription2 ,
                      status2.getURLdescription() );

        // Check Status 1
        final LNK_Status status1 = status.get( 2 );
        assertEquals( "IDs should be the same" ,
                      ID1 ,
                      status1.getID() );
        assertEquals( "Messages should be the same" ,
                      message1 ,
                      status1.getMessage() );

        // Check iterator
        final Iterator<LNK_Status> it = client.iteratorFeed( null ,
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
    }
    // PRIVATE
    private final Dispatcher dispatcher;
    private final LinkedInClient client;
}
