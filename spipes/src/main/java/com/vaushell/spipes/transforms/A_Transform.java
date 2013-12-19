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

package com.vaushell.spipes.transforms;

import com.vaushell.spipes.Dispatcher;
import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import java.util.Properties;
import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * A transform filter.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public abstract class A_Transform
{
    // PUBLIC
    public A_Transform()
    {
        this.node = null;
        this.properties = new Properties();
    }

    public void setParent( final A_Node node )
    {
        this.node = node;
    }

    public Properties getProperties()
    {
        return properties;
    }

    /**
     * Load configuration for this transform.
     *
     * @param cNode Configuration
     * @throws Exception
     */
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        Dispatcher.readProperties( properties ,
                                   cNode );
    }

    /**
     * Prepare transform's execution. Executed 1 time at the beginning.
     *
     * @throws Exception
     */
    public abstract void prepare()
        throws Exception;

    /**
     * Transform's execution. Executed for each message.
     *
     * @param message the Message
     * @return the transformed message
     * @throws Exception
     */
    public abstract Message transform( final Message message )
        throws Exception;

    /**
     * Close transform's execution. Executed 1 time at the ending.
     *
     * @throws Exception
     */
    public abstract void terminate()
        throws Exception;

    // PROTECTED
    protected String getNodeID()
    {
        return node.getNodeID();
    }

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
     * Retrieve node's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    protected String getNodeConfig( final String key )
    {
        return node.getConfig( key );
    }

    /**
     * Retrieve main's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    protected String getMainConfig( final String key )
    {
        return node.getMainConfig( key );
    }

    // PRIVATE
    private A_Node node;
    private final Properties properties;
}
