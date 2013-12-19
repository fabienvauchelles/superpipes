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
import com.vaushell.spipes.Message;
import com.vaushell.spipes.transforms.A_Transform;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

        this.activated = true;
        this.internalStack = new LinkedList<>();
        this.transformsIN = new ArrayList<>();
        this.transformsOUT = new ArrayList<>();
    }

    public String getNodeID()
    {
        return nodeID;
    }

    /**
     * Retrieve node's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    public String getConfig( final String key )
    {
        return properties.getProperty( key );
    }

    /**
     * Retrieve main's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    public String getMainConfig( final String key )
    {
        return dispatcher.getConfig( key );
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

    public void addTransformIN( final A_Transform transform )
    {
        transformsIN.add( transform );
    }

    public void addTransformOUT( final A_Transform transform )
    {
        transformsOUT.add( transform );
    }

    public void prepare()
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] prepare" );
        }
        prepareImpl();

        for ( final A_Transform transform : transformsIN )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + getNodeID() + "/IN:" + transform.getClass().getSimpleName() + "] prepare" );
            }
            transform.prepare();
        }

        for ( final A_Transform transform : transformsOUT )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + getNodeID() + "/OUT:" + transform.getClass().getSimpleName() + "] prepare" );
            }
            transform.prepare();
        }
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

    public void terminate()
        throws Exception
    {
        for ( final A_Transform transform : transformsOUT )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + getNodeID() + "/OUT:" + transform.getClass().getSimpleName() + "] terminate" );
            }
            transform.terminate();
        }

        for ( final A_Transform transform : transformsIN )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + getNodeID() + "/IN:" + transform.getClass().getSimpleName() + "] terminate" );
            }
            transform.terminate();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] terminate" );
        }
        terminateImpl();
    }

    /**
     * Receive a message and stack it.
     *
     * @param message Message
     */
    public void receiveMessage( final Message message )
        throws Exception
    {
        if ( message == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receiveMessage : message=" + message );
        }

        Message result = message;
        for ( final A_Transform transform : transformsIN )
        {
            result = transform.transform( result );
            if ( result == null )
            {
                return;
            }
        }

        synchronized( internalStack )
        {
            internalStack.addFirst( result );

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
     * Prepare node's execution. Executed 1 time at the beginning.
     *
     * @throws Exception
     */
    protected abstract void prepareImpl()
        throws Exception;

    /**
     * Loop execution. The execution is looped until message reception.
     *
     * @throws Exception
     */
    protected abstract void loop()
        throws Exception;

    /**
     * Close node's execution. Executed 1 time at the ending.
     *
     * @throws Exception
     */
    protected abstract void terminateImpl()
        throws Exception;

    /**
     * Send a message to every connected nodes.
     *
     * @param message Message.
     * @throws java.lang.Exception
     */
    protected void sendMessage( final Message message )
        throws Exception
    {
        if ( message == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] sendMessage : message=" + message );
        }

        Message result = message;
        for ( final A_Transform transform : transformsOUT )
        {
            result = transform.transform( result );
            if ( result == null )
            {
                return;
            }
        }

        dispatcher.sendMessage( nodeID ,
                                result );
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
    protected Message getLastMessageOrWait()
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

    /**
     * Pop the last message.
     *
     * @param timeout max time to wait (in ms)
     * @return the message (or null if empty)
     * @throws InterruptedException
     */
    protected Message getLastMessageOrWait( final long timeout )
        throws InterruptedException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] getLastMessageOrWait() : timeout=" + timeout );
        }

        synchronized( internalStack )
        {
            if ( internalStack.isEmpty() )
            {
                internalStack.wait( timeout );
            }

            return internalStack.pollLast();
        }
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( A_Node.class );
    private String nodeID;
    private Properties properties;
    private Dispatcher dispatcher;
    private final LinkedList<Message> internalStack;
    private volatile boolean activated;
    private final List<A_Transform> transformsIN;
    private final List<A_Transform> transformsOUT;
}
