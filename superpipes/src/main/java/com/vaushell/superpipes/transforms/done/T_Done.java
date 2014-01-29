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

package com.vaushell.superpipes.transforms.done;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.transforms.A_Transform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter already known message by ID.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_Done
    extends A_Transform
{
    // PUBLIC
    public T_Done()
    {
        super();

        this.ids = new HashSet<>();
        this.fields = new ArrayList<>();
    }

    @Override
    public void prepare()
        throws IOException
    {
        // Config
        path = getNode().getDispatcher().getDatas().resolve( Paths.get( getNode().getNodeID() ,
                                                                        "done.dat" ) );

        Files.createDirectories( path.getParent() );

        // Load previous ID
        if ( Files.exists( path ) )
        {
            try( final BufferedReader bfr = Files.newBufferedReader( path ,
                                                                     Charset.forName( "utf-8" ) ) )
            {
                String line = bfr.readLine();
                while ( line != null )
                {
                    final int ind = line.indexOf( ' ' );
                    if ( ind < 0 )
                    {
                        ids.add( line );
                    }
                    else
                    {
                        ids.add( line.substring( 0 ,
                                                 ind ) );
                    }

                    line = bfr.readLine();
                }
            }
        }

        // Load fields list
        final String fieldsStr = getProperties().getConfigString( "fields" ,
                                                                  null );
        if ( fieldsStr != null )
        {
            for ( final String field : fieldsStr.split( "," ) )
            {
                final String cleanField = field.trim();
                if ( !cleanField.isEmpty() )
                {
                    fields.add( cleanField );
                }
            }
        }
    }

    @Override
    public Message transform( final Message message )
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNode().getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + Message.
                formatSimple( message ) );
        }

        final String ID = buildID( message ,
                                   fields );
        if ( ids.contains( ID ) )
        {
            return null;
        }

        // Save message ID. Won't be replay
        ids.add( ID );

        try( final BufferedWriter bfw = Files.newBufferedWriter( path ,
                                                                 Charset.forName( "utf-8" ) ,
                                                                 StandardOpenOption.APPEND ,
                                                                 StandardOpenOption.CREATE ) )
        {
            bfw.write( ID );
            bfw.write( ' ' );
            bfw.write( Message.formatSimple( message ) );
            bfw.newLine();
        }

        return message;
    }

    @Override
    public void terminate()
    {
        // Nothing
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( T_Done.class );
    private final HashSet<String> ids;
    private Path path;
    private final List<String> fields;

    private String buildID( final Message message ,
                            final Collection<String> keys )
    {
        if ( message.getPropertyCount() == 0 )
        {
            return null;
        }

        final StringBuilder sb = new StringBuilder();

        for ( final String key : keys )
        {
            final Serializable value = message.getProperty( key );
            if ( value != null )
            {
                sb.append( '$' )
                    .append( key )
                    .append( '#' )
                    .append( value );
            }
        }

        if ( sb.length() <= 0 )
        {
            return buildID( message ,
                            message.getKeys() );
        }
        else
        {
            return DigestUtils.md5Hex( sb.toString() );
        }
    }
}
