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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Time slot which allow messages to pass the buffer.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class Slot
{
    // PUBLIC
    /**
     * Create a slot and parse value.
     *
     * @param days List of days, separated by a comma.
     * @param start Inclusive starting hour (format: HH:MM)
     * @param end Exclusive ending hour (format: HH:MM)
     * @return the slot
     * @throws ParseException
     */
    public static Slot parse( final String days ,
                              final String start ,
                              final String end )
        throws ParseException
    {
        // Days
        final SimpleDateFormat dfDay = new SimpleDateFormat( "E" ,
                                                             Locale.ENGLISH );

        final TreeSet<Integer> rDays = new TreeSet<>();
        for ( final String sDay : days.split( "," ) )
        {
            final Calendar cDay = Calendar.getInstance();
            cDay.setTime( dfDay.parse( sDay ) );

            rDays.add( cDay.get( Calendar.DAY_OF_WEEK ) );
        }

        // Hours
        final SimpleDateFormat dfHour = new SimpleDateFormat( "HH:mm" ,
                                                              Locale.ENGLISH );

        final Calendar minHour = Calendar.getInstance();
        minHour.setTime( dfHour.parse( start ) );

        final Calendar maxHour = Calendar.getInstance();
        maxHour.setTime( dfHour.parse( end ) );

        return new Slot( rDays ,
                         minHour.get( Calendar.HOUR_OF_DAY ) ,
                         minHour.get( Calendar.MINUTE ) ,
                         maxHour.get( Calendar.HOUR_OF_DAY ) ,
                         maxHour.get( Calendar.MINUTE ) );
    }

    public TreeSet<Integer> getDays()
    {
        return days;
    }

    public int getMinHour()
    {
        return minHour;
    }

    public int getMinMinute()
    {
        return minMinute;
    }

    public int getMaxHour()
    {
        return maxHour;
    }

    public int getMaxMinute()
    {
        return maxMinute;
    }

    /**
     * Return the time to wait to be in a slot and not to burst.
     *
     * @param mdate Actuam date
     * @return the time to wait (in ms)
     */
    public long getSmallestDiffInMs( final Calendar mdate )
    {
        if ( areWeInside( mdate ) )
        {
            return 0;
        }

        long bestDiff = Long.MAX_VALUE;
        for ( final int dow : days )
        {
            final Calendar minDayHour = (Calendar) mdate.clone();
            minDayHour.set( Calendar.DAY_OF_WEEK ,
                            dow );
            minDayHour.set( Calendar.HOUR_OF_DAY ,
                            minHour );
            minDayHour.set( Calendar.MINUTE ,
                            minMinute );
            minDayHour.set( Calendar.SECOND ,
                            0 );
            minDayHour.set( Calendar.MILLISECOND ,
                            0 );

            if ( minDayHour.before( mdate ) )
            {
                minDayHour.add( Calendar.WEEK_OF_YEAR ,
                                1 );
            }

            final long diff = minDayHour.getTimeInMillis() - mdate.getTimeInMillis();
            if ( diff < bestDiff )
            {
                bestDiff = diff;
            }
        }

        return bestDiff;
    }

    // DEFAULT
    /**
     * Are we inside this slot ?
     *
     * @param mdate Actual date
     * @return true or not
     */
    boolean areWeInside( final Calendar mdate )
    {
        if ( !days.contains( mdate.get( Calendar.DAY_OF_WEEK ) ) )
        {
            return false;
        }

        final Calendar minDayHour = (Calendar) mdate.clone();
        minDayHour.set( Calendar.HOUR_OF_DAY ,
                        minHour );
        minDayHour.set( Calendar.MINUTE ,
                        minMinute );
        minDayHour.set( Calendar.SECOND ,
                        0 );
        minDayHour.set( Calendar.MILLISECOND ,
                        0 );

        if ( mdate.before( minDayHour ) )
        {
            return false;
        }

        final Calendar maxDayHour = (Calendar) mdate.clone();
        maxDayHour.set( Calendar.HOUR_OF_DAY ,
                        maxHour );
        maxDayHour.set( Calendar.MINUTE ,
                        maxMinute );
        maxDayHour.set( Calendar.SECOND ,
                        0 );
        maxDayHour.set( Calendar.MILLISECOND ,
                        0 );
        if ( maxDayHour.before( mdate ) )
        {
            return false;
        }

        return true;
    }

    // PRIVATE
    private final TreeSet<Integer> days;
    private final int minHour;
    private final int minMinute;
    private final int maxHour;
    private final int maxMinute;

    private Slot( final TreeSet<Integer> days ,
                  final int minHour ,
                  final int minMinute ,
                  final int maxHour ,
                  final int maxMinute
    )
    {
        this.days = days;
        this.minHour = minHour;
        this.minMinute = minMinute;
        this.maxHour = maxHour;
        this.maxMinute = maxMinute;
    }

}
