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

package com.vaushell.spipes.transforms.tags;

import com.vaushell.spipes.dispatch.Dispatcher;
import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.dispatch.Tags;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.dummy.N_Dummy;
import com.vaushell.spipes.transforms.A_Transform;
import java.util.Arrays;
import java.util.TreeSet;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see T_Tags
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_TagsTest
{
    // PUBLIC
    public T_TagsTest()
    {
        this.dispatcher = new Dispatcher();
    }

    /**
     * Test INCLUDE_ONE.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIncludeOne()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy" ,
                                             N_Dummy.class );
        final A_Transform t = n.addTransformIN( T_Tags.class );
        t.getProperties().setProperty( "type" ,
                                       "INCLUDE_ONE" );
        t.getProperties().setProperty( "tags" ,
                                       "java,extreme" );

        // Prepare
        n.prepare();

        // Transform
        final Message mKO = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "blog" ,
                                                      "coding" ) );

        assertNull( "Message is not include" ,
                    t.transform( mKO ) );

        final Message mOK = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "coding" ,
                                                      "java" ) );

        assertNotNull( "Messate is include" ,
                       t.transform( mOK ) );

        // Terminate
        n.terminate();
    }

    /**
     * Test INCLUDE_ALL.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testIncludeAll()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy2" ,
                                             N_Dummy.class );
        final A_Transform t = n.addTransformIN( T_Tags.class );
        t.getProperties().setProperty( "type" ,
                                       "INCLUDE_ALL" );
        t.getProperties().setProperty( "tags" ,
                                       "java,extreme" );

        // Prepare
        n.prepare();

        // Transform
        final Message mKO = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "coding" ,
                                                      "java" ) );

        assertNull( "Message is not include" ,
                    t.transform( mKO ) );

        final Message mOK = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "extreme" ,
                                                      "java" ) );

        assertNotNull( "Messate is include" ,
                       t.transform( mOK ) );

        // Terminate
        n.terminate();
    }

    /**
     * Test EXCLUDE_ONE.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExcludeOne()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy3" ,
                                             N_Dummy.class );
        final A_Transform t = n.addTransformIN( T_Tags.class );
        t.getProperties().setProperty( "type" ,
                                       "EXCLUDE_ONE" );
        t.getProperties().setProperty( "tags" ,
                                       "java,extreme" );

        // Prepare
        n.prepare();

        // Transform
        final Message mKO = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "blog" ,
                                                      "java" ) );

        assertNull( "Message is not include" ,
                    t.transform( mKO ) );

        final Message mOK = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "blog" ,
                                                      "coding" )
        );

        assertNotNull( "Messate is include" ,
                       t.transform( mOK ) );

        // Terminate
        n.terminate();
    }

    /**
     * Test EXCLUDE_ALL.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testExcludeAll()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy4" ,
                                             N_Dummy.class );
        final A_Transform t = n.addTransformIN( T_Tags.class );
        t.getProperties().setProperty( "type" ,
                                       "EXCLUDE_ALL" );
        t.getProperties().setProperty( "tags" ,
                                       "java,extreme" );

        // Prepare
        n.prepare();

        // Transform
        final Message mKO = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "java" ,
                                                      "extreme" ) );

        assertNull( "Message is not include" ,
                    t.transform( mKO ) );

        final Message mOK = Message.create( Message.KeyIndex.TAGS ,
                                            new Tags( "blog" ,
                                                      "java" ) );

        assertNotNull( "Messate is include" ,
                       t.transform( mOK ) );

        // Terminate
        n.terminate();
    }

    // PRIVATE
    private final Dispatcher dispatcher;
}
