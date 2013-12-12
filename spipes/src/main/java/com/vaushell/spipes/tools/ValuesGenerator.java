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

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Values generator.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class ValuesGenerator
{
    // PUBLIC
    /**
     * Generate random text.
     *
     * @param minWords Minimum words count
     * @param maxWords Maximum words count
     * @return the Text
     */
    public static String getRandomText( final int minWords ,
                                        final int maxWords )
    {
        final StringBuilder sb = new StringBuilder();

        final int count = RND.nextInt( maxWords - minWords ) + minWords;
        for ( int i = 0 ; i < count ; ++i )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ' ' );
            }

            sb.append( getRandomWord( 3 ,
                                      10 ) );
        }

        return sb.toString();
    }

    /**
     * Generate random set.
     *
     * @param minWords Minimum words count
     * @param maxWords Maximum words count
     * @return the Set
     */
    public static Set<String> getRandomWordSet( final int minWords ,
                                                final int maxWords )
    {
        final HashSet<String> s = new HashSet<>();

        final int count = RND.nextInt( maxWords - minWords ) + minWords;
        for ( int i = 0 ; i < count ; ++i )
        {
            s.add( getRandomWord( 3 ,
                                  7 ) );
        }

        return s;
    }

    /**
     * Generate a random word.
     *
     * @param minLetters Minimum letters count
     * @param maxLetters Maximum letters count
     * @return The word
     */
    public static String getRandomWord( final int minLetters ,
                                        final int maxLetters )
    {
        final char[] str = new char[ RND.nextInt( maxLetters - minLetters ) + minLetters ];

        for ( int i = 0 ; i < str.length ; ++i )
        {
            int charCode = RND.nextInt( 26 );
            if ( RND.nextBoolean() )
            {
                charCode += 65;
            }
            else
            {
                charCode += 97;
            }

            str[ i] = (char) charCode;
        }

        return new String( str );
    }
    // PRIVATE
    private static final Random RND = new Random();

    private ValuesGenerator()
    {
        // Nothing
    }
}
