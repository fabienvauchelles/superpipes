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

import com.vaushell.spipes.dispatch.Dispatcher;
import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.dispatch.Tags;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.test.N_ReceiveBlocking;
import com.vaushell.spipes.tools.scribe.code.VC_FileFactory;
import com.vaushell.spipes.transforms.bitly.T_Shorten;
import com.vaushell.spipes.transforms.image.T_FindBiggest;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.configuration.XMLConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeMethod;
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
        this.dispatcher = null;
    }

    /**
     * Initialize the dispatcher before every test.
     *
     * @throws Exception
     */
    @BeforeMethod
    public void loadDispatcher()
        throws Exception
    {
        // My config
        String conf = System.getProperty( "conf" );
        if ( conf == null )
        {
            conf = "conf-local/twitter/configuration.xml";
        }

        String datas = System.getProperty( "datas" );
        if ( datas == null )
        {
            datas = "conf-local/twitter/datas";
        }

        final XMLConfiguration config = new XMLConfiguration( conf );

        final Path pDatas = Paths.get( datas );

        dispatcher = new Dispatcher();
        dispatcher.init( config ,
                         pDatas ,
                         new VC_FileFactory( pDatas ) );
    }

    /**
     * Send a tweet, use bitly, no image.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendTweetBitlyNoImage()
        throws Exception
    {
        // Construct path
        final A_Node nTwitter = dispatcher.addNode( "twitter" ,
                                                    N_TW_Post.class ,
                                                    "twitter" );
        nTwitter.addTransformIN( T_FindBiggest.class );
        nTwitter.addTransformIN( T_Shorten.class ,
                                 "bitly" );

        final N_ReceiveBlocking nReceive = (N_ReceiveBlocking) dispatcher.addNode( "receive" ,
                                                                                   N_ReceiveBlocking.class );

        dispatcher.addNode( "delete" ,
                            N_TW_Delete.class ,
                            "twitter" );

        dispatcher.addRoute( "twitter" ,
                             "delete" );
        dispatcher.addRoute( "delete" ,
                             "receive" );

        // Start
        dispatcher.start();

        // Create the message
        final Message message = Message.create(
            Message.KeyIndex.TITLE ,
            "The algorithm for a perfectly balanced photo gallery – Summit Stories from Crispy Mountain" ,
            Message.KeyIndex.PUBLISHED_DATE ,
            new DateTime( 2013 ,
                          8 ,
                          13 ,
                          11 ,
                          32 ,
                          10 ,
                          00 ) ,
            Message.KeyIndex.DESCRIPTION ,
            "un algo pour afficher une galerie d'image en maximisant l'utilisation et en répartissant les photos même si elles sont d'un ratio hauteur/largeur très différent. Exemple de résultat: http://www.chromatic.io/FQrLQsb'" ,
            "id-permanent" ,
            "geKSvg" ,
            "id-shaarli" ,
            "20130813_113210" ,
            Message.KeyIndex.URI ,
            URI.create( "http://www.crispymtn.com/stories/the-algorithm-for-a-perfectly-balanced-photo-gallery" ) ,
            "uri-permanent" ,
            URI.create( "http://lesliensducode.com/?geKSvg" ) ,
            Message.KeyIndex.TAGS ,
            new Tags( "image" ,
                      "optimize" ,
                      "picture" ,
                      "position" ,
                      "ratio" )
        );

        // Send it
        nTwitter.receiveMessage( message );

        // Wait to be receive
        final Message messageRcv = nReceive.getProcessingMessageOrWait( new Duration( 10L * 1000L ) );
        assertNotNull( "Message shouldn't be null" ,
                       messageRcv );

        // Stop
        dispatcher.stopAndWait();
    }

    /**
     * Send a tweet, use bitly, with an image.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendTweetBitlyWithImage()
        throws Exception
    {
        // Construct path
        final A_Node nTwitter = dispatcher.addNode( "twitter" ,
                                                    N_TW_Post.class ,
                                                    "twitter" );
        nTwitter.addTransformIN( T_FindBiggest.class );
        nTwitter.addTransformIN( T_Shorten.class ,
                                 "bitly" );

        final N_ReceiveBlocking nReceive = (N_ReceiveBlocking) dispatcher.addNode( "receive" ,
                                                                                   N_ReceiveBlocking.class );

        dispatcher.addNode( "delete" ,
                            N_TW_Delete.class ,
                            "twitter" );

        dispatcher.addRoute( "twitter" ,
                             "delete" );
        dispatcher.addRoute( "delete" ,
                             "receive" );

        // Start
        dispatcher.start();

        // Create the message
        final Message message = Message.create(
            Message.KeyIndex.TITLE ,
            "The Secret Weapon: Evernote and GTD smoothly integrated into TSW" ,
            Message.KeyIndex.PUBLISHED_DATE ,
            new DateTime( 2013 ,
                          6 ,
                          20 ,
                          21 ,
                          59 ,
                          45 ,
                          00 ) ,
            Message.KeyIndex.DESCRIPTION ,
            "Amélioration de la méthodologie GTD avec Evernote" ,
            "id-permanent" ,
            "Vp_SOA" ,
            "id-shaarli" ,
            "20130620_215945" ,
            Message.KeyIndex.URI ,
            URI.create( "http://www.thesecretweapon.org/" ) ,
            "uri-permanent" ,
            URI.create( "http://lesliensducode.com/?Vp_SOA" ) ,
            Message.KeyIndex.TAGS ,
            new Tags( "evernote" ,
                      "gtd" ,
                      "methodologie" ,
                      "organize" ,
                      "task" ,
                      "todo" )
        );

        // Send it
        nTwitter.receiveMessage( message );

        // Wait to be receive
        final Message messageRcv = nReceive.getProcessingMessageOrWait( new Duration( 10L * 1000L ) );
        assertNotNull( "Message shouldn't be null" ,
                       messageRcv );

        // Stop
        dispatcher.stopAndWait();
    }

    // PRIVATE
    private Dispatcher dispatcher;
}
