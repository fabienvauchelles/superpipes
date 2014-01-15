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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Error mailer send error by email.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class ErrorMailer
    extends Thread
{
    // PUBLIC
    public ErrorMailer()
    {
        super();

        this.activated = true;
        this.internalStack = new ArrayList<>();
        this.properties = new Properties();
        this.antiBurst = new Duration( 1000L );
    }

    /**
     * Load configuration for the error mailer.
     *
     * @param cNode Configuration
     */
    public void load( final HierarchicalConfiguration cNode )
    {
        if ( cNode == null )
        {
            throw new IllegalArgumentException();
        }

        Dispatcher.readProperties( properties ,
                                   cNode );

        if ( properties.containsKey( "anti-burst" ) )
        {
            antiBurst = new Duration( Long.parseLong( properties.getProperty( "anti-burst" ) ) );

            if ( antiBurst.getMillis() <= 0L )
            {
                throw new IllegalArgumentException( "anti-burst can't be <=0. Should be null or empty." );
            }
        }
    }

    @Override
    public void run()
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] start thread" );
        }

        try
        {
            while ( isActive() )
            {
                try
                {
                    final StringBuilder sb = new StringBuilder( "<html><body>" );

                    boolean first = true;
                    for ( final String content : getLastErrorsOrWait() )
                    {
                        if ( first )
                        {
                            first = false;
                        }
                        else
                        {
                            sb.append( String.format( "<hr/>%n" ) );
                        }

                        sb.append( content );
                    }

                    sb.append( "</body></html>" );

                    sendHTML( sb.toString() );
                }
                catch( final InterruptedException ex )
                {
                    // Ignore
                }
            }
        }
        catch( final Throwable th )
        {
            LOGGER.error( "Error" ,
                          th );
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] stop thread" );
        }
    }

    /**
     * Receive a error and stack it.
     *
     * @param content content
     */
    public void receiveError( final String content )
    {
        if ( content == null )
        {
            throw new IllegalArgumentException();
        }

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] receiveError : content=" + content );
        }

        synchronized( internalStack )
        {
            internalStack.add( content );

            internalStack.notifyAll();
        }
    }

    /**
     * Stop the error mailer.
     */
    public void stopMe()
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] stopMe" );
        }

        synchronized( this )
        {
            activated = false;
        }

        interrupt();
    }

    // DEFAULT
    final Properties properties;

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( ErrorMailer.class );
    private Duration antiBurst;
    private final List<String> internalStack;
    private volatile boolean activated;
    private DateTime lastPop;

    private boolean isActive()
    {
        synchronized( this )
        {
            return activated;
        }
    }

    private List<String> getLastErrorsOrWait()
        throws InterruptedException
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getClass().getSimpleName() + "] getLastErrorsOrWait" );
        }

        synchronized( internalStack )
        {
            Duration remaining;
            if ( lastPop == null )
            {
                remaining = new Duration( 0L );
            }
            else
            {
                // Null for now
                final Duration elapsed = new Duration( lastPop ,
                                                       null );
                remaining = antiBurst.minus( elapsed );
            }

            while ( internalStack.isEmpty() || remaining.getMillis() > 0L )
            {
                if ( internalStack.isEmpty() || remaining.getMillis() <= 0L )
                {
                    internalStack.wait();
                }
                else
                {
                    internalStack.wait( remaining.getMillis() );
                }

                if ( lastPop == null )
                {
                    remaining = new Duration( 0L );
                }
                else
                {
                    final Duration elapsed = new Duration( lastPop ,
                                                           null );
                    remaining = antiBurst.minus( elapsed );
                }
            }

            final List<String> ret = new ArrayList<>();
            ret.addAll( internalStack );

            internalStack.clear();

            lastPop = new DateTime();

            return ret;
        }
    }

    private void sendHTML( final String message )
        throws MessagingException , IOException
    {
        if ( message == null || message.isEmpty() )
        {
            throw new IllegalArgumentException( "message" );
        }

        final String host = properties.getProperty( "host" );
        if ( host == null || host.isEmpty() )
        {
            throw new IllegalArgumentException( "host" );
        }

        final String from = properties.getProperty( "from" );
        if ( from == null || from.isEmpty() )
        {
            throw new IllegalArgumentException( "from" );
        }

        final String tos = properties.getProperty( "to" );
        if ( tos == null || tos.isEmpty() )
        {
            throw new IllegalArgumentException( "to" );
        }

        final Properties props = System.getProperties();
        props.setProperty( "mail.smtp.host" ,
                           host );

        final String port = properties.getProperty( "port" );
        if ( port != null && !port.isEmpty() )
        {
            props.setProperty( "mail.smtp.port" ,
                               port );
        }

        if ( "true".equalsIgnoreCase( properties.getProperty( "ssl" ) ) )
        {
            props.setProperty( "mail.smtp.ssl.enable" ,
                               "true" );
        }

        final Session session = Session.getInstance( props ,
                                                     null );
//        session.setDebug( true );

        final javax.mail.Message msg = new MimeMessage( session );

        msg.setFrom( new InternetAddress( from ) );

        msg.setRecipients( javax.mail.Message.RecipientType.TO ,
                           InternetAddress.parse( tos ,
                                                  false ) );

        msg.setSubject( "SPipes error message" );

        msg.setDataHandler( new DataHandler(
            new ByteArrayDataSource( message ,
                                     "text/html" ) ) );

        msg.setHeader( "X-Mailer" ,
                       "spipes" );

        Transport t = null;
        try
        {
            t = session.getTransport( "smtp" );

            final String username = properties.getProperty( "username" );
            final String password = properties.getProperty( "password" );
            if ( username == null || username.isEmpty() || password == null || password.isEmpty() )
            {
                t.connect();
            }
            else
            {
                if ( port == null || port.isEmpty() )
                {
                    t.connect( host ,
                               username ,
                               password );
                }
                else
                {
                    t.connect( host ,
                               Integer.parseInt( port ) ,
                               username ,
                               password );
                }
            }

            t.sendMessage( msg ,
                           msg.getAllRecipients() );
        }
        finally
        {
            if ( t != null )
            {
                t.close();
            }
        }
    }
}
