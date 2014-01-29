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

import java.util.Random;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retry execution.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 * @param <T> Type return of the execution function.
 */
public abstract class A_Retry<T>
{
    // PUBLIC
    public A_Retry()
    {
        this.retry = 0;
        this.waitTime = new Duration( 0L );
        this.waitTimeMultiplier = 1.0;
        this.maxDuration = new Duration( 0L );

        this.tryCount = 0;
        this.random = new Random();
    }

    /**
     * How many times do I retry if the check fails ?.
     *
     * @param retry Retry doesn't include normal execution. If retry is 2, code will be executed 3 times.
     * @return this element.
     */
    public A_Retry<T> setRetry( final int retry )
    {
        if ( retry < 0 )
        {
            throw new IllegalArgumentException();
        }

        this.retry = retry;

        return this;
    }

    /**
     * How long should I wait between 2 checks ? (in milliseconds).
     *
     * @param waitTime Time to wait
     * @return this element.
     */
    public A_Retry<T> setWaitTime( final Duration waitTime )
    {
        if ( waitTime == null || waitTime.getMillis() < 0L )
        {
            throw new IllegalArgumentException();
        }

        this.waitTime = waitTime;

        return this;
    }

    /**
     * How multiple I wait-time each time ?. Next Time to Wait = Actual Time to wait * multiplier
     *
     * @param waitTimeMultiplier Multiplier.
     * @return this element.
     */
    public A_Retry<T> setWaitTimeMultiplier( final double waitTimeMultiplier )
    {
        if ( waitTimeMultiplier < 0 )
        {
            throw new IllegalArgumentException();
        }

        this.waitTimeMultiplier = waitTimeMultiplier;

        return this;
    }

    /**
     * Add or substract randomly time to wait-time (between 0 and JitterRanger).
     *
     * @param jitterRange Jitter delay in ms.
     * @return this element.
     */
    public A_Retry<T> setJitterRange( final int jitterRange )
    {
        if ( jitterRange < 0 )
        {
            throw new IllegalArgumentException();
        }

        this.jitterRange = jitterRange;

        return this;
    }

    /**
     * How long shoud I retry ? (in milliseconds, 0=disabled). Doesn't interrupt code execution.
     *
     * @param maxDuration Maximum duration.
     * @return this element.
     */
    public A_Retry<T> setMaxDuration( final Duration maxDuration )
    {
        if ( maxDuration == null || maxDuration.getMillis() < 0L )
        {
            throw new IllegalArgumentException();
        }

        this.maxDuration = maxDuration;

        return this;
    }

    /**
     * Execute.
     *
     * @return Function returns if necessary.
     * @throws RetryException
     */
    public T execute()
        throws RetryException
    {
        start = new DateTime();

        while ( true )
        {
            try
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug( "try " + tryCount + "/" + retry );
                }

                return executeContent();
            }
            catch( final Throwable ex )
            {
                // Error
                if ( tryCount >= retry )
                {
                    if ( LOGGER.isDebugEnabled() )
                    {
                        LOGGER.debug( "try count reached" );
                    }

                    throw new RetryException( ex );
                }

                if ( maxDuration.getMillis() > 0L )
                {
                    final Duration actualDuration = new Duration( start ,
                                                                  null );

                    if ( !actualDuration.isShorterThan( maxDuration ) )
                    {
                        if ( LOGGER.isDebugEnabled() )
                        {
                            LOGGER.debug( "try delay reached" );
                        }

                        throw new RetryException( ex );
                    }
                }
            }

            if ( waitTime.getMillis() > 0L )
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug(
                        "try " + tryCount + "/" + retry + " failed. Wait " + waitTime + " before next retry" );
                }

                try
                {
                    Thread.sleep( waitTime.getMillis() );
                }
                catch( final InterruptedException ex )
                {
                    // Ignore
                }
            }
            else
            {
                if ( LOGGER.isDebugEnabled() )
                {
                    LOGGER.debug(
                        "try " + tryCount + "/" + retry + " failed. Don't wait" );
                }
            }

            // First, multiply time
            waitTime = new Duration( (long) ( (double) waitTime.getMillis() * waitTimeMultiplier ) );

            // Second, add jitter
            if ( jitterRange > 0 )
            {
                final int jitter = random.nextInt( jitterRange );
                if ( random.nextBoolean() )
                {
                    waitTime = waitTime.plus( (long) jitter );
                }
                else
                {
                    if ( (long) jitter < waitTime.getMillis() )
                    {
                        waitTime = waitTime.minus( (long) jitter );
                    }
                }
            }

            ++tryCount;
        }
    }

    // PROTECTED
    /**
     * Specific code to execute.
     *
     * @return Value return.
     * @throws Exception
     */
    protected abstract T executeContent()
        throws Exception;

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( A_Retry.class );
    private int retry;
    private int tryCount;
    private Duration waitTime;
    private double waitTimeMultiplier;
    private int jitterRange;
    private final Random random;
    private Duration maxDuration;
    private DateTime start;
}
