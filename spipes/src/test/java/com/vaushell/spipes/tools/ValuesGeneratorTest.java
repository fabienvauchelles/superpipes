package com.vaushell.spipes.tools;

import java.util.Set;
import javax.naming.ConfigurationException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test
 *
 * @see ValuesGenerator
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class ValuesGeneratorTest
{
    // PUBLIC
    @BeforeClass
    public void setUpClass()
            throws ConfigurationException
    {
    }

    @AfterClass
    public void tearDownClass()
            throws Exception
    {
    }

    @Test
    public void testWord()
    {
        for ( int i = 0 ; i < 1000 ; ++i )
        {
            String word = ValuesGenerator.getRandomWord( 10 ,
                                                         60 );

            assertTrue( word.length() >= 10 );
            assertTrue( word.length() < 60 );

            assertTrue( isWord( word ) );
        }
    }

    @Test
    public void testText()
    {
        for ( int i = 0 ; i < 1000 ; ++i )
        {
            String text = ValuesGenerator.getRandomText( 10 ,
                                                         60 );
            String[] words = text.split( " " );

            assertTrue( words.length >= 10 );
            assertTrue( words.length < 60 );
        }
    }

    @Test
    public void testSet()
    {
        for ( int i = 0 ; i < 1000 ; ++i )
        {
            Set<String> s = ValuesGenerator.getRandomWordSet( 10 ,
                                                              60 );

            assertTrue( s.size() >= 10 );
            assertTrue( s.size() < 60 );
        }
    }

    // PRIVATE
    private boolean isWord( String word )
    {
        for ( int i = 0 ; i < word.length() ; ++i )
        {
            char c = word.charAt( i );

            if ( !Character.isLetter( c ) )
            {
                return false;
            }
        }

        return true;
    }
}
