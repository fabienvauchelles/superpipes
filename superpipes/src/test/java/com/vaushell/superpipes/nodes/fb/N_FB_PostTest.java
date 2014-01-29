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

package com.vaushell.superpipes.nodes.fb;

import com.vaushell.superpipes.dispatch.ConfigProperties;
import com.vaushell.superpipes.dispatch.Dispatcher;
import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.dispatch.Tags;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.nodes.test.N_ReceiveBlocking;
import com.vaushell.superpipes.tools.scribe.code.VC_FileFactory;
import com.vaushell.superpipes.transforms.bitly.T_Shorten;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.apache.commons.configuration.XMLConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit test.
 *
 * @see N_FB_Post
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_FB_PostTest
{
    // PUBLIC
    public N_FB_PostTest()
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
            conf = "conf-local/test/configuration.xml";
        }

        String datas = System.getProperty( "datas" );
        if ( datas == null )
        {
            datas = "conf-local/test/datas";
        }

        final XMLConfiguration config = new XMLConfiguration( conf );

        final Path pDatas = Paths.get( datas );

        dispatcher = new Dispatcher();
        dispatcher.init( config ,
                         pDatas ,
                         new VC_FileFactory( pDatas ) );
    }

    /**
     * Send a message, use bitly.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testSendMessageBitly()
        throws Exception
    {
        // Construct path
        final A_Node nFB = dispatcher.addNode( "post-facebook" ,
                                               N_FB_Post.class ,
                                               Arrays.asList( dispatcher.getCommon( "facebook" ) ) );
        nFB.addTransformIN( T_Shorten.class ,
                            Arrays.asList( dispatcher.getCommon( "bitly" ) ) );

        final N_ReceiveBlocking nReceive = (N_ReceiveBlocking) dispatcher.addNode( "receive-facebook" ,
                                                                                   N_ReceiveBlocking.class ,
                                                                                   ConfigProperties.EMPTY_COMMONS );

        dispatcher.addNode( "delete-facebook" ,
                            N_FB_Delete.class ,
                            Arrays.asList( dispatcher.getCommon( "facebook" ) ) );

        dispatcher.addRoute( "post-facebook" ,
                             "delete-facebook" );
        dispatcher.addRoute( "delete-facebook" ,
                             "receive-facebook" );

        // Start
        dispatcher.start();

        // Create the message
        final Message message = Message.create(
            Message.KeyIndex.TITLE ,
            "Animations sympas en CSS" ,
            Message.KeyIndex.PUBLISHED_DATE ,
            new DateTime( 2014 ,
                          1 ,
                          28 ,
                          12 ,
                          41 ,
                          24 ,
                          00 ) ,
            "id-permanent" ,
            "F0_-sA" ,
            "id-shaarli" ,
            "20140128_124124" ,
            Message.KeyIndex.URI ,
            URI.create( "https://github.com/daneden/animate.css" ) ,
            "uri-permanent" ,
            URI.create( "http://lesliensducode.com/?F0_-sA" ) ,
            Message.KeyIndex.TAGS ,
            new Tags( "animation" ,
                      "css" ,
                      "jquery" )
        );

        // Send it
        nFB.receiveMessage( message );

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
