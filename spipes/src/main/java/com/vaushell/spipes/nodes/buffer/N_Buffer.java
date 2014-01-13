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

package com.vaushell.spipes.nodes.buffer;

import com.vaushell.spipes.dispatch.Message;
import com.vaushell.spipes.nodes.A_Node;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Buffer node. Regulate the flow.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_Buffer
    extends A_Node
{
    // PUBLIC
    public N_Buffer()
    {
        super( null ,
               null );

        this.slots = new ArrayList<>();
        this.messageIDs = new TreeSet<>();
        this.lastWrite = null;
        this.rnd = new Random();
    }

    @Override
    public void load( final HierarchicalConfiguration cNode )
        throws Exception
    {
        super.load( cNode );

        slots.clear();
        final List<HierarchicalConfiguration> sNodes = cNode.configurationsAt( "slots.slot" );
        if ( sNodes != null )
        {
            for ( final HierarchicalConfiguration sNode : sNodes )
            {
                final Slot slot = Slot.parse( sNode.getString( "[@days]" ) ,
                                              sNode.getString( "[@startat]" ) ,
                                              sNode.getString( "[@endat]" ) );

                slots.add( slot );
            }
        }

        final String flowLimitStr = getConfig( "flow-limit" );
        if ( flowLimitStr == null )
        {
            flowLimit = null;
        }
        else
        {
            flowLimit = new Duration( Long.parseLong( flowLimitStr ) );

            if ( flowLimit.getMillis() <= 0L )
            {
                throw new IllegalArgumentException( "flow-limit can't be <=0. Should be null or empty" );
            }
        }

        final String waitMinStr = getConfig( "wait-min" );
        final String waitMaxStr = getConfig( "wait-max" );
        if ( waitMinStr == null || waitMaxStr == null )
        {
            waitMin = null;
            waitMax = null;
        }
        else
        {
            waitMin = Integer.parseInt( waitMinStr );
            waitMax = Integer.parseInt( waitMaxStr );
        }
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        // Load messages IDs
        messagesPath = getDispatcher().getDatas().resolve( getNodeID() );

        Files.createDirectories( messagesPath );

        try( final DirectoryStream<Path> stream = Files.newDirectoryStream( messagesPath ) )
        {
            for ( final Path p : stream )
            {
                final long ID = Long.parseLong( p.getFileName().toString() );
                messageIDs.add( ID );
            }
        }
    }

    @Override
    protected void loop()
        throws Exception
    {
        // 1. Are we allowed to publish ?
        final DateTime now = new DateTime();

        final Duration time2wait = getTimeToWait( now );
        if ( time2wait.getMillis() <= 0L )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace(
                    "[" + getNodeID() + "] time to wait : " + time2wait + ". During this time, we're trying to catch an incoming message." );
            }

            final Message message = getLastMessageOrWait( time2wait );
            if ( message != null )
            {
                pushMessage( message );
            }

            // And loop again.
        }
        else
        {
            // 2. Pop from stack
            Message message = popMessage();
            if ( message == null )
            {
                if ( LOGGER.isTraceEnabled() )
                {
                    LOGGER.trace(
                        "[" + getNodeID() + "] no time to wait and not message in buffer. We're waiting an incoming message" );
                }

                // Nothing : we wait for external
                message = getLastMessageOrWait();

                // Push to stack
                pushMessage( message );

                // And loop to check if we're allowed to publish.
            }
            else
            {
                // 3. We published
                if ( LOGGER.isTraceEnabled() )
                {
                    LOGGER.trace(
                        "[" + getNodeID() + "] no time to wait and we found a message in the buffer. We're sending it." );
                }

                lastWrite = now;

                sendMessage( message );
            }
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_Buffer.class );
    private Duration flowLimit;
    private final List<Slot> slots;
    private final TreeSet<Long> messageIDs;
    private Integer waitMin;
    private Integer waitMax;
    private DateTime lastWrite;
    private Path messagesPath;
    private final Random rnd;

    private Message popMessage()
        throws IOException , ClassNotFoundException
    {
        if ( messageIDs.isEmpty() )
        {
            return null;
        }

        final Long ID = messageIDs.pollFirst();

        final Path p = messagesPath.resolve( Long.toString( ID ) );

        final Message m = readMessage( p );

        Files.delete( p );

        return m;
    }

    private void pushMessage( final Message message )
        throws IOException
    {
        final DateTime now = new DateTime();

        final Duration delta;
        if ( waitMin == null || waitMax == null )
        {
            delta = new Duration( 0L );
        }
        else
        {
            if ( waitMin.equals( waitMax ) )
            {
                delta = new Duration( (long) waitMin );
            }
            else
            {
                delta = new Duration( (long) ( rnd.nextInt( waitMax - waitMin ) + waitMin ) );
            }
        }

        final DateTime ID;
        if ( messageIDs.isEmpty() )
        {
            ID = now.plus( delta );
        }
        else
        {
            final DateTime askedTime = now.plus( delta );

            final long lastID = messageIDs.last();
            final DateTime lastTime = new DateTime( lastID );

            if ( askedTime.isBefore( lastTime ) )
            {
                ID = lastTime.plusMillis( 1 );
            }
            else
            {
                ID = askedTime;
            }
        }

        final Path p = messagesPath.resolve( Long.toString( ID.getMillis() ) );

        if ( LOGGER.isDebugEnabled() )
        {
            LOGGER.debug(
                "[" + getNodeID() + "] write message with ID=" + ID
            );
        }

        writeMessage( p ,
                      message );

        messageIDs.add( ID.getMillis() );
    }

    private Duration getTimeToWait( final DateTime from )
    {
        // Best slot
        Duration minDuration;
        if ( slots.isEmpty() )
        {
            minDuration = new Duration( 0L );
        }
        else
        {
            minDuration = null;
            for ( final Slot slot : slots )
            {
                final Duration duration = slot.getSmallestDiff( from );
                if ( minDuration == null || duration.isShorterThan( minDuration ) )
                {
                    minDuration = duration;

                    if ( minDuration.getMillis() <= 0L )
                    {
                        break;
                    }
                }
            }
        }

        // Anti burst
        if ( flowLimit != null && lastWrite != null )
        {
            final Duration diff = new Duration( lastWrite ,
                                                from );

            final Duration toAdd = flowLimit.minus( diff );
            if ( toAdd.isLongerThan( minDuration ) )
            {
                minDuration = toAdd;
            }
        }

        // First message
        if ( !messageIDs.isEmpty() )
        {
            final long firstID = messageIDs.first();
            final DateTime first = new DateTime( firstID );

            if ( first.isAfter( from ) )
            {
                final Duration diff = new Duration( from ,
                                                    first );
                if ( diff.isLongerThan( minDuration ) )
                {
                    minDuration = diff;
                }
            }
        }

        // Result
        return minDuration;
    }

    private static void writeMessage( final Path p ,
                                      final Message m )
        throws IOException
    {
        try( ObjectOutputStream os = new ObjectOutputStream( Files.newOutputStream( p ) ) )
        {
            os.writeObject( m );
        }
    }

    private static Message readMessage( final Path p )
        throws IOException , ClassNotFoundException
    {
        try( ObjectInputStream is = new ObjectInputStream( Files.newInputStream( p ) ) )
        {
            return (Message) is.readObject();
        }
    }
}
