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

package com.vaushell.superpipes.transforms.uri;

import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.nodes.dummy.N_Dummy;
import com.vaushell.superpipes.transforms.A_Transform;
import java.net.URI;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see T_Date
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_CheckURITest
{
    // PUBLIC
    public T_CheckURITest()
    {
        // Dispatcher
        final Dispatcher dispatcher = new Dispatcher();
        this.node = dispatcher.addNode( "dummy" ,
                                        N_Dummy.class );
        this.transform = node.addTransformIN( T_CheckURI.class );

        // Server
        this.server = new Server( PORT );

        final ResourceHandler rHandler = new ResourceHandler();
        rHandler.setDirectoriesListed( true );
        rHandler.setResourceBase( "src/test/webapp" );

        this.server.setHandler( rHandler );
    }

    /**
     * Start test execution.
     *
     * @throws Exception
     */
    @BeforeClass
    public void start()
        throws Exception
    {
        server.start();

        // Prepare
        node.prepare();
    }

    /**
     * Stop test execution.
     *
     * @throws Exception
     */
    @AfterClass
    public void stop()
        throws Exception
    {
        // Terminate
        node.terminate();

        server.stop();
        server.join();
        server.destroy();
    }

    /**
     * Test URI.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testURI()
        throws Exception
    {
        // Transform 1
        final Message m1 = Message.create( Message.KeyIndex.TITLE ,
                                           "mon titre" ,
                                           Message.KeyIndex.URI ,
                                           URI.create( "http://localhost:" + Integer.toString( PORT ) + "/findbiggest.html" ) );

        assertNotNull( "URI is correct" ,
                       transform.transform( m1 ) );

        // Transform 2
        final Message m2 = Message.create( Message.KeyIndex.TITLE ,
                                           "mon titre" ,
                                           Message.KeyIndex.URI ,
                                           URI.create( "http://localhost:" + Integer.toString( PORT ) + "/notexist.html" ) );

        assertNull( "URI is incorrect" ,
                    transform.transform( m2 ) );

    }

    // PRIVATE
    private static final int PORT = 45123;
    private final A_Node node;
    private final A_Transform transform;
    private final Server server;
}
