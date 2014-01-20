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

package com.vaushell.superpipes.nodes.buffer;

import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see Slot
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class SlotTest
{
    // PUBLIC
    public SlotTest()
    {
        // Nothing
    }

    /**
     * Test of parse method, of class Slot.
     */
    @Test
    public void testParse()
    {
        final Slot slot = Slot.parse( "TUE,SAT" ,
                                      "17:00:00" ,
                                      "18:30:00" );

        final TreeSet<Integer> days = new TreeSet<>();
        days.add( DateTimeConstants.TUESDAY );
        days.add( DateTimeConstants.SATURDAY );
        assertArrayEquals( "Days should be the same" ,
                           days.toArray() ,
                           slot.getDays().toArray() );

        assertEquals( "Min MinMillisOfDay (17:00:00=61200000) should be the same" ,
                      61200000 ,
                      slot.getMinMillisOfDay() );
        assertEquals( "Min MinMillisOfDay (18:30:00=66600000) should be the same" ,
                      66600000 ,
                      slot.getMaxMillisOfDay() );
    }

    /**
     * Test of parse method, of class Slot.
     *
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testParse2()
    {
        Slot.parse( "TUE,xvsrtzer" ,
                    "17:00:00" ,
                    "18:30:00" );
    }

    /**
     * Test of parse method, of class Slot.
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testParse3()
    {
        Slot.parse( "TUE,SAT" ,
                    "17:a00:00" ,
                    "18:30:00" );
    }

    /**
     * Test of areWeInside method, of class Slot.
     *
     */
    @Test
    public void testAreWeInside()
    {
        // TUE, SAT with 17:00:00-18:30:00
        Slot slot = Slot.parse( "TUE,SAT" ,
                                "17:00:00" ,
                                "18:30:00" );

        // With 14/01/2014 (TUE) 16:59:00 => false
        DateTime dt = new DateTime( 2014 ,
                                    1 ,
                                    14 ,
                                    16 ,
                                    59 ,
                                    0 );
        assertFalse( "14/01/2014 (TUE) 16:59:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                     slot.areWeInside( dt ) );

        // With 14/01/2014 (TUE) 17:01:00 => true
        dt = new DateTime( 2014 ,
                           1 ,
                           14 ,
                           17 ,
                           1 ,
                           0 );
        assertTrue( "14/01/2014 (TUE) 17:01:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                    slot.areWeInside( dt ) );

        // With 14/01/2014 (TUE) 18:01:00 => true
        dt = new DateTime( 2014 ,
                           1 ,
                           14 ,
                           18 ,
                           1 ,
                           0 );
        assertTrue( "14/01/2014 (TUE) 18:01:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                    slot.areWeInside( dt ) );

        // With 14/01/2014 (TUE) 19:00:00 => false
        dt = new DateTime( 2014 ,
                           1 ,
                           14 ,
                           19 ,
                           0 ,
                           0 );
        assertFalse( "14/01/2014 (TUE) 19:00:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                     slot.areWeInside( dt ) );

        // With 13/01/2014 (MON) 17:30:00 => false
        dt = new DateTime( 2014 ,
                           1 ,
                           13 ,
                           17 ,
                           30 ,
                           0 );

        assertFalse( "13/01/2014 (MON) 17:30:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                     slot.areWeInside( dt ) );

        // With 18/01/2014 (SAT) 17:30:00 => true
        dt = new DateTime( 2014 ,
                           1 ,
                           18 ,
                           17 ,
                           30 ,
                           0 );
        assertTrue( "18/01/2014 (SAT) 17:30:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                    slot.areWeInside( dt ) );

        // With 13/01/2014 (MON) with 00:00:00-23:58:00
        slot = Slot.parse( "MON" ,
                           "00:00:00" ,
                           "23:58:00" );

        // With 13/01/2014 (MON) 00:00:00 => true
        dt = new DateTime( 2014 ,
                           1 ,
                           13 ,
                           0 ,
                           0 ,
                           0 );
        assertTrue( "13/01/2014 (MON) 00:00:00 inside MON with 00:00:00-23:58:00" ,
                    slot.areWeInside( dt ) );

        // With 13/01/2014 (MON) 23:57:59 => true
        dt = new DateTime( 2014 ,
                           1 ,
                           13 ,
                           23 ,
                           57 ,
                           59 );
        assertTrue( "13/01/2014 (MON) 23:57:59 inside MON with 00:00:00-23:58:00" ,
                    slot.areWeInside( dt ) );

        // With 13/01/2014 (MON) 23:58:00 => false
        dt = new DateTime( 2014 ,
                           1 ,
                           13 ,
                           23 ,
                           58 ,
                           0 );
        assertFalse( "13/01/2014 (MON) 23:58:00 inside MON with 00:00:00-23:58:00" ,
                     slot.areWeInside( dt ) );

        // With 13/01/2014 (MON) 23:59:00 => false
        dt = new DateTime( 2014 ,
                           1 ,
                           13 ,
                           23 ,
                           59 ,
                           0 );
        assertFalse( "13/01/2014 (MON) 23:59:00 inside MON with 00:00:00-23:58:00" ,
                     slot.areWeInside( dt ) );
    }

    /**
     * Test of getSmallestDiff method, of class Slot.
     */
    @Test
    public void testGetSmallestDiff()
    {
        // TUE, SAT with 17:00:00-18:30:00
        final Slot slot = Slot.parse( "TUE,SAT" ,
                                      "17:00:00" ,
                                      "18:30:00" );

        // With 14/01/2014 (TUE), 16:00:00 => 3600000
        DateTime dt = new DateTime( 2014 ,
                                    1 ,
                                    14 ,
                                    16 ,
                                    0 ,
                                    0 );
        assertEquals( "With 14/01/2014 (TUE), 16:00:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                      3600000L ,
                      slot.getSmallestDiff( dt ).getMillis() );

        // With 13/01/2014 (MON), 17:00:00 => 86400000
        dt = new DateTime( 2014 ,
                           1 ,
                           13 ,
                           17 ,
                           0 ,
                           0 );
        assertEquals( "With 13/01/2014 (MON), 17:00 inside TUE, SAT with 17:00-18:30" ,
                      86400000L ,
                      slot.getSmallestDiff( dt ).getMillis() );

        // With 15/01/2014 (WED), 17:00:00 => 259200000
        dt = new DateTime( 2014 ,
                           1 ,
                           15 ,
                           17 ,
                           0 ,
                           0 );
        assertEquals( "15/01/2014 (WED), 17:00:00 inside TUE, SAT with 17:00:00-18:30:00" ,
                      259200000L ,
                      slot.getSmallestDiff( dt ).getMillis() );

        // With 18/01/2014 (SAT), 18:31:00 => 253740000
        dt = new DateTime( 2014 ,
                           1 ,
                           18 ,
                           18 ,
                           31 ,
                           0 );
        assertEquals( "18/01/2014 (SAT), 18:01 inside TUE, SAT with 17:00:00-18:30:00" ,
                      253740000L ,
                      slot.getSmallestDiff( dt ).getMillis() );
    }
}
