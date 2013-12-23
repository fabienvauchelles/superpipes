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

import com.vaushell.spipes.tools.scribe.code.VC_FileFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Daemon for JSVC.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class DaemonApp
    implements Daemon
{
    // PUBLIC
    public DaemonApp()
    {
        this.dispatcher = new Dispatcher();
    }

    @Override
    public void init( final DaemonContext context )
        throws Exception
    {
        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "[" + getClass().getSimpleName() + "] init()" );
        }

        final String[] args = context.getArguments();

        // My config
        final XMLConfiguration config = new XMLConfiguration();
        config.setDelimiterParsingDisabled( true );

        final Path datas;
        switch( args.length )
        {
            case 1:
            {
                config.load( args[ 0] );
                datas = Paths.get( "datas" );
                break;
            }

            case 2:
            {
                config.load( args[ 0] );
                datas = Paths.get( args[ 1] );
                break;
            }

            default:
            {
                config.load( "conf/configuration.xml" );
                datas = Paths.get( "datas" );
                break;
            }
        }

        dispatcher.init( config ,
                         datas ,
                         new VC_FileFactory( datas ) );
    }

    @Override
    public void start()
        throws Exception
    {
        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "[" + getClass().getSimpleName() + "] start()" );
        }

        dispatcher.start();
    }

    @Override
    public void stop()
        throws Exception
    {
        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "[" + getClass().getSimpleName() + "] stop()" );
        }

        dispatcher.stopAndWait();
    }

    @Override
    public void destroy()
    {
        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug( "[" + getClass().getSimpleName() + "] destroy()" );
        }
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( Daemon.class );
    private final Dispatcher dispatcher;
}
