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

package com.vaushell.superpipes.tools.scribe.code;

import com.vaushell.superpipes.tools.FilesHelper;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Use a file which contains the verification code.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class VC_FileFactory
    implements A_ValidatorCode.I_Factory
{
    // PUBLIC
    public VC_FileFactory( final Path path )
    {
        this.path = path;
    }

    @Override
    public A_ValidatorCode create( final String prefix )
    {
        return new VC_File( prefix );
    }

    // PRIVATE
    private final Path path;

    private class VC_File
        extends A_ValidatorCode
    {
        public VC_File( final String prefix )
        {
            super( prefix );
        }

        @Override
        public String getValidationCode( final String authURL )
        {
            try
            {
                System.out.println( getPrefix() + " Use this URL :" );
                System.out.println( authURL );

                final Path codeFile = FilesHelper.createIncrNonExistentFilename( path ,
                                                                                 "" ,
                                                                                 ".code" );
                System.out.println( "Write token inside :'" + codeFile.toString() + "'" );

                final String code = FilesHelper.fileContent( codeFile );

                System.out.println( getPrefix() + " Read code is '" + code + "'" );

                return code;
            }
            catch( final IOException |
                         InterruptedException ex )
            {
                throw new RuntimeException( ex );
            }
        }
    }
}
