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

package com.vaushell.spipes.transforms.bitly;

import com.vaushell.spipes.Dispatcher;
import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.dummy.N_Dummy;
import com.vaushell.spipes.transforms.A_Transform;
import java.net.URI;
import java.util.Properties;
import org.apache.commons.configuration.XMLConfiguration;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see T_Shorten
 * @see T_Expand
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_ShortenExpandTest
{
    // PUBLIC
    public T_ShortenExpandTest()
    {
        this.dispatcher = new Dispatcher();
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

        // Test if parameters are set.
        final Properties properties = dispatcher.getCommon( "bitly" );

        assertTrue( "Parameter 'username' should exist" ,
                    properties.containsKey( "username" ) );
        assertTrue( "Parameter 'apikey' should exist" ,
                    properties.containsKey( "apikey" ) );
    }

    /**
     * Test T_Shorten and T_Expand.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testTransform()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy" ,
                                             N_Dummy.class );
        final A_Transform tShorten = n.addTransformIN( T_Shorten.class ,
                                                       "bitly" );
        final A_Transform tExpand = n.addTransformIN( T_Expand.class ,
                                                      "bitly" );

        // Prepare node
        n.prepare();

        // Test : shorten
        final URI refURI = new URI( "http://fabien.vauchelles.com/" );
        final Message m = Message.create( Message.KeyIndex.URI ,
                                          refURI );

        assertNotNull( "Transform should return a message" ,
                       tShorten.transform( m ) );

        final URI shortenURL = (URI) m.getProperty( Message.KeyIndex.URI );

        assertTrue( "Shorten URL should contains bit.ly domain" ,
                    shortenURL.getHost().contains( "bit.ly" ) );

        // Test : expand
        assertNotNull( "Transform should return a message" ,
                       tExpand.transform( m ) );

        final URI expandURL = (URI) m.getProperty( Message.KeyIndex.URI );

        assertEquals( "URI must be the same after a reduce/expand" ,
                      refURI ,
                      expandURL );

        // Terminate node
        n.terminate();
    }

    // PRIVATE
    private final Dispatcher dispatcher;
}
