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

import com.vaushell.spipes.Message;
import com.vaushell.spipes.transforms.A_Transform;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filter message if he has no tags, or exlude some tags, or include, etc.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_Tags
    extends A_Transform
{
    // PUBLIC
    /**
     * Tags filter operation type.
     */
    public enum OpType
    {
        INCLUDE_ONE,
        INCLUDE_ALL,
        EXCLUDE_ONE,
        EXCLUDE_ALL;
    }

    public T_Tags()
    {
        super();
    }

    @Override
    public void prepare()
        throws Exception
    {
        op = OpType.valueOf( getConfig( "type" ) );

        final String tagsFilter = getConfig( "tags" );
        if ( tagsFilter == null || tagsFilter.isEmpty() )
        {
            throw new IllegalArgumentException( "tags parameter should be specified" );
        }

        tags = tagsFilter.split( "," );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Message transform( final Message message )
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + message );
        }

        if ( !message.contains( Message.KeyIndex.TAGS ) )
        {
            return null;
        }

        final Set<String> mTags = (Set<String>) message.getProperty( Message.KeyIndex.TAGS );

        switch( op )
        {
            case INCLUDE_ALL:
            {
                if ( containsAll( tags ,
                                  mTags ) )
                {
                    return message;
                }
                else
                {
                    return null;
                }
            }

            case INCLUDE_ONE:
            {
                if ( containsOne( tags ,
                                  mTags ) )
                {
                    return message;
                }
                else
                {
                    return null;
                }
            }

            case EXCLUDE_ALL:
            {
                if ( containsAll( tags ,
                                  mTags ) )
                {
                    return null;
                }
                else
                {
                    return message;
                }
            }

            case EXCLUDE_ONE:
            {
                if ( containsOne( tags ,
                                  mTags ) )
                {
                    return null;
                }
                else
                {
                    return message;
                }
            }

            default:
            {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public void terminate()
        throws Exception
    {
        // Nothing
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( T_Tags.class );
    private String[] tags;
    private OpType op;

    private static boolean containsOne( final String[] mustHave ,
                                        final Set<String> tagsSet )
    {
        for ( final String tag1 : mustHave )
        {
            if ( contains( tag1 ,
                           tagsSet ) )
            {
                return true;
            }
        }

        return false;
    }

    private static boolean containsAll( final String[] mustHave ,
                                        final Set<String> tagsSet )
    {
        for ( final String tag1 : mustHave )
        {
            if ( !contains( tag1 ,
                            tagsSet ) )
            {
                return false;
            }
        }

        return true;
    }

    private static boolean contains( final String mustHave ,
                                     final Set<String> tagsSet )
    {
        for ( final String tag : tagsSet )
        {
            if ( mustHave.equals( tag ) )
            {
                return true;
            }
        }

        return false;
    }

}
