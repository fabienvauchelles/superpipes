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

package com.vaushell.spipes.nodes.stub;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import com.vaushell.spipes.tools.ValuesGenerator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generate random news.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_NewsGenerator
    extends A_Node
{
    // PUBLIC
    public N_NewsGenerator()
    {
        super();
    }

    @Override
    public void prepare()
        throws Exception
    {
        // Nothing
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
        throws URISyntaxException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] generate post" );
        }

        final String uriStr = "http://" + ValuesGenerator.getRandomWord( 10 ,
                                                                         20 );
        final News news = News.create( ValuesGenerator.getRandomText( 10 ,
                                                                      20 ) ,
                                       ValuesGenerator.getRandomText( 20 ,
                                                                      30 ) ,
                                       new URI( uriStr ) ,
                                       new URI( uriStr ) ,
                                       ValuesGenerator.getRandomText( 1 ,
                                                                      2 ) ,
                                       ValuesGenerator.getRandomText( 100 ,
                                                                      200 ) ,
                                       ValuesGenerator.getRandomWordSet( 3 ,
                                                                         8 ) ,
                                       new Date() );

        sendMessage( news );
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_NewsGenerator.class );
}
