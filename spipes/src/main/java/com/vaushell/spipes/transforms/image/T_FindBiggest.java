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

package com.vaushell.spipes.transforms.image;

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.tools.http.ImageExtractor;
import com.vaushell.spipes.transforms.A_Transform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import javax.imageio.ImageIO;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Find biggest image in the URI.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_FindBiggest
    extends A_Transform
{
    // PUBLIC
    public T_FindBiggest()
    {
        super();

        this.client = null;
        this.extractor = null;
    }

    @Override
    public void prepare()
        throws Exception
    {
        this.client = HttpClientBuilder
            .create()
            .setDefaultCookieStore( new BasicCookieStore() )
            .setUserAgent( "Mozilla/5.0 (Windows NT 5.1; rv:15.0) Gecko/20100101 Firefox/15.0.1" )
            .build();

        this.extractor = new ImageExtractor( this.client );
    }

    @Override
    public Message transform( final Message message )
        throws Exception
    {
        // Receive
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNode().getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + message );
        }

        if ( message.contains( Message.KeyIndex.URI ) )
        {
            final URI uri = (URI) message.getProperty( Message.KeyIndex.URI );

            try
            {
                final BufferedImage biggest = extractor.extractBiggest( uri );

                if ( biggest != null )
                {
                    try( ByteArrayOutputStream bos = new ByteArrayOutputStream() )
                    {
                        // PNG is lossless
                        ImageIO.write( biggest ,
                                       "png" ,
                                       bos );

                        message.setProperty( Message.KeyIndex.PICTURE ,
                                             bos.toByteArray() );
                    }
                }
            }
            catch( final IOException ex )
            {
                LOGGER.error( "Image extraction error" ,
                              ex );
            }
        }

        return message;
    }

    @Override
    public void terminate()
        throws IOException
    {
        if ( client != null )
        {
            client.close();
        }
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( T_FindBiggest.class );
    private CloseableHttpClient client;
    private ImageExtractor extractor;
}
