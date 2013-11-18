/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class ValuesGenerator
{
    // PUBLIC
    public static String getRandomText( int minWords ,
                                        int maxWords )
    {
        StringBuilder sb = new StringBuilder();

        int count = rnd.nextInt( maxWords - minWords ) + minWords;
        for ( int i = 0 ; i < count ; ++i )
        {
            if ( sb.length() > 0 )
            {
                sb.append( " " );
            }

            sb.append( getRandomWord( 3 ,
                                      10 ) );
        }

        return sb.toString();
    }

    public static Set<String> getRandomWordSet( int minWords ,
                                                int maxWords )
    {
        HashSet<String> s = new HashSet<>();

        int count = rnd.nextInt( maxWords - minWords ) + minWords;
        for ( int i = 0 ; i < count ; ++i )
        {
            s.add( getRandomWord( 3 ,
                                  7 ) );
        }

        return s;
    }

    public static String getRandomWord( int minLetters ,
                                        int maxLetters )
    {
        char[] str = new char[ rnd.nextInt( maxLetters - minLetters ) + minLetters ];

        for ( int i = 0 ; i < str.length ; ++i )
        {
            int charCode = rnd.nextInt( 26 );
            if ( rnd.nextBoolean() )
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
    private final static Random rnd = new Random();
}
