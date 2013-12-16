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

import com.vaushell.spipes.Message;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
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
     * @throws URISyntaxException
     */
    @Test
    public void testConvertNews()
        throws URISyntaxException
    {
        final Message message = new Message();
        message.setProperty( "title" ,
                             "Le titre de cette news n'est pas trop long" );
        message.setProperty( "description" ,
                             "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" );

        final String uriStr = "http://url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";
        message.setProperty( "uri" ,
                             new URI( uriStr ) );
        message.setProperty( "uri-source" ,
                             new URI( uriStr ) );
        message.setProperty( "author" ,
                             "John Kiki" );
        message.setProperty( "content" ,
                             "Le contenu, je m'en fous" );

        final Set<String> tags = new TreeSet<>();
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
        message.setProperty( "tags" ,
                             tags );

        message.setProperty( "published-date" ,
                             new Date().getTime() );

        final String content = N_TW_Post.createContent( message );

        assertEquals( "(" + content.length() + ") " + content ,
                      content.length() ,
                      N_TW_Post.TWEET_SIZE );
    }

    /**
     * Check tweet content length (2).
     *
     * @throws URISyntaxException
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testURLlong()
        throws URISyntaxException
    {
        final Message message = new Message();
        message.setProperty( "title" ,
                             "Le titre de cette news n'est pas trop long" );
        message.setProperty( "description" ,
                             "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" );

        final String uriStr = "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234";
        message.setProperty( "uri" ,
                             new URI( uriStr ) );
        message.setProperty( "uri-source" ,
                             new URI( uriStr ) );
        message.setProperty( "author" ,
                             "John Kiki" );
        message.setProperty( "content" ,
                             "Le contenu, je m'en fous" );

        message.setProperty( "tags" ,
                             new TreeSet<String>() );

        message.setProperty( "published-date" ,
                             new Date().getTime() );

        N_TW_Post.createContent( message );
    }

    /**
     * Check tweet URL null.
     *
     * @throws URISyntaxException
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testURLnull()
        throws URISyntaxException
    {
        final Message message = new Message();
        message.setProperty( "title" ,
                             "Le titre de cette news n'est pas trop long" );
        message.setProperty( "description" ,
                             "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" );

        message.setProperty( "uri-source" ,
                             new URI(
            "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234" ) );
        message.setProperty( "author" ,
                             "John Kiki" );
        message.setProperty( "content" ,
                             "Le contenu, je m'en fous" );

        message.setProperty( "tags" ,
                             new TreeSet<String>() );

        message.setProperty( "published-date" ,
                             new Date().getTime() );

        N_TW_Post.createContent( message );
    }

    /**
     * Check tweet title null.
     *
     * @throws URISyntaxException
     */
    @Test( expectedExceptions =
    {
        IllegalArgumentException.class
    } )
    public void testURLtitleNull()
        throws URISyntaxException
    {
        final Message message = new Message();
        message.setProperty( "description" ,
                             "La description de la news est vraiment longue c'est pourquoi je vais bientôt la couper mais je vais en rajouter un peu histoire que la ligne soit suffisament longue pour le test et j'adore écrire les descriptions" );

        message.setProperty( "uri-source" ,
                             new URI(
            "http://ceci-est-une-enorme-url.com/encore-jen-rajoute/qui-est-bien-trop-longue/url.de.ouf/qui-est-enorme/sur-ce-site/et-je-suis-sure-que-ca-va-peter/mais-il-faut-toujours-en-rajouter/car-cela-ne-suffit-pas/p=1234" ) );
        message.setProperty( "author" ,
                             "John Kiki" );
        message.setProperty( "content" ,
                             "Le contenu, je m'en fous" );

        message.setProperty( "tags" ,
                             new TreeSet<String>() );

        message.setProperty( "published-date" ,
                             new Date().getTime() );

        N_TW_Post.createContent( message );
    }
}
