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

package com.vaushell.superpipes.transforms.rnd;

import com.vaushell.superpipes.dispatch.ConfigProperties;
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
public class T_RandomLinearTest
{
    // PUBLIC
    public T_RandomLinearTest()
    {
        this.dispatcher = new Dispatcher();
    }

    /**
     * Test random.
     *
     * @throws java.lang.Exception
     */
    @SuppressWarnings( "unchecked" )
    @Test
    public void testRandom()
        throws Exception
    {
        final A_Node n = dispatcher.addNode( "dummy" ,
                                             N_Dummy.class ,
                                             ConfigProperties.EMPTY_COMMONS );
        final A_Transform t = n.addTransformIN( T_RandomLinear.class ,
                                                ConfigProperties.EMPTY_COMMONS );

        final int percent = 30;
        t.getProperties().setProperty( "percent" ,
                                       Integer.toString( percent ) );

        // Prepare
        n.prepare();

        // Transform
        int count = 0;
        final int max = 100000;
        for ( int i = 0 ; i < max ; ++i )
        {
            final Message m = Message.create( Message.KeyIndex.TITLE ,
                                              "mon titre" ,
                                              Message.KeyIndex.PUBLISHED_DATE ,
                                              generateTimestamp( "01/03/2014 10:31:00" ) );

            if ( t.transform( m ) != null )
            {
                ++count;
            }
        }

        final double realPercent = count * 100.0 / max;
        assertTrue( "Message is discarded by " + Integer.toString( percent ) + '%' ,
                    realPercent < (double) percent + 5 );

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
