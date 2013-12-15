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

import org.apache.commons.configuration.XMLConfiguration;

/**
 * Main class.
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

        if ( args.length > 0 )
        {
            config.load( args[ 0] );
        }
        else
        {
            config.load( "conf/configuration.xml" );
        }

        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.load( config );

        // Run
        dispatcher.start();

        // Wait
        try
        {
            Thread.sleep( 1000 * 10 );
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
