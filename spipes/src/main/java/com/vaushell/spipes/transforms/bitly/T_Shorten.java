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

package com.vaushell.spipes.transforms.bitly;

import com.rosaloves.bitlyj.Bitly;
import com.rosaloves.bitlyj.Bitly.Provider;
import com.rosaloves.bitlyj.Url;
import com.vaushell.spipes.Message;
import com.vaushell.spipes.transforms.A_Transform;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_Shorten
    extends A_Transform
{
    // PUBLIC
    public T_Shorten()
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
    public Message transform( final Message message )
        throws Exception
    {
        // Receive
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + message );
        }

        if ( message.contains( "uri" ) )
        {
            final URI longURI = (URI) message.getProperty( "uri" );

            final Url url = bitly.call( Bitly.shorten( longURI.toString() ) );

            if ( url != null && url.getShortUrl() != null )
            {
                message.setProperty( "uri" ,
                                     new URI( url.getShortUrl() ) );
                message.setProperty( "uri-source" ,
                                     longURI );
            }
        }

        return message;
    }

    @Override
    public void terminate()
        throws Exception
    {
        // Nothing
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( T_Shorten.class );
    private Provider bitly;
}
