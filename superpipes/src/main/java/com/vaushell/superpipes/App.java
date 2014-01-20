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

package com.vaushell.superpipes;

import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.tools.scribe.code.VC_SystemInputFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Main class. For development purpose.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class App
{
    // PUBLIC
    /**
     * Main method.
     *
     * @param args Command line arguments
     * @throws Exception
     */
    public static void main( final String... args )
        throws Exception
    {
        // My config
        final XMLConfiguration config = new XMLConfiguration();
        config.setDelimiterParsingDisabled( true );

        final long delay;
        final Path datas;
        switch( args.length )
        {
            case 1:
            {
                config.load( args[ 0] );
                datas = Paths.get( "datas" );
                delay = 10000L;

                break;
            }

            case 2:
            {
                config.load( args[ 0] );
                datas = Paths.get( args[ 1] );
                delay = 10000L;

                break;
            }

            case 3:
            {
                config.load( args[ 0] );
                datas = Paths.get( args[ 1] );
                delay = Long.parseLong( args[2] );

                break;
            }

            default:
            {
                config.load( "conf/configuration.xml" );
                datas = Paths.get( "datas" );
                delay = 10000L;

                break;
            }
        }

        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.init( config ,
                         datas ,
                         new VC_SystemInputFactory() );

        // Run
        dispatcher.start();

        // Wait
        try
        {
            Thread.sleep( delay );
        }
        catch( final InterruptedException ex )
        {
            // Ignore
        }

        // Stop
        dispatcher.stopAndWait();
    }

    // PRIVATE
    private App()
    {
        // Nothing
    }
}
