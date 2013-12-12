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

package com.vaushell.spipes;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.stub.N_MessageLogger;
import com.vaushell.spipes.nodes.stub.N_NewsGenerator;
import java.util.Properties;
import javax.naming.ConfigurationException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test
 *
 * @see Dispatcher cs
 */
public class DispatcherTest
{
    // PUBLIC
    @BeforeClass
    public void setUpClass()
            throws ConfigurationException
    {
    }

    @AfterClass
    public void tearDownClass()
            throws Exception
    {
    }

    @Test
    public void testAdd()
    {
        Dispatcher dispatcher = new Dispatcher();

        dispatcher.addNode( "generator" ,
                            N_NewsGenerator.class ,
                            new Properties() );

        dispatcher.addNode( "receptor" ,
                            N_MessageLogger.class ,
                            new Properties() );

        A_Node node = dispatcher.nodes.get( "generator" );
        assertEquals( node.getClass() ,
                      N_NewsGenerator.class );

        dispatcher.addRoute( "generator" ,
                             "receptor" );

        assertTrue( dispatcher.routes.get( "generator" ).contains( "receptor" ) );

        dispatcher.addRoute( "generator" ,
                             "receptor" );

        assertEquals( dispatcher.routes.get( "generator" ).size() ,
                      1 );
    }

    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testNodeDuplicate()
    {
        Dispatcher dispatcher = new Dispatcher();

        dispatcher.addNode( "generator" ,
                            N_NewsGenerator.class ,
                            new Properties() );
        dispatcher.addNode( "generator" ,
                            N_NewsGenerator.class ,
                            new Properties() );
    }

    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testAddRouteBeforeNode()
    {
        Dispatcher dispatcher = new Dispatcher();

        dispatcher.addRoute( "generator" ,
                             "receptor" );
    }
}
