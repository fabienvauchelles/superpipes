/*
 * Copyright (C) 2014 Fabien Vauchelles (fabien_AT_vauchelles_DOT_com).
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

package com.vaushell.superpipes.tools.retry;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * Unit test.
 *
 * @see A_Retry
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class RetryTest
{
    // PUBLIC
    public RetryTest()
    {
        // Nothing
    }

    /**
     * Test of retry.
     *
     * @throws RetryException
     */
    @Test( expectedExceptions = RetryException.class )
    public void testRetry()
        throws RetryException
    {
        final AtomicInteger i = new AtomicInteger();

        try
        {
            new A_Retry<Void>()
            {
                @Override
                protected Void executeContent()
                    throws Exception
                {
                    i.getAndIncrement();

                    throw new IOException( "error" );
                }
            }
                .setRetry( 2 )
                .execute();
        }
        finally
        {
            assertEquals( "Test must try 3 times (normal + 2 retries)" ,
                          3 ,
                          i.get() );
        }
    }

    /**
     * Test of retry. Works.
     *
     * @throws RetryException
     */
    @Test
    public void testRetry2()
        throws RetryException
    {
        final AtomicInteger i = new AtomicInteger();

        final String content = new A_Retry<String>()
        {
            @Override
            protected String executeContent()
                throws Exception
            {
                if ( i.getAndIncrement() < 2 )
                {
                    throw new IOException( "error" );
                }

                return "mycontent";
            }
        }
            .setRetry( 2 )
            .execute();

        assertEquals( "Content controls" ,
                      "mycontent" ,
                      content );

        assertEquals( "Test must try 3 times (normal + 2 retries)" ,
                      3 ,
                      i.get() );
    }

    /**
     * Test of waitTime.
     *
     * @throws RetryException
     */
    @Test( expectedExceptions = RetryException.class )
    public void testWaitTime()
        throws RetryException
    {
        final DateTime start = new DateTime();
        final AtomicInteger i = new AtomicInteger();

        try
        {
            new A_Retry<Void>()
            {
                @Override
                protected Void executeContent()
                    throws Exception
                {
                    i.getAndIncrement();

                    throw new IOException( "error" );
                }
            }
                .setRetry( 1 )
                .setWaitTime( new Duration( 2000L ) )
                .execute();
        }
        finally
        {
            final Duration duration = new Duration( start ,
                                                    null );

            assertEquals( "Test must try 2 times (normal + 1 retry)" ,
                          2 ,
                          i.get() );

            assertTrue( "Duration must be greater than 1.5" ,
                        duration.getMillis() > 1500L );
        }
    }

    /**
     * Test of maxDuration.
     *
     * @throws RetryException
     */
    @Test( expectedExceptions = RetryException.class )
    public void testMaxDuration()
        throws RetryException
    {
        final DateTime start = new DateTime();

        try
        {
            new A_Retry<Void>()
            {
                @Override
                protected Void executeContent()
                    throws Exception
                {
                    throw new IOException( "error" );
                }
            }
                .setRetry( 3 )
                .setWaitTime( new Duration( 1000L ) )
                .setWaitTimeMultiplier( 2 )
                .setMaxDuration( new Duration( 3000L ) )
                .execute();
        }
        finally
        {
            final Duration duration = new Duration( start ,
                                                    null );

            assertTrue( "Max duration must be less than 3.5s" ,
                        duration.getMillis() < 3500L );
        }
    }
}
