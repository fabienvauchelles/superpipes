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

package com.vaushell.superpipes.nodes.test;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import java.util.LinkedList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wait message to be receive and send it to a function.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_ReceiveBlocking
    extends A_Node
{
    // PUBLIC
    public N_ReceiveBlocking()
    {
        super( null ,
               null );

        this.messages = new LinkedList<>();
    }

    /**
     * Get the actual processing message, or wait it.
     *
     * @return the Message
     * @throws InterruptedException
     */
    public Message getProcessingMessageOrWait()
        throws InterruptedException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] getProcessingMessageOrWait()" );
        }

        synchronized( messages )
        {
            while ( messages.isEmpty() )
            {
                messages.wait();
            }

            return messages.pollFirst();
        }
    }

    /**
     * Get the actual processing message, or wait it.
     *
     * @param timeout a timeout
     * @return the Message
     * @throws InterruptedException
     */
    public Message getProcessingMessageOrWait( final Duration timeout )
        throws InterruptedException
    {
        if ( timeout == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] getProcessingMessageOrWait() : timeout=" + timeout );
        }

        synchronized( messages )
        {
            DateTime start = new DateTime();
            Duration remaining = timeout;
            while ( messages.isEmpty() && remaining.getMillis() > 0L )
            {
                messages.wait( remaining.getMillis() );

                final DateTime now = new DateTime();

                final Duration elapsed = new Duration( start ,
                                                       now );

                remaining = remaining.minus( elapsed );

                start = now;
            }

            return messages.pollFirst();
        }
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        // Nothing
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected void loop()
        throws Exception
    {
        // Receive
        setMessage( getLastMessageOrWait() );

        synchronized( messages )
        {
            messages.addLast( getMessage() );

            messages.notifyAll();
        }

        sendMessage();
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private final LinkedList<Message> messages;
    private static final Logger LOGGER = LoggerFactory.getLogger( N_ReceiveBlocking.class );

}
