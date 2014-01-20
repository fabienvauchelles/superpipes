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

package com.vaushell.superpipes.nodes.shaarli;

import com.vaushell.shaarlijavaapi.ShaarliClient;
import com.vaushell.shaarlijavaapi.ShaarliTemplates;
import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.dispatch.Tags;
import com.vaushell.superpipes.nodes.A_Node;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a RSS feed.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_Shaarli_Post
    extends A_Node
{
    // PUBLIC
    public N_Shaarli_Post()
    {
        super( null ,
               DEFAULT_ANTIBURST );

        this.templates = new ShaarliTemplates();
        this.retry = 3;
        this.delayBetweenRetry = new Duration( 5L * 1000L );
    }

    @Override
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        super.load( cNode );

        final List<HierarchicalConfiguration> cTemplates = cNode.configurationsAt( "templates.template" );
        if ( cTemplates != null )
        {
            for ( final HierarchicalConfiguration cTemplate : cTemplates )
            {
                templates.add( cTemplate.getString( "[@key]" ) ,
                               cTemplate.getString( "[@csspath]" ) ,
                               cTemplate.getString( "[@attribut]" ) ,
                               cTemplate.getString( "[@regex]" ) );
            }
        }

        // Load retry count if exists.
        final String retryStr = getConfig( "retry" ,
                                           true );
        if ( retryStr != null )
        {
            try
            {
                retry = Integer.parseInt( retryStr );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "'retry' must be an integer" ,
                                                    ex );
            }
        }

        // Load delay between retry if exists.
        final String delayBetweenRetryStr = getConfig( "delay-between-retry" ,
                                                       true );
        if ( delayBetweenRetryStr != null )
        {
            try
            {
                delayBetweenRetry = new Duration( Long.parseLong( delayBetweenRetryStr ) );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "'delay-between-retry' must be a long" ,
                                                    ex );
            }
        }
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        this.client = new ShaarliClient( templates ,
                                         getConfig( "url" ,
                                                    false ) );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    protected void loop()
        throws Exception
    {
        // Receive
        setMessage( getLastMessageOrWait() );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + Message.formatSimple( getMessage() ) );
        }

        if ( !getMessage().contains( Message.KeyIndex.URI )
             || !getMessage().contains( Message.KeyIndex.TITLE )
             || !getMessage().contains( Message.KeyIndex.TAGS ) )
        {
            throw new IllegalArgumentException( "message doesn't have an uri, a title or a set of tags" );
        }

        // Send to Shaarli
        // Log in
        if ( !client.login( getConfig( "login" ,
                                       false ) ,
                            getConfig( "password" ,
                                       false ) ) )
        {
            throw new IllegalArgumentException( "Login error" );
        }

        final URI uri = (URI) getMessage().getProperty( Message.KeyIndex.URI );
        final Tags tags = (Tags) getMessage().getProperty( Message.KeyIndex.TAGS );

        final String ID = createLink( uri == null ? null : uri.toString() ,
                                      (String) getMessage().getProperty( Message.KeyIndex.TITLE ) ,
                                      (String) getMessage().getProperty( Message.KeyIndex.DESCRIPTION ) ,
                                      tags == null ? Collections.EMPTY_SET : tags.getAll() ,
                                      retry );

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive ID : " + ID );
        }

        if ( ID != null && !ID.isEmpty() )
        {
            getMessage().setProperty( "id-shaarli" ,
                                      ID );

            sendMessage();
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_Shaarli_Post.class );
    private ShaarliClient client;
    private final ShaarliTemplates templates;
    private int retry;
    private Duration delayBetweenRetry;

    private String createLink( final String uri ,
                               final String title ,
                               final String description ,
                               final Set<String> tags ,
                               final int remainingRetry )
    {
        try
        {
            final String ID = client.createLink( uri ,
                                                 title ,
                                                 description ,
                                                 tags ,
                                                 false );

            if ( ID == null || ID.isEmpty() )
            {
                if ( remainingRetry <= 0 )
                {
                    return null;
                }
            }
            else
            {
                return ID;
            }
        }
        catch( final Throwable ex )
        {
            if ( remainingRetry <= 0 )
            {
                throw ex;
            }
        }

        if ( delayBetweenRetry.getMillis() > 0L )
        {
            try
            {
                Thread.sleep( delayBetweenRetry.getMillis() );
            }
            catch( final InterruptedException ex )
            {
                // Ignore
            }
        }

        return createLink( uri ,
                           title ,
                           description ,
                           tags ,
                           remainingRetry - 1 );
    }
}
