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

import java.util.Set;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see ValuesGenerator
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class ValuesGeneratorTest
{
    // PUBLIC
    public ValuesGeneratorTest()
    {
        // Nothing
    }

    @Test
    public void testWord()
    {
        for ( int i = 0 ; i < 1000 ; ++i )
        {
            final String word = ValuesGenerator.getRandomWord( 10 ,
                                                               60 );

            assertTrue( "Word length must be greather than 10 characters" ,
                        word.length() >= 10 );
            assertTrue( "Word length must be less than 60 characters" ,
                        word.length() < 60 );

            assertTrue( "A word should only contain character" ,
                        isWord( word ) );
        }
    }

    @Test
    public void testText()
    {
        for ( int i = 0 ; i < 1000 ; ++i )
        {
            final String text = ValuesGenerator.getRandomText( 10 ,
                                                               60 );
            final String[] words = text.split( " " );

            assertTrue( "Text length must be greather than 10 characters" ,
                        words.length >= 10 );
            assertTrue( "Text length must be less than 60 characters" ,
                        words.length < 60 );
        }
    }

    @Test
    public void testSet()
    {
        for ( int i = 0 ; i < 1000 ; ++i )
        {
            final Set<String> s = ValuesGenerator.getRandomWordSet( 10 ,
                                                                    60 );

            assertTrue( "Set length must be greather than 10 characters" ,
                        s.size() >= 10 );
            assertTrue( "Set length must be less than 60 characters" ,
                        s.size() < 60 );
        }
    }

    // PRIVATE
    private boolean isWord( final String word )
    {
        for ( int i = 0 ; i < word.length() ; ++i )
        {
            final char c = word.charAt( i );

            if ( !Character.isLetter( c ) )
            {
                return false;
            }
        }

        return true;
    }
}
