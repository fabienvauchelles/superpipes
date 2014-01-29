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

package com.vaushell.superpipes.nodes.tumblr;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.nodes.A_Node;
import com.vaushell.superpipes.tools.scribe.tumblr.TB_Post;
import com.vaushell.superpipes.tools.scribe.tumblr.TumblrClient;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Post a message to Tumblr.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_TB
    extends A_Node
{
    // PUBLIC
    public N_TB()
    {
        super( null ,
               DEFAULT_ANTIBURST );

        this.client = new TumblrClient();
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

    @SuppressWarnings( "unchecked" )
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
        final Iterator<TB_Post> it = client.iteratorFeed( getProperties().getConfigString( "blogname" ) ,
                                                          Math.min( POST_MAX_COUNT ,
                                                                    max ) );
        while ( it.hasNext() && count < max )
        {
            final TB_Post post = it.next();

            if ( post.getID() >= 0 )
            {
                setMessage( Message.create(
                    "id-tumblr" ,
                    post.getID() ,
                    Message.KeyIndex.PUBLISHED_DATE ,
                    post.getTimestamp()
                ) );

                if ( post.getBlog() != null && post.getBlog().getTitle() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.AUTHOR ,
                                              post.getBlog().getTitle() );
                }

                if ( post.getMessage() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.CONTENT ,
                                              post.getMessage() );
                }

                if ( post.getURL() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.URI ,
                                              URI.create( post.getURL() ) );
                }

                if ( post.getURLdescription() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.DESCRIPTION ,
                                              post.getURLdescription() );
                }

                if ( post.getURLname() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.TITLE ,
                                              post.getURLname() );
                }

                if ( post.getTags() != null )
                {
                    getMessage().setProperty( Message.KeyIndex.TAGS ,
                                              post.getTags() );
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
    private static final Logger LOGGER = LoggerFactory.getLogger( N_TB.class );
    private final TumblrClient client;
    private static final int POST_MAX_COUNT = 20;
}
