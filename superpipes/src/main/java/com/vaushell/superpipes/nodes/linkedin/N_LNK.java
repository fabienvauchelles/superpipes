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

package com.vaushell.superpipes.nodes.linkedin;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.nodes.twitter.N_TW_Post;
import com.vaushell.superpipes.tools.scribe.linkedin.LNK_Status;
import com.vaushell.superpipes.tools.scribe.linkedin.LNK_User;
import com.vaushell.superpipes.tools.scribe.linkedin.LinkedInClient;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read a LinkedIn feed.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_LNK
    extends A_Node
{
    // PUBLIC
    public N_LNK()
    {
        // Read every 10 minutes
        super( new Duration( 600000L ) ,
               null );

        this.client = new LinkedInClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = getDispatcher().getDatas().resolve( Paths.get( getNodeID() ,
                                                                              "token" ) );

        client.login( getProperties().getConfigString( "key" ) ,
                      getProperties().getConfigString( "secret" ) ,
                      tokenPath ,
                      getDispatcher().getVCodeFactory().create( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
    }

    @Override
    protected void loop()
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] read feed " );
        }

        final int max = getProperties().getConfigInteger( "max" );

        int count = 0;
        final Iterator<LNK_Status> it = client.iteratorFeed( getProperties().getConfigString( "userid" ,
                                                                                              null ) ,
                                                             Math.min( POST_MAX_COUNT ,
                                                                       max ) );
        while ( it.hasNext() && count < max )
        {
            final LNK_Status status = it.next();

            if ( status.getID() != null )
            {
                setMessage( Message.create(
                    "id-linkedin" ,
                    status.getID() ,
                    Message.KeyIndex.PUBLISHED_DATE ,
                    status.getTimestamp()
                ) );

                final String username = LNK_User.formatName( status.getPerson() );
                if ( username != null )
                {
                    getMessage().setProperty( Message.KeyIndex.AUTHOR ,
                                              username );
                }

                if ( status.getMessage() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.CONTENT ,
                                              status.getMessage() );
                }

                if ( status.getURLshorten() == null )
                {
                    if ( status.getURL() != null )
                    {
                        getMessage().setProperty( Message.KeyIndex.URI ,
                                                  URI.create( status.getURL() ) );
                    }
                }
                else
                {
                    getMessage().setProperty( Message.KeyIndex.URI ,
                                              URI.create( status.getURLshorten() ) );

                    if ( status.getURL() != null )
                    {
                        getMessage().setProperty( Message.KeyIndex.URI_SOURCE ,
                                                  URI.create( status.getURL() ) );
                    }
                }

                if ( status.getURLdescription() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.DESCRIPTION ,
                                              status.getURLdescription() );
                }

                if ( status.getURLname() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.TITLE ,
                                              status.getURLname() );
                }

                sendMessage();
            }

            ++count;
        }
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TW_Post.class );
    private static final int POST_MAX_COUNT = 250;
    private final LinkedInClient client;
}
