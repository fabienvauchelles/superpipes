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

package com.vaushell.superpipes.transforms.tags;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.dispatch.Tags;
import com.vaushell.superpipes.transforms.A_Transform;
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
        op = OpType.valueOf( getProperties().getConfigString( "type" ) );

        tags = getProperties().getConfigString( "tags" ).split( "," );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Message transform( final Message message )
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNode().getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + Message.
                formatSimple( message ) );
        }

        if ( !message.contains( Message.KeyIndex.TAGS ) )
        {
            return null;
        }

        final Tags mTags = (Tags) message.getProperty( Message.KeyIndex.TAGS );

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
                                        final Tags tags )
    {
        for ( final String tag1 : mustHave )
        {
            if ( contains( tag1 ,
                           tags ) )
            {
                return true;
            }
        }

        return false;
    }

    private static boolean containsAll( final String[] mustHave ,
                                        final Tags tags )
    {
        for ( final String tag1 : mustHave )
        {
            if ( !contains( tag1 ,
                            tags ) )
            {
                return false;
            }
        }

        return true;
    }

    private static boolean contains( final String mustHave ,
                                     final Tags tags )
    {
        for ( final String tag : tags.getAll() )
        {
            if ( mustHave.equals( tag ) )
            {
                return true;
            }
        }

        return false;
    }

}
