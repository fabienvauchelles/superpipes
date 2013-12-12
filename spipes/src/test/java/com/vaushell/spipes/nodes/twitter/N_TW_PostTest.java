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

package com.vaushell.spipes.nodes.twitter;

import com.vaushell.spipes.nodes.rss.News;
import com.vaushell.spipes.tools.ValuesGenerator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import javax.naming.ConfigurationException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Unit test
 *
 * @see ValuesGenerator
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TW_PostTest
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
    public void testConvertNews()
            throws URISyntaxException
    {
        Set<String> tags = new TreeSet<>();
        tags.add( "ceci" );
        tags.add( "est" );
        tags.add( "un" );
        tags.add( "tag" );
        tags.add( "mais" );
        tags.add( "je" );
        tags.add( "vais" );
        tags.add( "en" );
        tags.add( "rajouter" );
        tags.add( "pour" );
        tags.add( "que" );
        tags.add( "ca" );
        tags.add( "soit" );
        tags.add( "long" );

        String uriStr = "http://url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";
        News news = News.create( "Le titre de cette news n'est pas trop long" ,
                                 "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
                                 new URI( uriStr ) ,
                                 new URI( uriStr ) ,
                                 "John Kiki" ,
                                 "Le contenu, je m'en fous" ,
                                 tags ,
                                 new Date() );

        Tweet tweet = N_TW_Post.convertFromNews( news );

        assertEquals( "(" + tweet.getMessage().length() + ") " + tweet.getMessage() ,
                      tweet.getMessage().length() ,
                      N_TW_Post.TWEET_SIZE );
    }

    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testURLlong()
            throws URISyntaxException
    {
        String uriStr = "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";
        News news = News.create( "Le titre de cette news n'est pas trop long" ,
                                 "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
                                 new URI( uriStr ) ,
                                 new URI( uriStr ) ,
                                 "John Kiki" ,
                                 "Le contenu, je m'en fous" ,
                                 new TreeSet<String>() ,
                                 new Date() );

        Tweet tweet = N_TW_Post.convertFromNews( news );
    }

    @Test( expectedExceptions =
    {
        NullPointerException.class
    } )
    public void testURLnull()
            throws URISyntaxException
    {
        String uriStr = "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";
        News news = News.create( "Le titre de cette news n'est pas trop long" ,
                                 "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
                                 null ,
                                 new URI( uriStr ) ,
                                 "John Kiki" ,
                                 "Le contenu, je m'en fous" ,
                                 new TreeSet<String>() ,
                                 new Date() );

        Tweet tweet = N_TW_Post.convertFromNews( news );
    }

    @Test( expectedExceptions =
    {
        NullPointerException.class
    } )
    public void testURLtitleNull()
            throws URISyntaxException
    {
        String uriStr = "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";
        News news = News.create( null ,
                                 "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
                                 null ,
                                 new URI( uriStr ) ,
                                 "John Kiki" ,
                                 "Le contenu, je m'en fous" ,
                                 new TreeSet<String>() ,
                                 new Date() );

        Tweet tweet = N_TW_Post.convertFromNews( news );
    }
}
