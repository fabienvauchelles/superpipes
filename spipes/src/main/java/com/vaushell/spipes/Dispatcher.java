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

package com.vaushell.spipes;

import com.vaushell.spipes.nodes.A_Node;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Flow dispatcher.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class Dispatcher
{
    // PUBLIC
    public Dispatcher()
    {
        this.nodes = new HashMap<>();
        this.routes = new HashMap<>();
    }

    /**
     * Retrieve main's parameter.
     *
     * @param key Key of parameter
     * @return the value
     */
    public String getConfig( final String key )
    {
        return properties.getProperty( key );
    }

    /**
     * Add a node to the flow.
     *
     * @param nodeID Node's ID
     * @param type Node's type
     * @param properties Node's properties
     */
    public void addNode( final String nodeID ,
                         final String type ,
                         final Properties properties )
    {
        if ( nodeID == null || type == null || properties == null )
        {
            throw new IllegalArgumentException();
        }

        if ( nodes.containsKey( nodeID ) )
        {
            throw new IllegalArgumentException( "node '" + nodeID + "' already exists" );
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] addNode : nodeID=" + nodeID + " / type=" + type + " / properties.size=" + properties.
                size() );
        }

        try
        {
            final A_Node node = (A_Node) Class.forName( type ).newInstance();
            node.config( nodeID ,
                         properties ,
                         this );

            nodes.put( nodeID ,
                       node );
        }
        catch( final ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    /**
     * Add a node to the flow.
     *
     * @param nodeID Node's ID
     * @param clazz Node's type class
     * @param properties Node's properties
     */
    public void addNode( final String nodeID ,
                         final Class<?> clazz ,
                         final Properties properties )
    {
        addNode( nodeID ,
                 clazz.getName() ,
                 properties );
    }

    /**
     * Add a route between 2 nodes.
     *
     * @param sourceID source node ID
     * @param destinationID destination node ID
     */
    public void addRoute( final String sourceID ,
                          final String destinationID )
    {
        if ( sourceID == null || destinationID == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] addRoute : sourceID=" + sourceID + " / destinationID=" + destinationID );
        }

        if ( !nodes.containsKey( sourceID ) )
        {
            throw new IllegalArgumentException( "Cannot find route source '" + sourceID + "'" );
        }

        if ( !nodes.containsKey( destinationID ) )
        {
            throw new IllegalArgumentException( "Cannot find route destination '" + destinationID + "'" );
        }

        Set<String> subRoutes = routes.get( sourceID );
        if ( subRoutes == null )
        {
            subRoutes = new HashSet<>();
            routes.put( sourceID ,
                        subRoutes );
        }

        subRoutes.add( destinationID );
    }

    /**
     * Start the dispatcher.
     *
     * @throws Exception
     */
    public void start()
        throws Exception
    {
        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug(
                "[" + getClass().getSimpleName() + "] start" );
        }

        // Prepare nodes
        for ( final A_Node node : nodes.values() )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + node.getNodeID() + "] prepare" );
            }
            node.prepare();
        }

        // Start nodes
        for ( final A_Node node : nodes.values() )
        {
            node.start();
        }
    }

    /**
     * Stop the dispatcher and wait all nodes to stop.
     *
     * @throws Exception
     */
    public void stopAndWait()
        throws Exception
    {
        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug(
                "[" + getClass().getSimpleName() + "] stopAndWait" );
        }

        for ( final A_Node node : nodes.values() )
        {
            node.stopMe();
        }

        for ( final A_Node node : nodes.values() )
        {
            try
            {
                node.join();
            }
            catch( final InterruptedException ex )
            {
                // Ignore
            }
        }

        for ( final A_Node node : nodes.values() )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace( "[" + node.getNodeID() + "] terminate" );
            }
            node.terminate();
        }
    }

    /**
     * Send a message, from a node.
     *
     * @param sourceID source node ID
     * @param message message
     */
    public void sendMessage( final String sourceID ,
                             final Object message )
    {

        if ( sourceID == null || message == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] sendMessage : sourceID=" + sourceID + " / message=" + message );
        }

        final Set<String> subRoutes = routes.get( sourceID );
        if ( subRoutes != null )
        {
            for ( final String destinationID : subRoutes )
            {
                final A_Node destination = nodes.get( destinationID );

                destination.receiveMessage( message );
            }
        }

    }

    /**
     * Load configuration.
     *
     * @param config Configuration
     */
    public void load( final XMLConfiguration config )
    {
        if ( config == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "[" + getClass().getSimpleName() + "] load" );
        }

        // Load general configuration
        properties = readProperties( config.configurationAt( "general" ) );

        // Load nodes
        final List<HierarchicalConfiguration> cNodes = config.configurationsAt( "nodes.node" );

        if ( cNodes != null )
        {
            for ( final HierarchicalConfiguration cNode : cNodes )
            {
                final Properties nodeProperties = readProperties( cNode );

                addNode( cNode.getString( "id" ) ,
                         cNode.getString( "type" ) ,
                         nodeProperties );
            }
        }

        // Load routes
        final List<HierarchicalConfiguration> cRoutes = config.configurationsAt( "routes.route" );

        if ( cRoutes != null )
        {
            for ( final HierarchicalConfiguration cRoute : cRoutes )
            {
                final String[] sourcesID = cRoute.getStringArray( "source" );
                final String[] destinationsID = cRoute.getStringArray( "destination" );

                for ( final String sourceID : sourcesID )
                {
                    for ( final String destinationID : destinationsID )
                    {
                        addRoute( sourceID ,
                                  destinationID );
                    }
                }
            }
        }
    }

    // DEFAULT
    final HashMap<String , A_Node> nodes;
    final HashMap<String , Set<String>> routes;

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( Dispatcher.class );
    private Properties properties;

    private Properties readProperties( final HierarchicalConfiguration node )
    {
        final Properties nodeProperties = new Properties();

        final List<HierarchicalConfiguration> hConfs = node.configurationsAt( "param" );
        if ( hConfs != null )
        {
            for ( final HierarchicalConfiguration hConf : hConfs )
            {
                final String name = hConf.getString( "[@name]" );
                final String value = hConf.getString( "[@value]" );

                if ( name != null && name.length() > 0 && value != null && value.length() > 0 )
                {
                    nodeProperties.put( name ,
                                        value );
                }
            }
        }

        return nodeProperties;
    }
}
