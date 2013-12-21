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

package com.vaushell.spipes.transforms.done;

import com.vaushell.spipes.Dispatcher;
import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.dummy.N_Dummy;
import com.vaushell.spipes.transforms.A_Transform;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.XMLConfiguration;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see T_Done
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_DoneTest
{
    // PUBLIC
    public T_DoneTest()
    {
        this.dispatcher = new Dispatcher();
    }

    /**
     * Initialize the test.
     *
     * @throws java.lang.Exception
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
        dispatcher.init( config ,
                         Paths.get( datas ) );
    }

    /**
     * Test INCLUDE_ONE.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDuplicate()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy" ,
                                             N_Dummy.class );
        final A_Transform t = n.addTransformIN( T_Done.class );

        final Path p = dispatcher.getDatas().resolve( Paths.get( n.getNodeID() ,
                                                                 "done.dat" ) );

        Files.deleteIfExists( p );

        // Prepare
        n.prepare();

        // Transform
        final Message mLearn = Message.create( Message.KeyIndex.TITLE ,
                                               "mon titre" ,
                                               Message.KeyIndex.DESCRIPTION ,
                                               "ma description" );
        final Message mLearn2 = Message.create( Message.KeyIndex.TITLE ,
                                                "mon titre2" ,
                                                Message.KeyIndex.DESCRIPTION ,
                                                "ma description2" );

        assertNotNull( "Message is learned" ,
                       t.transform( mLearn ) );
        assertNotNull( "Message 2 is learned" ,
                       t.transform( mLearn2 ) );

        assertNull( "Message is not duplicated" ,
                    t.transform( mLearn ) );
        assertNull( "Message 2 is not duplicated" ,
                    t.transform( mLearn2 ) );

        // Terminate
        n.terminate();
    }

    // PRIVATE
    private final Dispatcher dispatcher;
}
