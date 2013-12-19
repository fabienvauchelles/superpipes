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

package com.vaushell.spipes.nodes.buffer;

import java.text.ParseException;
import java.util.Calendar;
import java.util.TreeSet;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
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
     *
     * @throws java.text.ParseException
     */
    @Test
    public void testParse()
        throws ParseException
    {
        final Slot slot = Slot.parse( "TUE,SAT" ,
                                      "17:00" ,
                                      "18:30" );

        final TreeSet<Integer> days = new TreeSet<>();
        days.add( Calendar.TUESDAY );
        days.add( Calendar.SATURDAY );
        assertArrayEquals( "Days should be the same" ,
                           days.toArray() ,
                           slot.getDays().toArray() );

        assertEquals( "Hours should be the same" ,
                      17 ,
                      slot.getMinHour() );
        assertEquals( "Minutes should be the same" ,
                      0 ,
                      slot.getMinMinute() );
        assertEquals( "Hours should be the same" ,
                      18 ,
                      slot.getMaxHour() );
        assertEquals( "Minutes should be the same" ,
                      30 ,
                      slot.getMaxMinute() );
    }

    /**
     * Test of parse method, of class Slot.
     *
     * @throws java.text.ParseException
     */
    @Test( expectedExceptions =
    {
        ParseException.class
    } )
    public void testParse2()
        throws ParseException
    {
        Slot.parse( "TUE,xvsrtzer" ,
                    "17:00" ,
                    "18:30" );
    }

    /**
     * Test of parse method, of class Slot.
     *
     * @throws java.text.ParseException
     */
    @Test( expectedExceptions =
    {
        ParseException.class
    } )
    public void testParse3()
        throws ParseException
    {
        Slot.parse( "TUE,SAT" ,
                    "17:a00" ,
                    "18:30" );
    }

    /**
     * Test of areWeInside method, of class Slot.
     *
     * @throws java.text.ParseException
     */
    @Test
    public void testAreWeInside()
        throws ParseException
    {
        // TUE, SAT with 17:00-18:30
        Slot slot = Slot.parse( "TUE,SAT" ,
                                "17:00" ,
                                "18:30" );

        // With TUE 16:59 => false
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.TUESDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 16 );
        cal.set( Calendar.MINUTE ,
                 59 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertFalse( "TUE 16:59 inside TUE, SAT with 17:00-18:30" ,
                     slot.areWeInside( cal ) );

        // With TUE 17:01 => true
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.TUESDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 17 );
        cal.set( Calendar.MINUTE ,
                 1 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertTrue( "TUE 17:01 inside TUE, SAT with 17:00-18:30" ,
                    slot.areWeInside( cal ) );

        // With TUE 18:01 => true
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.TUESDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 18 );
        cal.set( Calendar.MINUTE ,
                 1 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertTrue( "TUE 18:01 inside TUE, SAT with 17:00-18:30" ,
                    slot.areWeInside( cal ) );

        // With TUE 19:00 => false
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.TUESDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 19 );
        cal.set( Calendar.MINUTE ,
                 0 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertFalse( "TUE 19:00 inside TUE, SAT with 17:00-18:30" ,
                     slot.areWeInside( cal ) );

        // With MON 17:30 => false
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.MONDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 17 );
        cal.set( Calendar.MINUTE ,
                 30 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertFalse( "MON 17:30 inside TUE, SAT with 17:00-18:30" ,
                     slot.areWeInside( cal ) );

        // With SAT 17:30 => true
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.SATURDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 17 );
        cal.set( Calendar.MINUTE ,
                 30 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertTrue( "SAT 17:30 inside TUE, SAT with 17:00-18:30" ,
                    slot.areWeInside( cal ) );

        // MON with 00:00-23:58
        slot = Slot.parse( "MON" ,
                           "00:00" ,
                           "23:58" );

        // With MON 00:00 => true
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.MONDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 0 );
        cal.set( Calendar.MINUTE ,
                 0 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertTrue( "MON 00:00 inside MON with 00:00-23:58" ,
                    slot.areWeInside( cal ) );

        // With MON 23:58 => true
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.MONDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 23 );
        cal.set( Calendar.MINUTE ,
                 58 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertTrue( "MON 23:58 inside MON with 00:00-23:58" ,
                    slot.areWeInside( cal ) );

        // With MON 23:59 => false
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.MONDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 23 );
        cal.set( Calendar.MINUTE ,
                 59 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertFalse( "MON 23:59 inside MON with 00:00-23:58" ,
                     slot.areWeInside( cal ) );
    }

    /**
     * Test of getSmallestDiffInMs method, of class Slot.
     *
     * @throws java.text.ParseException
     */
    @Test
    public void testGetSmallestDiffInMs()
        throws ParseException
    {
        // TUE, SAT with 17:00-18:30
        final Slot slot = Slot.parse( "TUE,SAT" ,
                                      "17:00" ,
                                      "18:30" );

        // with TUE, 16:00 => 3600000
        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.TUESDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 16 );
        cal.set( Calendar.MINUTE ,
                 0 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertEquals( "TUE, 16:00 inside TUE, SAT with 17:00-18:30" ,
                      3600000 ,
                      slot.getSmallestDiffInMs( cal ) );

        // with MON, 17:00 => 86400000
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.MONDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 17 );
        cal.set( Calendar.MINUTE ,
                 0 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertEquals( "MON, 17:00 inside TUE, SAT with 17:00-18:30" ,
                      86400000 ,
                      slot.getSmallestDiffInMs( cal ) );

        // with WED, 17:00 => 259200000
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.WEDNESDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 17 );
        cal.set( Calendar.MINUTE ,
                 0 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertEquals( "WED, 17:00 inside TUE, SAT with 17:00-18:30" ,
                      259200000 ,
                      slot.getSmallestDiffInMs( cal ) );

        // with SAT, 18:31 => 253740000
        cal = Calendar.getInstance();
        cal.set( Calendar.DAY_OF_WEEK ,
                 Calendar.SATURDAY );
        cal.set( Calendar.HOUR_OF_DAY ,
                 18 );
        cal.set( Calendar.MINUTE ,
                 31 );
        cal.set( Calendar.SECOND ,
                 0 );
        cal.set( Calendar.MILLISECOND ,
                 0 );
        assertEquals( "SAT, 18:01 inside TUE, SAT with 17:00-18:30" ,
                      253740000 ,
                      slot.getSmallestDiffInMs( cal ) );
    }
}
