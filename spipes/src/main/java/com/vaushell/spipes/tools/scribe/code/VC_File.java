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

package com.vaushell.spipes.tools.scribe.code;

import com.vaushell.spipes.tools.FilesHelper;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Ask to user to enter the code with keyboard.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class VC_File
    implements I_ValidationCode
{
    // PUBLIC
    public VC_File( final String prefix ,
                    final Path path )
    {
        this.prefix = prefix;
        this.path = path;
    }

    @Override
    public String getValidationCode( final String authURL )
    {
        try
        {
            System.out.println( prefix + " Use this URL :" );
            System.out.println( authURL );

            System.out.println( "Write token inside :'" + path.toString() + "'" );
            final String code = FilesHelper.fileContent( path );

            System.out.println( prefix + " Read code is '" + code + "'" );

            return code;
        }
        catch( final IOException |
                     InterruptedException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    // PRIVATE
    private final String prefix;
    private final Path path;
}
