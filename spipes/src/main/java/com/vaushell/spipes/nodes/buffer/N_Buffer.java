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

import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_Buffer
    extends A_Node
{
    // PUBLIC
    public N_Buffer()
    {
        super();

        this.slots = new ArrayList<>();
        this.lastWrite = null;
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        if ( slots.isEmpty() )
        {
            throw new IllegalArgumentException( "must have slots" );
        }
    }

    @Override
    protected void loop()
        throws Exception
    {
        // 1. Are we allowed to publish ?
        final Calendar cal = Calendar.getInstance();

        final long ttw = getTimeToWait( cal );
        if ( ttw > 0 )
        {
            try
            {
                Thread.sleep( ttw );
            }
            catch( final InterruptedException ex )
            {
                // Ignore
            }

            return;
        }

        // 2. Pop from stack
        Message message = popMessage();
        if ( message == null )
        {
            // Nothing : we wait for external
            message = getLastMessageOrWait();

            // Push to stack
            pushMessage( message );

            // And loop to check if we're allowed to publish.
            return;
        }

        // 3. We published
        lastWrite = cal.getTimeInMillis();

        sendMessage( message );
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
    }

    // PRIVATE
    private long flowLimit;
    private final List<Slot> slots;
    private Long lastWrite;

    private Message popMessage()
    {
        throw new UnsupportedOperationException();
    }

    private void pushMessage( final Message message )
    {
        throw new UnsupportedOperationException();
    }

    private long getTimeToWait( final Calendar calendar )
    {
        // Best slot
        long minTime = Long.MAX_VALUE;
        for ( final Slot slot : slots )
        {
            final long time = slot.getSmallestDiffInMs( calendar );
            if ( time < minTime )
            {
                minTime = time;

                if ( minTime == 0 )
                {
                    break;
                }
            }
        }

        // Anti burst
        if ( lastWrite != null )
        {
            long diff = calendar.getTimeInMillis() - lastWrite;
            if ( diff < flowLimit )
            {
                minTime = Math.max( minTime ,
                                    flowLimit - diff );
            }
        }

        return minTime;
    }
}
