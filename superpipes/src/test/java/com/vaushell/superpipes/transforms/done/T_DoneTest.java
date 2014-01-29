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

package com.vaushell.superpipes.transforms.done;

import com.vaushell.superpipes.dispatch.ConfigProperties;
import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.nodes.dummy.N_Dummy;
import com.vaushell.superpipes.tools.scribe.code.VC_FileFactory;
import com.vaushell.superpipes.transforms.A_Transform;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
    }

    /**
     * Test duplicate.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDuplicate()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy" ,
                                             N_Dummy.class ,
                                             ConfigProperties.EMPTY_COMMONS );
        final A_Transform t = n.addTransformIN( T_Done.class ,
                                                ConfigProperties.EMPTY_COMMONS );

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

    /**
     * Test duplicate with fields.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDuplicate2()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy2" ,
                                             N_Dummy.class ,
                                             ConfigProperties.EMPTY_COMMONS );
        final A_Transform t = n.addTransformIN( T_Done.class ,
                                                Arrays.asList( dispatcher.getCommon( "id" ) ) );

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
                                                "mon titre" ,
                                                Message.KeyIndex.DESCRIPTION ,
                                                "ma description2" );

        assertNotNull( "Message is learned" ,
                       t.transform( mLearn ) );
        assertNull( "Message 2 is not learned" ,
                    t.transform( mLearn2 ) );

        // Terminate
        n.terminate();
    }

    // PRIVATE
    private final Dispatcher dispatcher;
}
