/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.stub;

import com.vaushell.spipes.nodes.A_Message;
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

    // PROTECTED
    @Override
    protected void prepare()
            throws Exception
    {
    }

    @Override
    protected void loop()
            throws InterruptedException
    {
        A_Message message = getLastMessageOrWait();

        if ( logger.isInfoEnabled() )
        {
            logger.info( "[" + getNodeID() + "] receive message : " + message );
        }
    }

    @Override
    protected void terminate()
            throws Exception
    {
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_MessageLogger.class );
}
