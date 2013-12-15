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

package com.vaushell.spipes.transforms.tags;

import com.vaushell.spipes.transforms.A_Transform;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_ExcludeTags
    extends A_Transform
{
    // PUBLIC
    public T_ExcludeTags()
    {
        // Nothing
    }

    @Override
    public void prepare()
        throws Exception
    {
        final String tagsFilter = getConfig( "tags" );
        if ( tagsFilter == null || tagsFilter.isEmpty() )
        {
            throw new IllegalArgumentException( "tags parameter should be specified" );
        }

        tags = tagsFilter.split( "," );
    }

    @Override
    public Object transform( final Object message )
        throws Exception
    {
        final I_Tags msg = (I_Tags) message;

        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + msg );
        }

        if ( contains( tags ,
                       msg.getTags() ) )
        {
            return null;
        }
        else
        {
            return message;
        }
    }

    @Override
    public void terminate()
        throws Exception
    {
        // Nothing
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( T_ExcludeTags.class );
    private String[] tags;

    private static boolean contains( final String[] tags1 ,
                                     final Set<String> tags2 )
    {
        for ( final String tag1 : tags1 )
        {
            for ( final String tag2 : tags2 )
            {
                if ( tag1.equals( tag2 ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

}
