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

package com.vaushell.spipes.dispatch;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.dummy.N_Dummy;
import com.vaushell.spipes.nodes.stub.N_MessageLogger;
import com.vaushell.spipes.nodes.stub.N_NewsGenerator;
import com.vaushell.spipes.transforms.A_Transform;
import com.vaushell.spipes.transforms.done.T_Done;
import java.util.Properties;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see Dispatcher
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class DispatcherTest
{
    // PUBLIC
    public DispatcherTest()
    {
        // Nothing
    }

    /**
     * Test add.
     */
    @Test
    public void testAdd()
    {
        final Dispatcher dispatcher = new Dispatcher();

        dispatcher.addNode( "generator" ,
                            N_NewsGenerator.class );

        dispatcher.addNode( "receptor" ,
                            N_MessageLogger.class );

        final A_Node node = dispatcher.nodes.get( "generator" );
        assertEquals( "Node should be a N_NewsGenerator class" ,
                      node.getClass() ,
                      N_NewsGenerator.class );

        dispatcher.addRoute( "generator" ,
                             "receptor" );

        assertTrue( "Generator route should contain receptor" ,
                    dispatcher.routes.get( "generator" ).contains( "receptor" ) );

        dispatcher.addRoute( "generator" ,
                             "receptor" );

        assertEquals( "Generator should only have one route" ,
                      dispatcher.routes.get( "generator" ).size() ,
                      1 );
    }

    /**
     * Test duplicate node insertion.
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testNodeDuplicate()
    {
        final Dispatcher dispatcher = new Dispatcher();

        dispatcher.addNode( "generator" ,
                            N_NewsGenerator.class );
        dispatcher.addNode( "generator" ,
                            N_NewsGenerator.class );
    }

    /**
     * Test node/route insertion order.
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testAddRouteBeforeNode()
    {
        final Dispatcher dispatcher = new Dispatcher();

        dispatcher.addRoute( "generator" ,
                             "receptor" );
    }

    /**
     * Test commons.
     */
    @Test
    public void testCommons()
    {
        final Dispatcher dispatcher = new Dispatcher();

        final Properties properties = new Properties();
        properties.put( "test" ,
                        "montest" );
        dispatcher.addCommon( "conf1" ,
                              properties );

        final Properties properties2 = new Properties();
        properties2.put( "check" ,
                         "moncheck" );
        dispatcher.addCommon( "conf2" ,
                              properties2 );

        // Node
        final A_Node node = dispatcher.addNode( "dummy" ,
                                                N_Dummy.class ,
                                                "conf1" );
        node.getProperties().setProperty( "test2" ,
                                          "montest2" );

        String val = node.getConfig( "test2" ,
                                     false );
        assertEquals( "test2 property should be found" ,
                      "montest2" ,
                      val );

        val = node.getConfig( "check" ,
                              true );
        assertNull( "check property is unknown" ,
                    val );

        val = node.getConfig( "test" ,
                              false );
        assertEquals( "test property should be found" ,
                      "montest" ,
                      val );

        node.getProperties().setProperty( "test" ,
                                          "monautretest" );

        val = node.getConfig( "test" ,
                              false );
        assertEquals( "test property should be found" ,
                      "monautretest" ,
                      val );

        // Transform
        final A_Transform transform = node.addTransformIN( T_Done.class ,
                                                           "conf2" ,
                                                           "conf1" );

        val = transform.getConfig( "check" ,
                                   false );
        assertEquals( "check property should be found" ,
                      "moncheck" ,
                      val );

        val = transform.getConfig( "test" ,
                                   false );
        assertEquals( "test property should be found" ,
                      "montest" ,
                      val );
    }

    /**
     * Test unknown common conf.
     */
    @Test
    public void testCommonsNull()
    {
        final Dispatcher dispatcher = new Dispatcher();

        // Node
        final A_Node node = dispatcher.addNode( "dummy" ,
                                                N_Dummy.class ,
                                                "confnull" );

        assertNull( "confnull common is unknown" ,
                    node.getConfig( "test2" ,
                                    true ) );

    }
}
