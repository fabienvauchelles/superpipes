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

import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeSet;
import org.apache.commons.configuration.HierarchicalConfiguration;
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
        super();

        this.slots = new ArrayList<>();
        this.messageIDs = new TreeSet<>();
        this.lastWrite = null;
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
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        // Load slots

        flowLimit = Long.parseLong( getConfig( "flow-limit" ) );

        messagesPath = getDispatcher().getDatas().resolve( getNodeID() );

        Files.createDirectories( messagesPath );

        try( final DirectoryStream<Path> stream = Files.newDirectoryStream( messagesPath ) )
        {
            for ( final Path p : stream )
            {
                messageIDs.add( p.getFileName().toString() );
            }
        }
    }

    @Override
    protected void loop()
        throws Exception
    {
        // 1. Are we allowed to publish ?
        final Calendar cal = Calendar.getInstance();

        final long ttw = getTimeToWait( cal );
        if ( ttw > 0 )
        {
            if ( LOGGER.isTraceEnabled() )
            {
                LOGGER.trace(
                    "[" + getNodeID() + "] time to wait : " + ttw + "ms. During this time, we're trying to catch an incoming message." );
            }

            final Message message = getLastMessageOrWait( ttw );
            if ( message != null )
            {
                pushMessage( message );
            }
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

                lastWrite = cal.getTimeInMillis();

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
    private long flowLimit;
    private final List<Slot> slots;
    private final TreeSet<String> messageIDs;
    private Long lastWrite;
    private Path messagesPath;

    private Message popMessage()
        throws IOException , ClassNotFoundException
    {
        if ( messageIDs.isEmpty() )
        {
            return null;
        }

        final String ID = messageIDs.pollFirst();

        final Path p = Paths.get( messagesPath.toString() ,
                                  ID );

        final Message m = readMessage( p );

        Files.delete( p );

        return m;
    }

    private void pushMessage( final Message message )
        throws IOException
    {
        final String ID = Long.toString( Calendar.getInstance().getTimeInMillis() );

        Path p = Paths.get( messagesPath.toString() ,
                            ID );

        int i = 2;
        while ( Files.exists( p ) )
        {
            p = Paths.get( messagesPath.toString() ,
                           ID + i );

            ++i;
        }

        writeMessage( p ,
                      message );

        messageIDs.add( ID );
    }

    private long getTimeToWait( final Calendar calendar )
    {
        // Best slot
        long minTime;
        if ( slots.isEmpty() )
        {
            minTime = 0;
        }
        else
        {
            minTime = Long.MAX_VALUE;
            for ( final Slot slot : slots )
            {
                final long time = slot.getSmallestDiffInMs( calendar );
                if ( time < minTime )
                {
                    minTime = time;

                    if ( minTime == 0 )
                    {
                        break;
                    }
                }
            }
        }

        // Anti burst
        if ( lastWrite != null )
        {
            final long diff = calendar.getTimeInMillis() - lastWrite;
            if ( diff < flowLimit )
            {
                minTime = Math.max( minTime ,
                                    flowLimit - diff );
            }
        }

        return minTime;
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
