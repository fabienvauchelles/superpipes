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

package com.vaushell.superpipes.dispatch;

import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.tools.ThrowableHelper;
import com.vaushell.superpipes.tools.scribe.code.A_ValidatorCode;
import java.nio.file.Files;
import java.nio.file.Path;
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
        this.commonsProperties = new HashMap<>();
        this.datas = null;
        this.vCodeFactory = null;
        this.eMailer = new ErrorMailer();
    }

    public Path getDatas()
    {
        return datas;
    }

    public A_ValidatorCode.I_Factory getVCodeFactory()
    {
        return vCodeFactory;
    }

    /**
     * Return common properties set.
     *
     * @param ID Common ID
     * @return Common properties set
     */
    public Properties getCommon( final String ID )
    {
        return commonsProperties.get( ID );
    }

    /**
     * Add a node to the flow.
     *
     * @param nodeID Node's ID
     * @param clazz Node's type class
     * @param commonsPropertiesID commons properties set reference
     * @return the node
     */
    public A_Node addNode( final String nodeID ,
                           final Class<?> clazz ,
                           final String... commonsPropertiesID )
    {
        return addNode( nodeID ,
                        clazz.getName() ,
                        commonsPropertiesID );
    }

    /**
     * Add a node to the flow.
     *
     * @param nodeID Node's ID
     * @param type Node's type
     * @param commonsPropertiesID commons properties set reference
     * @return the node
     */
    public A_Node addNode( final String nodeID ,
                           final String type ,
                           final String... commonsPropertiesID )
    {
        if ( nodeID == null || type == null || commonsPropertiesID == null )
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
                "[" + getClass().getSimpleName() + "] addNode : nodeID=" + nodeID + " / type=" + type + " / commonsPropertiesID.length=" + commonsPropertiesID.length );
        }

        try
        {
            final A_Node node = (A_Node) Class.forName( type ).newInstance();
            node.setParameters( nodeID ,
                                this ,
                                commonsPropertiesID );

            nodes.put( nodeID ,
                       node );

            return node;
        }
        catch( final ClassNotFoundException |
                     IllegalAccessException |
                     InstantiationException ex )
        {
            throw new RuntimeException( ex );
        }
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
     * Add common configuration.
     *
     * @param ID Common ID
     * @param properties The properties
     */
    public void addCommon( final String ID ,
                           final Properties properties )
    {
        if ( ID == null || properties == null || properties.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] addCommon : ID=" + ID + " / properties=" + properties );
        }

        commonsProperties.put( ID ,
                               properties );
    }

    /**
     * Post an error.
     *
     * @param th the error
     * @param message the message (could be null)
     */
    public void postError( final Throwable th ,
                           final Message message )
    {
        if ( th == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace(
                "[" + getClass().getSimpleName() + "] postError : th=" + th + " / message=" + Message.formatSimple( message ) );
        }

        if ( message == null )
        {
            LOGGER.error( ThrowableHelper.formatPlainText( th ) );

            eMailer.receiveError( ThrowableHelper.formatHTML( th ) );
        }
        else
        {
            LOGGER.error( String.format( "%s%n%s" ,
                                         ThrowableHelper.formatPlainText( th ) ,
                                         Message.formatPlainText( message ) ) );

            eMailer.receiveError( String.format( "%s%n%s%n" ,
                                                 ThrowableHelper.formatHTML( th ) ,
                                                 Message.formatHTML( message ) ) );
        }
    }

    /**
     * Start the dispatcher.
     *
     * @throws Exception
     */
    public void start()
        throws Exception
    {
        if ( LOGGER.isInfoEnabled() )
        {
            LOGGER.info(
                "[" + getClass().getSimpleName() + "] start" );
        }

        // Prepare nodes
        for ( final A_Node node : nodes.values() )
        {
            node.prepare();
        }

        // Start error mailer
        eMailer.start();

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
        if ( LOGGER.isInfoEnabled() )
        {
            LOGGER.info(
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

        // Error mailer
        eMailer.stopMe();
        eMailer.join();

        for ( final A_Node node : nodes.values() )
        {
            node.terminate();
        }
    }

    /**
     * Send a message, from a node.
     *
     * @param sourceID source node ID
     * @param message message
     * @throws java.lang.Exception
     */
    public void sendMessage( final String sourceID ,
                             final Message message )
        throws Exception
    {

        if ( sourceID == null || message == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] sendMessage : sourceID=" + sourceID + " / message=" + Message.
                formatSimple( message ) );
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
     * Init configuration.
     *
     * @param config Configuration
     * @param datas Path to store datas
     * @param vCodeFactory Valication code factory
     * @throws java.lang.Exception
     */
    public void init( final XMLConfiguration config ,
                      final Path datas ,
                      final A_ValidatorCode.I_Factory vCodeFactory )
        throws Exception
    {
        if ( config == null || datas == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isInfoEnabled() )
        {
            LOGGER.info( "[" + getClass().getSimpleName() + "] load() : datas=" + datas );
        }

        if ( Files.notExists( datas ) )
        {
            Files.createDirectory( datas );
        }
        else
        {
            if ( !Files.isDirectory( datas ) )
            {
                throw new IllegalArgumentException( "Path '" + datas.toString() + "' should be a directory" );
            }
        }

        this.datas = datas;
        this.vCodeFactory = vCodeFactory;

        // Load mailer
        eMailer.load( config.configurationAt( "mailer" ) );

        // Load commons
        commonsProperties.clear();
        final List<HierarchicalConfiguration> cCommons = config.configurationsAt( "commons.common" );
        if ( cCommons != null )
        {
            for ( final HierarchicalConfiguration cCommon : cCommons )
            {
                final Properties cProperties = new Properties();
                readProperties( cProperties ,
                                cCommon );

                addCommon( cCommon.getString( "[@id]" ) ,
                           cProperties );
            }
        }

        // Load nodes
        nodes.clear();
        final List<HierarchicalConfiguration> cNodes = config.configurationsAt( "nodes.node" );
        if ( cNodes != null )
        {
            for ( final HierarchicalConfiguration cNode : cNodes )
            {
                final String[] commons;

                final String commonsStr = cNode.getString( "[@commons]" );
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

                final A_Node node = addNode( cNode.getString( "[@id]" ) ,
                                             cNode.getString( "[@type]" ) ,
                                             commons );

                node.load( cNode );
            }
        }

        // Load routes
        routes.clear();
        final List<HierarchicalConfiguration> cRoutes = config.configurationsAt( "routes.route" );
        if ( cRoutes != null )
        {
            for ( final HierarchicalConfiguration cRoute : cRoutes )
            {
                final String sourceID = cRoute.getString( "[@source]" );
                final String destinationID = cRoute.getString( "[@destination]" );

                addRoute( sourceID ,
                          destinationID );
            }
        }
    }

    /**
     * Read configurations properties. Must inside tags 'params' with 'param'
     *
     * @param props Properties to fill
     * @param cNode Configuration
     */
    public static void readProperties( final Properties props ,
                                       final HierarchicalConfiguration cNode )
    {
        props.clear();

        if ( cNode != null )
        {
            final List<HierarchicalConfiguration> hConfs = cNode.configurationsAt( "params.param" );
            if ( hConfs != null )
            {
                for ( final HierarchicalConfiguration hConf : hConfs )
                {
                    final String name = hConf.getString( "[@name]" );
                    final String value = hConf.getString( "[@value]" );

                    if ( name != null && name.length() > 0 && value != null && value.length() > 0 )
                    {
                        props.put( name ,
                                   value );
                    }
                }
            }
        }
    }

    // DEFAULT
    final HashMap<String , A_Node> nodes;
    final HashMap<String , Set<String>> routes;
    final ErrorMailer eMailer;

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( Dispatcher.class );
    private final HashMap<String , Properties> commonsProperties;
    private Path datas;
    private A_ValidatorCode.I_Factory vCodeFactory;
}
