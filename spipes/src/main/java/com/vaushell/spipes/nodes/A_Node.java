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

import com.vaushell.spipes.dispatch.Dispatcher;
import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.transforms.A_Transform;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
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
    public static final Duration DEFAULT_DELAY = new Duration( 1L * 1000L );
    public static final Duration DEFAULT_ANTIBURST = new Duration( 2L * 1000L );
    public static final Duration SECURE_ANTIBURST = new Duration( 60L * 1000L );

    public A_Node( final Duration defaultDelay ,
                   final Duration defaultAntiBurst )
    {
        super();

        this.activated = true;
        this.internalStack = new LinkedList<>();
        this.transformsIN = new ArrayList<>();
        this.transformsOUT = new ArrayList<>();
        this.properties = new Properties();
        this.commonsPropertiesID = new ArrayList<>();
        this.lastPop = null;
        this.message = null;

        if ( defaultAntiBurst != null && defaultAntiBurst.getMillis() <= 0L )
        {
            throw new IllegalArgumentException( "defaultAntiBurst can't be <=0. Should be null." );
        }
        this.antiBurst = defaultAntiBurst;

        if ( defaultDelay != null && defaultDelay.getMillis() <= 0L )
        {
            throw new IllegalArgumentException( "defaultDelay can't be <=0. Should be null." );
        }
        this.delay = defaultDelay;
    }

    /**
     * Set node's parameters.
     *
     * @param nodeID Node's identifier
     * @param dispatcher Main dispatcher
     * @param commonsPropertiesID commons properties set reference
     */
    public void setParameters( final String nodeID ,
                               final Dispatcher dispatcher ,
                               final String[] commonsPropertiesID )
    {
        this.nodeID = nodeID;
        this.dispatcher = dispatcher;

        for ( final String cpID : commonsPropertiesID )
        {
            this.commonsPropertiesID.add( cpID );
        }
    }

    public String getNodeID()
    {
        return nodeID;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public Dispatcher getDispatcher()
    {
        return dispatcher;
    }

    /**
     * Retrieve node's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    public String getConfig( final String key )
    {
        String value = properties.getProperty( key );
        if ( value != null )
        {
            return value;
        }

        for ( final String commonPropertiesID : commonsPropertiesID )
        {
            final Properties commonsProperties = dispatcher.getCommon( commonPropertiesID );
            if ( commonsProperties != null )
            {
                value = commonsProperties.getProperty( key );
                if ( value != null )
                {
                    return value;
                }
            }
        }

        return null;
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

        if ( properties.containsKey( "anti-burst" ) )
        {
            antiBurst = new Duration( Long.parseLong( properties.getProperty( "anti-burst" ) ) );

            if ( antiBurst.getMillis() <= 0L )
            {
                throw new IllegalArgumentException( "antiBurst can't be <=0. Should be null or empty." );
            }
        }
        if ( properties.containsKey( "delay" ) )
        {
            delay = new Duration( Long.parseLong( properties.getProperty( "delay" ) ) );

            if ( delay.getMillis() <= 0L )
            {
                throw new IllegalArgumentException( "delay can't be <=0. Should be null or empty." );
            }
        }

        // Load transforms IN
        transformsIN.clear();
        final List<HierarchicalConfiguration> cTransformsIN = cNode.configurationsAt( "in.transform" );
        if ( cTransformsIN != null )
        {
            for ( final HierarchicalConfiguration cTransform : cTransformsIN )
            {
                final String[] commons;

                final String commonsStr = cTransform.getString( "[@commons]" );
                if ( commonsStr == null )
                {
                    commons = new String[]
                    {
                    };
                }
                else
                {
                    commons = commonsStr.split( "," );
                }

                final A_Transform transform = addTransformIN( cTransform.getString( "[@type]" ) ,
                                                              commons );

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
                final String[] commons;

                final String commonsStr = cTransform.getString( "[@commons]" );
                if ( commonsStr == null )
                {
                    commons = new String[]
                    {
                    };
                }
                else
                {
                    commons = commonsStr.split( "," );
                }

                final A_Transform transform = addTransformOUT( cTransform.getString( "[@type]" ) ,
                                                               commons );

                transform.load( cTransform );
            }
        }
    }

    /**
     * Add a transform to the node input.
     *
     * @param clazz Transform's type class
     * @param commonsPropertiesID commons properties set reference
     * @return the transform
     */
    public A_Transform addTransformIN( final Class<?> clazz ,
                                       final String... commonsPropertiesID )
    {
        return addTransformIN( clazz.getName() ,
                               commonsPropertiesID );
    }

    /**
     * Add a transform to the node input.
     *
     * @param type Transform's type
     * @param commonsPropertiesID commons properties set reference
     * @return the transform
     */
    public A_Transform addTransformIN( final String type ,
                                       final String... commonsPropertiesID )
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
            transform.setParameters( this ,
                                     commonsPropertiesID );

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
     * @param clazz Transform's type class
     * @param commonsPropertiesID commons properties set reference
     * @return the transform
     */
    public A_Transform addTransformOUT( final Class<?> clazz ,
                                        final String... commonsPropertiesID )
    {
        return addTransformOUT( clazz.getName() ,
                                commonsPropertiesID );
    }

    /**
     * Add a transform to the node output.
     *
     * @param type Transform's type
     * @param commonsPropertiesID commons properties set reference
     * @return the transform
     */
    public A_Transform addTransformOUT( final String type ,
                                        final String... commonsPropertiesID )
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
            transform.setParameters( this ,
                                     commonsPropertiesID );

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
                    setMessage( null );
                    loop();
                    setMessage( null );
                }
                catch( final InterruptedException ex )
                {
                    // Ignore
                }
                catch( final Throwable ex )
                {
                    getDispatcher().postError( ex ,
                                               message );
                }

                if ( delay != null )
                {
                    try
                    {
                        Thread.sleep( delay.getMillis() );
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
            getDispatcher().postError( th ,
                                       null );
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
     * @throws MessageException
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
     * Send actual message to every connected nodes.
     *
     * @throws java.lang.Exception
     */
    protected void sendMessage()
        throws Exception
    {
        if ( message == null )
        {
            throw new IllegalArgumentException( "Message is not set" );
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

        final Message m;
        synchronized( internalStack )
        {
            while ( internalStack.isEmpty() )
            {
                internalStack.wait();
            }

            m = internalStack.pollLast();
        }

        if ( lastPop != null && antiBurst != null )
        {
            // Null for now
            final Duration elapsed = new Duration( lastPop ,
                                                   null );

            final Duration remaining = antiBurst.minus( elapsed );
            if ( remaining.getMillis() > 0L )
            {
                Thread.sleep( remaining.getMillis() );
            }
        }

        lastPop = new DateTime();

        return m;
    }

    /**
     * Pop the last message.
     *
     * @param timeout max time to wait. If timeout is smaller than antiburst, use antiburst.
     * @return the message (or null if empty)
     * @throws InterruptedException
     */
    protected Message getLastMessageOrWait( final Duration timeout )
        throws InterruptedException
    {
        if ( timeout == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] getLastMessageOrWait() : timeout=" + timeout );
        }

        final Message m;
        synchronized( internalStack )
        {
            DateTime start = new DateTime();
            Duration remaining = timeout;
            while ( internalStack.isEmpty()
                    && remaining.getMillis() > 0L )
            {
                internalStack.wait( remaining.getMillis() );

                final DateTime now = new DateTime();

                final Duration elapsed = new Duration( start ,
                                                       now );

                remaining = remaining.minus( elapsed );

                start = now;
            }

            m = internalStack.pollLast();
        }

        if ( lastPop != null && antiBurst != null )
        {
            // Null for now
            final Duration elapsed = new Duration( lastPop ,
                                                   null );

            final Duration remaining = antiBurst.minus( elapsed );
            if ( remaining.getMillis() > 0L )
            {
                Thread.sleep( remaining.getMillis() );
            }
        }

        lastPop = new DateTime();

        return m;
    }

    protected Message getMessage()
    {
        return message;
    }

    protected void setMessage( final Message message )
    {
        this.message = message;
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( A_Node.class );
    private String nodeID;
    private final Properties properties;
    private final List<String> commonsPropertiesID;
    private Dispatcher dispatcher;
    private final LinkedList<Message> internalStack;
    private volatile boolean activated;
    private final List<A_Transform> transformsIN;
    private final List<A_Transform> transformsOUT;
    private DateTime lastPop;
    private Duration antiBurst;
    private Duration delay;
    private Message message;
}
