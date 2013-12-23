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

package com.vaushell.spipes.transforms.date;

import com.vaushell.spipes.dispatch.Dispatcher;
import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.dummy.N_Dummy;
import com.vaushell.spipes.transforms.A_Transform;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
                                       "01/03/2014 10:30" );
        t.getProperties().setProperty( "date-max" ,
                                       "02/03/2014 18:00" );

        // Prepare
        n.prepare();

        // Transform
        Message m = Message.create( Message.KeyIndex.TITLE ,
                                    "mon titre" ,
                                    Message.KeyIndex.PUBLISHED_DATE ,
                                    generateTimestamp( "01/03/2014 10:31" ) );

        assertNotNull( "Message is inside" ,
                       t.transform( m ) );

        m = Message.create( Message.KeyIndex.TITLE ,
                            "mon titre" ,
                            Message.KeyIndex.PUBLISHED_DATE ,
                            generateTimestamp( "01/03/2014 10:29" ) );

        assertNull( "Message is outside" ,
                    t.transform( m ) );

        m = Message.create( Message.KeyIndex.TITLE ,
                            "mon titre" ,
                            Message.KeyIndex.PUBLISHED_DATE ,
                            generateTimestamp( "02/03/2014 18:01" ) );

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

    private long generateTimestamp( final String dt )
        throws ParseException
    {
        final Calendar cal = Calendar.getInstance();

        cal.setTime( new SimpleDateFormat( "dd/MM/yyyy HH:ss" ,
                                           Locale.ENGLISH ).parse( dt ) );

        return cal.getTimeInMillis();
    }
}
