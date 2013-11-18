/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes;

import com.vaushell.spipes.nodes.A_Node;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
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
        this.nodes = new HashMap<>();
        this.routes = new HashMap<>();
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

    public void addRoutes( String sourceID ,
                           String... destinationsID )
    {
        if ( sourceID == null || destinationsID == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] addRoutes : sourceID=" + sourceID + " / destinationsID=" + StringUtils.
                    join( destinationsID ,
                          "," ) );
        }

        if ( destinationsID.length <= 0 )
        {
            return;
        }

        if ( !nodes.containsKey( sourceID ) )
        {
            throw new IllegalArgumentException( "Cannot find route source '" + sourceID + "'" );
        }

        for ( String destinationID : destinationsID )
        {
            if ( !nodes.containsKey( destinationID ) )
            {
                throw new IllegalArgumentException( "Cannot find route destination '" + destinationID + "'" );
            }
        }

        Set<String> subRoutes = routes.get( sourceID );
        if ( subRoutes == null )
        {
            subRoutes = new HashSet<>();
            routes.put( sourceID ,
                        subRoutes );
        }

        for ( String destinationID : destinationsID )
        {
            subRoutes.add( destinationID );
        }
    }

    public void start()
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace(
                    "[" + getClass().getSimpleName() + "] start" );
        }

        for ( A_Node node : nodes.values() )
        {
            node.start();
        }
    }

    public void stopAndWait()
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace(
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

    public void sendMessages( String sourceID ,
                              Collection messages )
    {

        if ( sourceID == null || messages == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getClass().getSimpleName() + "] sendMessages : sourceID=" + sourceID + " / messages=" + messages );
        }

        if ( messages.isEmpty() )
        {
            return;
        }

        Set<String> subRoutes = routes.get( sourceID );
        if ( subRoutes == null )
        {
            throw new IllegalArgumentException( "Cannot find route source '" + sourceID + "'" );
        }

        for ( String destinationID : subRoutes )
        {
            A_Node destination = nodes.get( destinationID );

            destination.receiveMessages( messages );
        }

    }

    public void load( XMLConfiguration config )
    {
        if ( config == null )
        {
            throw new NullPointerException();
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getClass().getSimpleName() + "] load" );
        }

        // Load nodes
        List<HierarchicalConfiguration> cNodes = config.configurationsAt( "nodes.node" );

        if ( cNodes != null )
        {
            for ( HierarchicalConfiguration cNode : cNodes )
            {
                String nodeID = cNode.getString( "id" );
                String type = cNode.getString( "type" );

                Properties properties = new Properties();

                List<HierarchicalConfiguration> hConfs = cNode.configurationsAt( "param" );
                if ( hConfs != null )
                {
                    for ( HierarchicalConfiguration hConf : hConfs )
                    {
                        String name = hConf.getString( "[@name]" );
                        String value = hConf.getString( "[@value]" );

                        if ( name != null && name.length() > 0 && value != null && value.length() > 0 )
                        {
                            properties.put( name ,
                                            value );
                        }
                    }
                }

                addNode( nodeID ,
                         type ,
                         properties );
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
                    addRoutes( sourceID ,
                               destinationsID );
                }
            }
        }
    }
    // DEFAULT
    HashMap<String , A_Node> nodes;
    HashMap<String , Set<String>> routes;
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( Dispatcher.class );
}
