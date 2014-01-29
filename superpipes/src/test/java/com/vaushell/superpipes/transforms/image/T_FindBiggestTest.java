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

package com.vaushell.superpipes.transforms.image;

import com.vaushell.superpipes.dispatch.ConfigProperties;
import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.nodes.dummy.N_Dummy;
import com.vaushell.superpipes.transforms.A_Transform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import javax.imageio.ImageIO;
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
public class T_FindBiggestTest
{
    // PUBLIC
    public T_FindBiggestTest()
    {
        // Dispatcher
        final Dispatcher dispatcher = new Dispatcher();
        this.node = dispatcher.addNode( "dummy" ,
                                        N_Dummy.class ,
                                        ConfigProperties.EMPTY_COMMONS );
        this.transform = node.addTransformIN( T_FindBiggest.class ,
                                              ConfigProperties.EMPTY_COMMONS );

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
     * Test image.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testImage()
        throws Exception
    {
        // Transform
        final Message m = Message.create( Message.KeyIndex.TITLE ,
                                          "mon titre" ,
                                          Message.KeyIndex.URI ,
                                          URI.create( "http://localhost:" + Integer.toString( PORT ) + "/findbiggest.html" ) );

        assertNotNull( "Message is inside" ,
                       transform.transform( m ) );

        assertTrue( "Message should contain an image" ,
                    m.contains( Message.KeyIndex.PICTURE ) );

        final byte[] bImage = (byte[]) m.getProperty( Message.KeyIndex.PICTURE );

        final BufferedImage imageRef = ImageIO.read( getClass().getResourceAsStream( "/fabienvauchelles600px.jpeg" ) );

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write( imageRef ,
                       "png" ,
                       os );
        os.close();

        assertArrayEquals( "Images should be the same" ,
                           os.toByteArray() ,
                           bImage );

    }

    /**
     * Test no image.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testNoImage()
        throws Exception
    {
        // Transform
        final Message m = Message.create( Message.KeyIndex.TITLE ,
                                          "mon titre" ,
                                          Message.KeyIndex.URI ,
                                          URI.create( "http://localhost:" + Integer.toString( PORT ) ) );

        assertNotNull( "Message is inside" ,
                       transform.transform( m ) );

        assertFalse( "Message should not contain an image" ,
                     m.contains( Message.KeyIndex.PICTURE ) );
    }

    // PRIVATE
    private static final int PORT = 45123;
    private final A_Node node;
    private final A_Transform transform;
    private final Server server;
}
