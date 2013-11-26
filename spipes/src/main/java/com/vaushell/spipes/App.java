package com.vaushell.spipes;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
