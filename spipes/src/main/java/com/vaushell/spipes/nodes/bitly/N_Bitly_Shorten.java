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
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_Bitly_Shorten
    extends A_Node
{
    // PUBLIC
    public N_Bitly_Shorten()
    {
        super();
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
        // Nothing
    }

    // PROTECTED
    @Override
    protected void loop()
        throws InterruptedException , URISyntaxException
    {
        // Receive
        final I_URIshorten message = (I_URIshorten) getLastMessageOrWait();

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        final URI longURI = message.getURI();
        if ( longURI != null )
        {
            final Url url = bitly.call( Bitly.shorten( longURI.toString() ) );

            if ( url != null && url.getShortUrl() != null )
            {
                message.setURI( new URI( url.getShortUrl() ) );
                message.setURIsource( longURI );
            }
        }

        sendMessage( message );
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_Bitly_Shorten.class );
    private Provider bitly;
}
