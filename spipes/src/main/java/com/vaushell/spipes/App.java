package com.vaushell.spipes;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Main
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class App
{
    // PUBLIC
    public static void main( String[] args )
    {
        // Config
        Dispatcher dispatcher = new Dispatcher();

        try
        {
            XMLConfiguration config = new XMLConfiguration( "conf/configuration.xml" );
            dispatcher.load( config );
        }
        catch( ConfigurationException ex )
        {
            throw new RuntimeException( ex );
        }

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
    // PRIVATE
}
