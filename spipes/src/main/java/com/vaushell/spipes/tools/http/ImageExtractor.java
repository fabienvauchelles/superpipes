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

package com.vaushell.spipes.tools.http;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extract biggest image of a webpage.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class ImageExtractor
{
    // PUBLIC
    public ImageExtractor( final CloseableHttpClient client )
    {
        this.client = client;
    }

    /**
     * Return the biggest image URI of this webpage.
     *
     * @param rootURI Webpage URI
     * @return Biggest image
     * @throws IOException
     */
    public BufferedImage extractBiggest( final URI rootURI )
        throws IOException
    {
        final List<URI> imagesURIs = new ArrayList<>();
        HttpEntity responseEntity = null;
        try
        {
            // Exec request
            final HttpGet get = new HttpGet( rootURI );

            try( final CloseableHttpResponse response = client.execute( get ) )
            {
                final StatusLine sl = response.getStatusLine();
                if ( sl.getStatusCode() != 200 )
                {
                    throw new IOException( sl.getReasonPhrase() );
                }

                responseEntity = response.getEntity();

                try( final InputStream is = responseEntity.getContent() )
                {
                    final Document doc = Jsoup.parse( is ,
                                                      "UTF-8" ,
                                                      rootURI.toString() );

                    final Elements elts = doc.select( "img" );
                    if ( elts != null )
                    {
                        for ( final Element elt : elts )
                        {
                            final String src = elt.attr( "src" );
                            if ( src != null && !src.isEmpty() )
                            {
                                try
                                {
                                    imagesURIs.add( rootURI.resolve( src ) );
                                }
                                catch( final IllegalArgumentException ex )
                                {
                                    // Ignore wrong encoded URI
                                }
                            }
                        }
                    }
                }
            }
        }
        finally
        {
            if ( responseEntity != null )
            {
                EntityUtils.consume( responseEntity );
            }
        }

        final BufferedImage[] images = new BufferedImage[ imagesURIs.size() ];
        final ExecutorService service = Executors.newCachedThreadPool();
        for ( int i = 0 ; i < imagesURIs.size() ; ++i )
        {
            final int num = i;

            service.execute( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        images[ num] = loadPicture( imagesURIs.get( num ) );
                    }
                    catch( final IOException ex )
                    {
                        images[ num] = null;
                    }
                }
            } );
        }

        service.shutdown();

        try
        {
            service.awaitTermination( 1L ,
                                      TimeUnit.DAYS );
        }
        catch( final InterruptedException ex )
        {
            // Ignore
        }

        BufferedImage biggest = null;
        int biggestSize = Integer.MIN_VALUE;
        for ( int i = 0 ; i < imagesURIs.size() ; ++i )
        {
            if ( images[i] != null )
            {
                final int actualSize = images[ i].getWidth() * images[i].getHeight();
                if ( actualSize > biggestSize )
                {
                    biggest = images[ i];

                    biggestSize = actualSize;
                }
            }
        }

        return biggest;
    }

    // PRIVATE
    private final CloseableHttpClient client;

    private BufferedImage loadPicture( final URI uri )
        throws IOException
    {
        HttpEntity responseEntity = null;
        try
        {
            // Exec request
            final HttpGet get = new HttpGet( uri );

            try( final CloseableHttpResponse response = client.execute( get ) )
            {
                final StatusLine sl = response.getStatusLine();
                if ( sl.getStatusCode() != 200 )
                {
                    throw new IOException( sl.getReasonPhrase() );
                }

                responseEntity = response.getEntity();

                final Header ct = responseEntity.getContentType();
                if ( ct == null )
                {
                    return null;
                }

                final String type = ct.getValue();
                if ( type == null )
                {
                    return null;
                }

                if ( !type.startsWith( "image/" ) )
                {
                    return null;
                }

                try( final ByteArrayOutputStream bos = new ByteArrayOutputStream() )
                {
                    try( final InputStream is = responseEntity.getContent() )
                    {
                        IOUtils.copy( is ,
                                      bos );
                    }

                    if ( bos.size() <= 0 )
                    {
                        return null;
                    }
                    else
                    {
                        try( final ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() ) )
                        {
                            return ImageIO.read( bis );
                        }
                    }
                }
            }
        }
        finally
        {
            if ( responseEntity != null )
            {
                EntityUtils.consume( responseEntity );
            }
        }
    }
}
