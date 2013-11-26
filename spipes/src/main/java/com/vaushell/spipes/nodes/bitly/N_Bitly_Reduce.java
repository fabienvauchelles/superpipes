/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.bitly;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.Url;
import com.vaushell.spipes.nodes.A_Node;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_Bitly_Reduce
        extends A_Node
{
    // PUBLIC
    public N_Bitly_Reduce()
    {
        this.bitly = null;
    }

    @Override
    public void prepare()
            throws Exception
    {
        // https://bitly.com/a/your_api_key
        this.bitly = Bitly.as( getConfig( "username" ) ,
                               getConfig( "apikey" ) );

    }

    @Override
    public void terminate()
            throws Exception
    {
    }
    // PROTECTED

    @Override
    protected void loop()
            throws InterruptedException , URISyntaxException
    {
        // Receive
        I_URI message = (I_URI) getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        URI longURI = message.getURI();
        if ( longURI != null )
        {
            Url url = bitly.call( Bitly.shorten( longURI.toString() ) );

            if ( url != null && url.getShortUrl() != null )
            {
                message.setURI( new URI( url.getShortUrl() ) );
            }
        }

        sendMessage( message );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_Bitly_Reduce.class );
    private Provider bitly;
}
