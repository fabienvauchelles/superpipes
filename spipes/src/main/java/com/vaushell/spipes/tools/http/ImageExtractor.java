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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
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
     * @param uri Webpage URI
     * @return Biggest image URI
     * @throws IOException
     */
    public URI extractBiggestPicture( final URI uri )
        throws IOException
    {
        final List<URI> imagesURIs = new ArrayList<>();
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

                try( final InputStream is = responseEntity.getContent() )
                {
                    final Document doc = Jsoup.parse( is ,
                                                      "UTF-8" ,
                                                      uri.toString() );

                    final Elements elts = doc.select( "img" );
                    if ( elts != null )
                    {
                        for ( final Element elt : elts )
                        {
                            final String src = elt.attr( "src" );
                            if ( src != null && !src.isEmpty() )
                            {
                                imagesURIs.add( uri.resolve( src ) );
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

        final Dimension[] dimensions = new Dimension[ imagesURIs.size() ];
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
                        dimensions[ num] = extractPictureSize( imagesURIs.get( num ) );
                    }
                    catch( final IOException ex )
                    {
                        dimensions[ num] = null;
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

        URI biggest = null;
        int biggestSize = Integer.MIN_VALUE;
        for ( int i = 0 ; i < imagesURIs.size() ; ++i )
        {
            if ( dimensions[i] != null )
            {
                final int actualSize = dimensions[ i].width * dimensions[i].height;
                if ( actualSize > biggestSize )
                {
                    biggest = imagesURIs.get( i );
                    biggestSize = actualSize;
                }
            }
        }

        return biggest;
    }

    // PRIVATE
    private final CloseableHttpClient client;

    private Dimension extractPictureSize( final URI uri )
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

                try( final InputStream is = responseEntity.getContent() )
                {
                    final BufferedImage image = ImageIO.read( is );
                    if ( image == null )
                    {
                        return null;
                    }

                    return new Dimension( image.getWidth() ,
                                          image.getHeight() );
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
