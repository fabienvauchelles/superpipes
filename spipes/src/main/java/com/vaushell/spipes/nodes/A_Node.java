/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes;

import com.vaushell.spipes.Dispatcher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public abstract class A_Node
        extends Thread
{
    // PUBLIC
    public A_Node()
    {
        this.nodeID = null;
        this.properties = properties;
        this.dispatcher = null;
        this.activated = true;
        this.internalStack = new LinkedList<>();
    }

    public void config( String nodeID ,
                        Properties properties ,
                        Dispatcher dispatcher )
    {
        this.nodeID = nodeID;
        this.properties = properties;
        this.dispatcher = dispatcher;
    }

    @Override
    public abstract void run();

    public void receiveMessages( Collection messages )
    {
        if ( messages == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receiveMessages : messages.size=" + messages.size() );
        }

        if ( messages.isEmpty() )
        {
            return;
        }

        synchronized( internalStack )
        {
            for ( Object message : messages )
            {
                internalStack.addFirst( message );
            }

            internalStack.notify();
        }
    }

    public synchronized void stopMe()
    {
        activated = false;

        interrupt();
    }

    // PROTECTED
    protected String getNodeID()
    {
        return nodeID;
    }

    protected String getValue( String key )
    {
        return properties.getProperty( key );
    }

    protected void sendMessages( Collection messages )
    {
        if ( messages == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] sendMessages : messages.size=" + messages.size() );
        }

        if ( messages.isEmpty() )
        {
            return;
        }

        dispatcher.sendMessages( nodeID ,
                                 messages );
    }

    protected void sendMessage( Object message )
    {
        if ( message == null )
        {
            throw new NullPointerException();
        }

        List messages = new ArrayList();
        messages.add( message );

        sendMessages( messages );
    }

    protected synchronized boolean isActive()
    {
        return activated;
    }

    protected Object getLastMessageOrWait()
            throws InterruptedException
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] getLastMessageOrWait" );
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
    private final static Logger logger = LoggerFactory.getLogger( A_Node.class );
    private String nodeID;
    private Properties properties;
    private Dispatcher dispatcher;
    private final LinkedList internalStack;
    private volatile boolean activated;
}
