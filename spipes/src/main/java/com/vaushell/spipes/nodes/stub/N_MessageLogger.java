/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.stub;

import com.vaushell.spipes.nodes.A_Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_MessageLogger
        extends A_Node
{
    // PUBLIC
    public N_MessageLogger()
    {
    }

    @Override
    public void run()
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] start thread " );
        }

        Object message = null;
        while ( isActive() )
        {
            try
            {
                message = getLastMessageOrWait();

                logger.info( "[" + getNodeID() + "] receive message : " + message );
            }
            catch( InterruptedException ignore )
            {
            }
            catch( Throwable th )
            {
                logger.error( "Receive error when processing message : " + message ,
                              th );
            }
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] stop thread" );
        }
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_MessageLogger.class );
}
