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

package com.vaushell.spipes.nodes.stub;

import com.vaushell.spipes.nodes.A_Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log message.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_MessageLogger
    extends A_Node
{
    // PUBLIC
    public N_MessageLogger()
    {
        super( null ,
               null );
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        // Nothing
    }

    @Override
    protected void loop()
        throws InterruptedException
    {
        setMessage( getLastMessageOrWait() );

        if ( LOGGER.isInfoEnabled() )
        {
            LOGGER.info( "[" + getNodeID() + "] receive message : " + getMessage() );
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_MessageLogger.class );
}
