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

package com.vaushell.spipes.transforms.done;

import com.vaushell.spipes.Message;
import com.vaushell.spipes.transforms.A_Transform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
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
    }

    @Override
    public void prepare()
        throws IOException
    {
        // Config
        path = Paths.get( getMainConfig( "datas-directory" ) ,
                          getNodeID() ,
                          "done.dat" );

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
                    ids.add( line );

                    line = bfr.readLine();
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
            LOGGER.trace( "[" + getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + message );
        }

        if ( ids.contains( message.getID() ) )
        {
            return null;
        }

        // Save message ID. Won't be replay
        ids.add( message.getID() );

        try( final BufferedWriter bfw = Files.newBufferedWriter( path ,
                                                                 Charset.forName( "utf-8" ) ,
                                                                 StandardOpenOption.APPEND ,
                                                                 StandardOpenOption.CREATE ) )
        {
            bfw.write( message.getID() );
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
}
