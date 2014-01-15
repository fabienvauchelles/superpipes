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

package com.vaushell.spipes.tools;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Throwable helper.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class ThrowableHelper
{
    // PUBLIC
    /**
     * Return full message error in plain text.
     *
     * @param th Throwable
     * @return the message
     */
    public static String formatPlainText( final Throwable th )
    {
        if ( th == null )
        {
            return null;
        }

        final StringBuilder sb = new StringBuilder();

        sb.append( String.format( "ERROR at %s%n" ,
                                  DATE_FORMAT.print( new DateTime() ) ) );

        // Message
        final List<Throwable> reverse = new ArrayList<>();

        Throwable actual = th;
        while ( actual != null )
        {
            reverse.add( actual );

            actual = actual.getCause();
        }

        for ( int errorNum = reverse.size() - 1 ; errorNum >= 0 ; --errorNum )
        {
            actual = reverse.get( errorNum );

            sb.append( String.format( "    Cause #%d: %s (%s)%n" ,
                                      errorNum ,
                                      actual.getMessage() ,
                                      actual.getClass().getName() ) );

            final StackTraceElement[] elts = actual.getStackTrace();
            if ( elts.length > 0 )
            {
                for ( final StackTraceElement elt : elts )
                {
                    sb.append( String.format( "          at %s.%s(%s:%d)%n" ,
                                              elt.getClassName() ,
                                              elt.getMethodName() ,
                                              elt.getFileName() ,
                                              elt.getLineNumber() ) );
                }
            }
        }

        return sb.toString();
    }

    /**
     * Return full message error in HTML.
     *
     * @param th Throwable
     * @return the message
     */
    public static String formatHTML( final Throwable th )
    {
        if ( th == null )
        {
            return null;
        }

        final StringBuilder sb = new StringBuilder();

        sb.append( String.format( "<h1>ERROR at %s</h1>%n" ,
                                  DATE_FORMAT.print( new DateTime() ) ) );

        // Message
        final List<Throwable> reverse = new ArrayList<>();

        Throwable actual = th;
        while ( actual != null )
        {
            reverse.add( actual );

            actual = actual.getCause();
        }

        for ( int errorNum = reverse.size() - 1 ; errorNum >= 0 ; --errorNum )
        {
            actual = reverse.get( errorNum );

            sb.append( String.format( "<h2>Cause #%d: %s (%s)</h2>%n" ,
                                      errorNum ,
                                      actual.getMessage() ,
                                      actual.getClass().getName() ) );

            final StackTraceElement[] elts = actual.getStackTrace();
            if ( elts.length > 0 )
            {
                sb.append( String.format( "<ul>%n" ) );

                for ( final StackTraceElement elt : elts )
                {
                    sb.append( String.format( "<li>at %s.%s(%s:%d)</li>%n" ,
                                              elt.getClassName() ,
                                              elt.getMethodName() ,
                                              elt.getFileName() ,
                                              elt.getLineNumber() ) );
                }

                sb.append( String.format( "</ul>%n" ) );
            }
        }

        return sb.toString();
    }

    // PRIVATE
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern( "dd/MM/yyyy HH:mm:ss" );

    private ThrowableHelper()
    {
        // Nothing
    }
}
