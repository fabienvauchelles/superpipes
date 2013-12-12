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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class App
{
    // PUBLIC
    public static void main( String[] args )
    {
        try
        {
            // Config
            Dispatcher dispatcher = new Dispatcher();

            XMLConfiguration config = new XMLConfiguration( "conf/configuration.xml" );
            dispatcher.load( config );

            // Run
            dispatcher.start();

            // Wait
            try
            {
                Thread.sleep( 1000 * 10 );
            }
            catch( InterruptedException ignore )
            {
            }

            // Stop
            dispatcher.stopAndWait();
        }
        catch( Exception ex )
        {
            logger.error( "[Main] Error" ,
                          ex );
        }
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( App.class );
}
