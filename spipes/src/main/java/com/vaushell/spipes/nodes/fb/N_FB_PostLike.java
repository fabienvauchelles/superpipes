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

package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.Message;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.scribe.code.VC_SystemInput;
import com.vaushell.spipes.tools.scribe.fb.FacebookClient;
import com.vaushell.spipes.tools.scribe.fb.FacebookException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Like a message on Facebook.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class N_FB_PostLike
    extends A_Node
{
    // PUBLIC
    public N_FB_PostLike()
    {
        super();

        this.client = new FacebookClient();
    }

    // PROTECTED
    @Override
    protected void prepareImpl()
        throws Exception
    {
        final Path tokenPath = Paths.get( getMainConfig( "datas-directory" ) ,
                                          getNodeID() ,
                                          "token" );

        client.login( getConfig( "key" ) ,
                      getConfig( "secret" ) ,
                      "publish_stream" ,
                      tokenPath ,
                      new VC_SystemInput( "[" + getClass().getName() + " / " + getNodeID() + "] " ) );
    }

    @Override
    protected void loop()
        throws InterruptedException , IOException , FacebookException
    {
        // Receive
        final Message message = (Message) getLastMessageOrWait();

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "] receive message : " + message );
        }

        if ( !message.contains( "id-facebook" ) )
        {
            throw new IllegalArgumentException( "message doesn't have an post id" );
        }

        // Like
        client.likePost( (String) message.getProperty( "id-facebook" ) );
    }

    @Override
    protected void terminateImpl()
        throws Exception
    {
        // Nothing
    }
    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( N_FB_PostLike.class );
    private final FacebookClient client;
}
