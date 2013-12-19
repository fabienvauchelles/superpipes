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
import org.apache.commons.configuration.HierarchicalConfiguration;
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
        this.properties = new Properties();
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
     * Set nodes's parameters.
     *
     * @param nodeID Node's identifier
     * @param dispatcher Main dispatcher
     */
    public void setParameters( final String nodeID ,
                               final Dispatcher dispatcher )
    {
        this.nodeID = nodeID;
        this.dispatcher = dispatcher;
    }

    /**
     * Load configuration for this node.
     *
     * @param cNode Configuration
     * @throws Exception
     */
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        Dispatcher.readProperties( properties ,
                                   cNode );

        // Load transforms IN
        transformsIN.clear();
        final List<HierarchicalConfiguration> cTransformsIN = cNode.configurationsAt( "in.transform" );
        if ( cTransformsIN != null )
        {
            for ( final HierarchicalConfiguration cTransform : cTransformsIN )
            {
                final A_Transform transform = addTransformIN( cTransform.getString( "[@type]" ) );

                transform.load( cTransform );
            }
        }

        // Load transforms OUT
        transformsOUT.clear();
        final List<HierarchicalConfiguration> cTransformsOUT = cNode.configurationsAt( "out.transform" );
        if ( cTransformsOUT != null )
        {
            for ( final HierarchicalConfiguration cTransform : cTransformsOUT )
            {
                final A_Transform transform = addTransformOUT( cTransform.getString( "[@type]" ) );

                transform.load( cTransform );
            }
        }
    }

    /**
     * Add a transform to the node input.
     *
     * @param type Transform's type
     * @return the transform
     */
    public A_Transform addTransformIN( final String type )
    {
        if ( type == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] addTransformIN : type=" + type );
        }

        try
        {
            final A_Transform transform = (A_Transform) Class.forName( type ).newInstance();
            transform.setParent( this );

            transformsIN.add( transform );

            return transform;
        }
        catch( final ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    /**
     * Add a transform to the node output.
     *
     * @param type Transform's type
     * @return the transform
     */
    public A_Transform addTransformOUT( final String type )
    {
        if ( type == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] addTransformOUT : type=" + type );
        }

        try
        {
            final A_Transform transform = (A_Transform) Class.forName( type ).newInstance();
            transform.setParent( this );

            transformsOUT.add( transform );

            return transform;
        }
        catch( final ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    /**
     * Prepare node's execution. Executed 1 time at the beginning. Generic implementation.
     *
     * @throws Exception
     */
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

    /**
     * Close node's execution. Executed 1 time at the ending. Generic implementation.
     *
     * @throws Exception
     */
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
     * @throws java.lang.Exception
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
        if ( timeout <= 0 )
        {
            throw new IllegalArgumentException( "you should use getLastMessageOrWait with a timeout=0" );
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] getLastMessageOrWait() : timeout=" + timeout );
        }

        synchronized( internalStack )
        {
            long start = System.currentTimeMillis();
            long remaining = timeout;
            while ( internalStack.isEmpty()
                    && remaining > 0 )
            {
                internalStack.wait( remaining );

                final long actual = System.currentTimeMillis();
                final long elapsed = actual - start;
                remaining -= elapsed;
                start = actual;
            }

            return internalStack.pollLast();
        }
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( A_Node.class );
    private String nodeID;
    private final Properties properties;
    private Dispatcher dispatcher;
    private final LinkedList<Message> internalStack;
    private volatile boolean activated;
    private final List<A_Transform> transformsIN;
    private final List<A_Transform> transformsOUT;
}
