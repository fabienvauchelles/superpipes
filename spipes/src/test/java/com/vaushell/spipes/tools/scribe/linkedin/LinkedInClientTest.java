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
            conf = "conf-local/configuration.xml";
        }

        final XMLConfiguration config = new XMLConfiguration( conf );
        dispatcher.load( config );

        // Test if parameters are set
        final Properties properties = dispatcher.getCommon( "linkedin" );

        final String key = properties.getProperty( "key" );
        assertNotNull( "Parameter 'key' should exist" ,
                       key );

        final String secret = properties.getProperty( "secret" );
        assertNotNull( "Parameter 'secret' should exist" ,
                       secret );

        final Path tokenPath = Paths.get( dispatcher.getConfig( "datas-directory" ) ,
                                          "test-tokens" ,
                                          "linkedin.token" );

        // Create tokens & login
        client.login( key ,
                      secret ,
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
        final LNK_Status status = client.readStatus( ID );

        assertEquals( "ID should be the same" ,
                      ID ,
                      status.getID() );
        assertEquals( "message should be the same" ,
                      message ,
                      status.getMessage() );
    }

    // PRIVATE
    private final Dispatcher dispatcher;
    private final LinkedInClient client;
}
