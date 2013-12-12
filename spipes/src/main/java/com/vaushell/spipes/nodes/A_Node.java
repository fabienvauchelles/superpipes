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

package com.vaushell.spipes.nodes;

import com.vaushell.spipes.Dispatcher;
import java.util.LinkedList;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A processing node.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public abstract class A_Node
    extends Thread
{
    // PUBLIC
    public A_Node()
    {
        super();

        this.nodeID = null;
        this.properties = null;
        this.dispatcher = null;
        this.activated = true;
        this.internalStack = new LinkedList<>();
    }

    /**
     * Prepare node's execution. Executed 1 time at the beginning.
     *
     * @throws Exception
     */
    public abstract void prepare()
        throws Exception;

    /**
     * Close node's execution. Executed 1 time at the ending.
     *
     * @throws Exception
     */
    public abstract void terminate()
        throws Exception;

    public String getNodeID()
    {
        return nodeID;
    }

    /**
     * Configurate the node.
     *
     * @param nodeID Node's identifier
     * @param properties Node's properties (String->String)
     * @param dispatcher Main dispatcher
     */
    public void config( final String nodeID ,
                        final Properties properties ,
                        final Dispatcher dispatcher )
    {
        this.nodeID = nodeID;
        this.properties = properties;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run()
    {
        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "[" + getNodeID() + "] start thread" );
        }

        try
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + getNodeID() + "] loopin'" );
            }
            while ( isActive() )
            {
                try
                {
                    loop();
                }
                catch( final InterruptedException ex )
                {
                    // Ignore
                }
                catch( final Throwable ex )
                {
                    LOGGER.error( "Error" ,
                                  ex );
                }

                final String delayStr = getConfig( "delay" );
                if ( delayStr != null )
                {
                    try
                    {
                        Thread.sleep( Long.parseLong( delayStr ) );
                    }
                    catch( final InterruptedException ex )
                    {
                        // Ignore
                    }
                }
            }
        }
        catch( final Throwable th )
        {
            LOGGER.error( "Error" ,
                          th );
        }

        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "[" + getNodeID() + "] stop thread" );
        }
    }

    /**
     * Receive a message and stack it.
     *
     * @param message Object message
     */
    public void receiveMessage( final Object message )
    {
        if ( message == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receiveMessage : message=" + message );
        }

        synchronized( internalStack )
        {
            internalStack.addFirst( message );

            internalStack.notifyAll();
        }
    }

    /**
     * Stop the node.
     */
    public void stopMe()
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] stopMe" );
        }

        synchronized( this )
        {
            activated = false;
        }

        interrupt();
    }

    // PROTECTED
    /**
     * Loop execution. The execution is looped until message reception.
     *
     * @throws Exception
     */
    protected abstract void loop()
        throws Exception;

    /**
     * Retrieve node's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    protected String getConfig( final String key )
    {
        return properties.getProperty( key );
    }

    /**
     * Retrieve main's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    protected String getMainConfig( final String key )
    {
        return dispatcher.getConfig( key );
    }

    /**
     * Send a message to every connected nodes.
     *
     * @param message Object message.
     */
    protected void sendMessage( final Object message )
    {
        if ( message == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] sendMessage : message=" + message );
        }

        dispatcher.sendMessage( nodeID ,
                                message );
    }

    /**
     * Is the node alive ?
     *
     * @return True if alive
     */
    protected boolean isActive()
    {
        synchronized( this )
        {
            return activated;
        }
    }

    /**
     * Pop the last message.
     *
     * @return the message
     * @throws InterruptedException
     */
    protected Object getLastMessageOrWait()
        throws InterruptedException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] getLastMessageOrWait" );
        }

        synchronized( internalStack )
        {
            while ( internalStack.isEmpty() )
            {
                internalStack.wait();
            }

            return internalStack.pollLast();
        }
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( A_Node.class );
    private String nodeID;
    private Properties properties;
    private Dispatcher dispatcher;
    private final LinkedList<Object> internalStack;
    private volatile boolean activated;
}
