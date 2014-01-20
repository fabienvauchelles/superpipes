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

package com.vaushell.superpipes.transforms.date;

import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.nodes.dummy.N_Dummy;
import com.vaushell.superpipes.transforms.A_Transform;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see T_Date
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_DateTest
{
    // PUBLIC
    public T_DateTest()
    {
        this.dispatcher = new Dispatcher();
    }

    /**
     * Test date.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testInside()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy" ,
                                             N_Dummy.class );
        final A_Transform t = n.addTransformIN( T_Date.class );
        t.getProperties().setProperty( "date-min" ,
                                       "01/03/2014 10:30:00" );
        t.getProperties().setProperty( "date-max" ,
                                       "02/03/2014 18:00:00" );

        // Prepare
        n.prepare();

        // Transform
        Message m = Message.create( Message.KeyIndex.TITLE ,
                                    "mon titre" ,
                                    Message.KeyIndex.PUBLISHED_DATE ,
                                    generateTimestamp( "01/03/2014 10:31:00" ) );

        assertNotNull( "Message is inside" ,
                       t.transform( m ) );

        m = Message.create( Message.KeyIndex.TITLE ,
                            "mon titre" ,
                            Message.KeyIndex.PUBLISHED_DATE ,
                            generateTimestamp( "01/03/2014 10:29:00" ) );

        assertNull( "Message is outside" ,
                    t.transform( m ) );

        m = Message.create( Message.KeyIndex.TITLE ,
                            "mon titre" ,
                            Message.KeyIndex.PUBLISHED_DATE ,
                            generateTimestamp( "02/03/2014 18:01:00" ) );

        assertNull( "Message is outside" ,
                    t.transform( m ) );

        m = Message.create( Message.KeyIndex.TITLE ,
                            "mon titre" );

        assertNull( "Message is outside" ,
                    t.transform( m ) );

        // Terminate
        n.terminate();
    }

    // PRIVATE
    private final Dispatcher dispatcher;

    private DateTime generateTimestamp( final String dt )
    {
        return DateTimeFormat.forPattern( "dd/MM/yyyy HH:mm:ss" ).parseDateTime( dt );
    }
}
