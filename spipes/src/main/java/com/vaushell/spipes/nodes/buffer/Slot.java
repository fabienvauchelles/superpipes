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

import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class Slot
{

    public Slot( TreeSet<Integer> days ,
                 Calendar minHour ,
                 Calendar maxHour )
    {
        this.days = days;
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

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
                            minHour.get( Calendar.HOUR_OF_DAY ) );
            minDayHour.set( Calendar.MINUTE ,
                            minHour.get( Calendar.MINUTE ) );

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
    boolean areWeInside( final Calendar mdate )
    {
        if ( !days.contains( mdate.get( Calendar.DAY_OF_WEEK ) ) )
        {
            return false;
        }

        final Calendar minDayHour = (Calendar) mdate.clone();
        minDayHour.set( Calendar.HOUR_OF_DAY ,
                        minHour.get( Calendar.HOUR_OF_DAY ) );
        minDayHour.set( Calendar.MINUTE ,
                        minHour.get( Calendar.MINUTE ) );
        if ( mdate.before( minDayHour ) )
        {
            return false;
        }

        final Calendar maxDayHour = (Calendar) mdate.clone();
        maxDayHour.set( Calendar.HOUR_OF_DAY ,
                        maxHour.get( Calendar.HOUR_OF_DAY ) );
        maxDayHour.set( Calendar.MINUTE ,
                        maxHour.get( Calendar.MINUTE ) );
        if ( maxDayHour.before( mdate ) )
        {
            return false;
        }

        return true;
    }

    // PRIVATE
    private TreeSet<Integer> days;
    private Calendar minHour;
    private Calendar maxHour;

}
