/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes;

import com.vaushell.spipes.Dispatcher;
import java.util.LinkedList;
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
        this.properties = null;
        this.dispatcher = null;
        this.activated = true;
        this.internalStack = new LinkedList<>();
    }

    public abstract void prepare()
            throws Exception;

    public abstract void terminate()
            throws Exception;

    public String getNodeID()
    {
        return nodeID;
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
    public void run()
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "[" + getNodeID() + "] start thread" );
        }

        try
        {
            if ( logger.isTraceEnabled() )
            {
                logger.trace( "[" + getNodeID() + "] loopin'" );
            }
            while ( isActive() )
            {
                try
                {
                    loop();
                }
                catch( InterruptedException ignore )
                {
                }
                catch( Throwable th )
                {
                    logger.error( "Error" ,
                                  th );
                }

                String delayStr = getConfig( "delay" );
                if ( delayStr != null )
                {
                    try
                    {
                        Thread.sleep( Long.parseLong( delayStr ) );
                    }
                    catch( InterruptedException ignore )
                    {
                    }
                }
            }
        }
        catch( Throwable th )
        {
            logger.error( "Error" ,
                          th );
        }

        if ( logger.isDebugEnabled() )
        {
            logger.debug( "[" + getNodeID() + "] stop thread" );
        }
    }

    public void receiveMessage( Object message )
    {
        if ( message == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receiveMessage : message=" + message );
        }

        synchronized( internalStack )
        {
            internalStack.addFirst( message );

            internalStack.notify();
        }
    }

    public synchronized void stopMe()
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] stopMe" );
        }

        activated = false;

        interrupt();
    }

    // PROTECTED
    protected abstract void loop()
            throws Exception;

    protected String getConfig( String key )
    {
        return properties.getProperty( key );
    }

    protected String getMainConfig( String key )
    {
        return dispatcher.getConfig( key );
    }

    protected void sendMessage( Object message )
    {
        if ( message == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] sendMessage : message=" + message );
        }

        dispatcher.sendMessage( nodeID ,
                                message );
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
