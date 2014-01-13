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

import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
     * @param start Inclusive starting hour (format: HH:mm:ss)
     * @param end Exclusive ending hour (format: HH:mm:ss)
     * @return the slot
     */
    public static Slot parse( final String days ,
                              final String start ,
                              final String end )
    {
        // Days
        final DateTimeFormatter fmtDay = DateTimeFormat.forPattern( "E" );

        final TreeSet<Integer> rDays = new TreeSet<>();
        for ( final String sDay : days.split( "," ) )
        {
            final int dayOfWeek = fmtDay.parseLocalDate( sDay ).getDayOfWeek();

            rDays.add( dayOfWeek );
        }

        // Hours (HH:mm:ss)
        final DateTimeFormatter fmtHour = DateTimeFormat.forPattern( "HH:mm:ss" );

        final LocalDateTime min = fmtHour.parseLocalDateTime( start ).withMillisOfSecond( 0 );
        final LocalDateTime max = fmtHour.parseLocalDateTime( end ).withMillisOfSecond( 0 );

        return new Slot( rDays ,
                         min.getMillisOfDay() ,
                         max.getMillisOfDay() );
    }

    public TreeSet<Integer> getDays()
    {
        return daysOfWeek;
    }

    public int getMinMillisOfDay()
    {
        return minMillisOfDay;
    }

    public int getMaxMillisOfDay()
    {
        return maxMillisOfDay;
    }

    /**
     * Return the time to wait to be in a slot and not to burst.
     *
     * @param date Actual date
     * @return the time to wait
     */
    public Duration getSmallestDiff( final DateTime date )
    {
        if ( areWeInside( date ) )
        {
            return new Duration( 0L );
        }

        Duration smallest = null;
        for ( final int dayOfWeek : daysOfWeek )
        {
            DateTime next = date.withDayOfWeek( dayOfWeek ).withMillisOfDay( minMillisOfDay );
            if ( next.isBefore( date ) )
            {
                next = next.plusWeeks( 1 );
            }

            final Duration duration = new Duration( date ,
                                                    next );
            if ( smallest == null || duration.isShorterThan( smallest ) )
            {
                smallest = duration;
            }
        }

        return smallest;
    }

    // DEFAULT
    /**
     * Are we inside this slot ?
     *
     * @param date Actual date
     * @return true or not
     */
    boolean areWeInside( final DateTime date )
    {

        if ( !daysOfWeek.contains( date.getDayOfWeek() ) )
        {
            return false;
        }

        final int dayMS = date.getMillisOfDay();
        return minMillisOfDay <= dayMS && dayMS < maxMillisOfDay;
    }

    // PRIVATE
    private final TreeSet<Integer> daysOfWeek;
    private final int minMillisOfDay;
    private final int maxMillisOfDay;

    private Slot( final TreeSet<Integer> daysOfWeek ,
                  final int minMillisOfDay ,
                  final int maxMillisOfDay
    )
    {
        this.daysOfWeek = daysOfWeek;
        this.minMillisOfDay = minMillisOfDay;
        this.maxMillisOfDay = maxMillisOfDay;
    }

}
