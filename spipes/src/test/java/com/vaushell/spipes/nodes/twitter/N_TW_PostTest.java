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

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.tools.scribe.twitter.TwitterClient;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeSet;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see N_TW_Post
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TW_PostTest
{
    // PUBLIC
    public N_TW_PostTest()
    {
        // Nothing
    }

    /**
     * Check tweet content length.
     *
     */
    @Test
    public void testConvertNews()
    {
        final String uriStr = "http://url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";

        final Message message = Message.create(
            Message.KeyIndex.TITLE ,
            "Le titre de cette news n'est pas trop long" ,
            Message.KeyIndex.DESCRIPTION ,
            "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
            Message.KeyIndex.URI ,
            URI.create( uriStr ) ,
            Message.KeyIndex.URI_SOURCE ,
            URI.create( uriStr ) ,
            Message.KeyIndex.AUTHOR ,
            "John Kiki" ,
            Message.KeyIndex.CONTENT ,
            "Le contenu, je m'en fous" ,
            Message.KeyIndex.PUBLISHED_DATE ,
            new Date().getTime() ,
            Message.KeyIndex.TAGS ,
            new TreeSet<>( Arrays.asList( "ceci" ,
                                          "est" ,
                                          "un" ,
                                          "tag" ,
                                          "mais" ,
                                          "je" ,
                                          "vais" ,
                                          "en" ,
                                          "rajouter" ,
                                          "pour" ,
                                          "que" ,
                                          "ca" ,
                                          "soit" ,
                                          "long" ) )
        );

        final String content = N_TW_Post.createContent( message ,
                                                        TwitterClient.TWEET_SIZE );

        assertEquals( "(" + content.length() + ") " + content ,
                      content.length() ,
                      TwitterClient.TWEET_SIZE );
    }

    /**
     * Check tweet content length (2).
     *
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testURLlong()
    {
        final String uriStr = "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";

        final Message message = Message.create(
            Message.KeyIndex.TITLE ,
            "Le titre de cette news n'est pas trop long" ,
            Message.KeyIndex.DESCRIPTION ,
            "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
            Message.KeyIndex.URI ,
            URI.create( uriStr ) ,
            Message.KeyIndex.URI_SOURCE ,
            URI.create( uriStr ) ,
            Message.KeyIndex.AUTHOR ,
            "John Kiki" ,
            Message.KeyIndex.CONTENT ,
            "Le contenu, je m'en fous" ,
            Message.KeyIndex.TAGS ,
            new TreeSet<String>() ,
            Message.KeyIndex.PUBLISHED_DATE ,
            new Date().getTime()
        );

        N_TW_Post.createContent( message ,
                                 TwitterClient.TWEET_SIZE );
    }

    /**
     * Check tweet URL null.
     *
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testURLnull()
    {
        final Message message = Message.create(
            Message.KeyIndex.TITLE ,
            "Le titre de cette news n'est pas trop long" ,
            Message.KeyIndex.DESCRIPTION ,
            "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
            Message.KeyIndex.URI_SOURCE ,
            URI.create(
            "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234" ) ,
            Message.KeyIndex.AUTHOR ,
            "John Kiki" ,
            Message.KeyIndex.CONTENT ,
            "Le contenu, je m'en fous" ,
            Message.KeyIndex.TAGS ,
            new TreeSet<String>() ,
            Message.KeyIndex.PUBLISHED_DATE ,
            new Date().getTime()
        );

        N_TW_Post.createContent( message ,
                                 TwitterClient.TWEET_SIZE );
    }

    /**
     * Check tweet title null.
     *
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testURLtitleNull()
    {
        final Message message = Message.create(
            Message.KeyIndex.DESCRIPTION ,
            "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" ,
            Message.KeyIndex.URI_SOURCE ,
            URI.create(
            "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234" ) ,
            Message.KeyIndex.AUTHOR ,
            "John Kiki" ,
            Message.KeyIndex.CONTENT ,
            "Le contenu, je m'en fous" ,
            Message.KeyIndex.TAGS ,
            new TreeSet<String>() ,
            Message.KeyIndex.PUBLISHED_DATE ,
            new Date().getTime()
        );

        N_TW_Post.createContent( message ,
                                 TwitterClient.TWEET_SIZE );
    }
}
