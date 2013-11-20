/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class Dispatcher
{
    // PUBLIC
    public Dispatcher()
    {
        this.properties = null;
        this.nodes = new HashMap<>();
        this.routes = new HashMap<>();
    }

    public String getConfig( String key )
    {
        return properties.getProperty( key );
    }

    public void addNode( String nodeID ,
                         String type ,
                         Properties properties )
    {
        if ( nodeID == null || type == null || properties == null )
        {
            throw new NullPointerException();
        }

        if ( nodes.containsKey( nodeID ) )
        {
            throw new IllegalArgumentException( "node '" + nodeID + "' already exists" );
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] addNode : nodeID=" + nodeID + " / type=" + type + " / properties.size=" + properties.
                    size() );
        }

        try
        {
            A_Node node = (A_Node) Class.forName( type ).newInstance();
            node.config( nodeID ,
                         properties ,
                         this );

            nodes.put( nodeID ,
                       node );
        }
        catch( ClassNotFoundException |
               IllegalAccessException |
               InstantiationException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    public void addNode( String nodeID ,
                         Class<?> clazz ,
                         Properties properties )
    {
        addNode( nodeID ,
                 clazz.getName() ,
                 properties );
    }

    public void addRoute( String sourceID ,
                          String destinationID )
    {
        if ( sourceID == null || destinationID == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
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

    public void start()
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug(
                    "[" + getClass().getSimpleName() + "] start" );
        }

        for ( A_Node node : nodes.values() )
        {
            node.start();
        }
    }

    public void stopAndWait()
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug(
                    "[" + getClass().getSimpleName() + "] stopAndWait" );
        }

        for ( A_Node node : nodes.values() )
        {
            node.stopMe();
        }

        for ( A_Node node : nodes.values() )
        {
            try
            {
                node.join();
            }
            catch( InterruptedException ignore )
            {
            }
        }
    }

    public void sendMessage( String sourceID ,
                             Object message )
    {

        if ( sourceID == null || message == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getClass().getSimpleName() + "] sendMessage : sourceID=" + sourceID + " / message=" + message );
        }

        Set<String> subRoutes = routes.get( sourceID );
        if ( subRoutes != null )
        {
            for ( String destinationID : subRoutes )
            {
                A_Node destination = nodes.get( destinationID );

                destination.receiveMessage( message );
            }
        }

    }

    public void load( XMLConfiguration config )
    {
        if ( config == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isDebugEnabled() )
        {
            logger.debug( "[" + getClass().getSimpleName() + "] load" );
        }

        // Load general configuration
        properties = readProperties( config.configurationAt( "general" ) );

        // Load nodes
        List<HierarchicalConfiguration> cNodes = config.configurationsAt( "nodes.node" );

        if ( cNodes != null )
        {
            for ( HierarchicalConfiguration cNode : cNodes )
            {
                String nodeID = cNode.getString( "id" );
                String type = cNode.getString( "type" );
                Properties nodeProperties = readProperties( cNode );

                addNode( nodeID ,
                         type ,
                         nodeProperties );
            }
        }

        // Load routes
        List<HierarchicalConfiguration> cRoutes = config.configurationsAt( "routes.route" );

        if ( cRoutes != null )
        {
            for ( HierarchicalConfiguration cRoute : cRoutes )
            {
                String[] sourcesID = cRoute.getStringArray( "source" );
                String[] destinationsID = cRoute.getStringArray( "destination" );

                for ( String sourceID : sourcesID )
                {
                    for ( String destinationID : destinationsID )
                    {
                        addRoute( sourceID ,
                                  destinationID );
                    }
                }
            }
        }
    }
    // DEFAULT
    HashMap<String , A_Node> nodes;
    HashMap<String , Set<String>> routes;
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( Dispatcher.class );
    private Properties properties;

    private Properties readProperties( HierarchicalConfiguration node )
    {
        Properties nodeProperties = new Properties();

        List<HierarchicalConfiguration> hConfs = node.configurationsAt( "param" );
        if ( hConfs != null )
        {
            for ( HierarchicalConfiguration hConf : hConfs )
            {
                String name = hConf.getString( "[@name]" );
                String value = hConf.getString( "[@value]" );

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
