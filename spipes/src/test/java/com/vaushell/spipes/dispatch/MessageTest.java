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

package com.vaushell.spipes.dispatch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.joda.time.DateTime;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see Message
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class MessageTest
{
    // PUBLIC
    public MessageTest()
    {
        // Nothing
    }

    /**
     * Test of message serialization.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void testSerialization()
        throws IOException , ClassNotFoundException
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
            new DateTime() ,
            Message.KeyIndex.TAGS ,
            new Tags( "ceci" ,
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
                      "long" )
        );

        final Path path = Files.createTempFile( "message" ,
                                                ".dat" );

        try( final ObjectOutputStream os = new ObjectOutputStream( Files.newOutputStream( path ) ) )
        {
            os.writeObject( message );
        }

        try( final ObjectInputStream is = new ObjectInputStream( Files.newInputStream( path ) ) )
        {
            final Message message2 = (Message) is.readObject();

            compareMessage( message ,
                            message2 );
        }

        Files.delete( path );
    }

    // PRIVATE
    private void compareMessage( final Message expected ,
                                 final Message actual )
    {
        assertNotNull( "Not null" ,
                       expected );
        assertNotNull( "Not null" ,
                       actual );

        // ID
        assertEquals( "Messages must have the same ID" ,
                      expected.getID() ,
                      actual.getID() );

        // Keys
        assertArrayEquals( "Message must have the same properties key" ,
                           expected.getKeys().toArray( new String[]
        {
                           } ) ,
                           actual.getKeys().toArray( new String[]
        {
                           } ) );

        for ( final String key : expected.getKeys() )
        {
            final Serializable expectedValue = expected.getProperty( key );
            final Serializable actualValue = actual.getProperty( key );

            assertEquals( "Properties '" + key + "' must have the same value" ,
                          expectedValue ,
                          actualValue );
        }
    }
}
