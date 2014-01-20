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

package com.vaushell.superpipes.dispatch;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.vaushell.superpipes.tools.scribe.code.VC_FileFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.apache.commons.configuration.XMLConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 * Unit test.
 *
 * @see ErrorMailer
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class ErrorMailerTest
{
    // PUBLIC
    public ErrorMailerTest()
    {
        this.dispatcher = new Dispatcher();
    }

    /**
     * Initialize the test.
     *
     * @throws Exception
     */
    @BeforeClass
    public void setUp()
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
        dispatcher.init( config ,
                         pDatas ,
                         new VC_FileFactory( pDatas ) );
    }

    /**
     * Test postError.
     *
     * @throws java.lang.InterruptedException
     */
    @Test
    @SuppressWarnings( "unchecked" )
    public void testPostError()
        throws InterruptedException
    {
        final SimpleSmtpServer server = SimpleSmtpServer.start( 33325 );

        dispatcher.eMailer.start();

        dispatcher.postError( new UnsupportedOperationException( "test null 1" ) ,
                              null );
        dispatcher.postError( new UnsupportedOperationException( "test null 2" ) ,
                              null );

        Thread.sleep( 500 );

        dispatcher.postError( new UnsupportedOperationException( "test null 3" ) ,
                              null );
        dispatcher.postError( new UnsupportedOperationException( "test null 4" ) ,
                              null );

        Thread.sleep( 800 );

        dispatcher.eMailer.stopMe();
        dispatcher.eMailer.join();

        server.stop();

        assertEquals( "SMTP server should receive 2 mails" ,
                      2 ,
                      server.getReceivedEmailSize() );

        final Iterator<SmtpMessage> it = server.getReceivedEmail();

        final SmtpMessage msg1 = it.next();
        assertTrue( "Mail n°1 must contain test 1" ,
                    msg1.getBody().contains( "test null 1" ) );

        final SmtpMessage msg2 = it.next();
        assertTrue( "Mail n°2 must contain test 2, 3 & 4" ,
                    msg2.getBody().contains( "test null 2" )
                    && msg2.getBody().contains( "test null 3" )
                    && msg2.getBody().contains( "test null 4" ) );
    }

    // PRIVATE
    private final Dispatcher dispatcher;
}
