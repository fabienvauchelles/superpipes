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

package com.vaushell.spipes;

import java.util.Map.Entry;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class Message
{
    // PUBLIC
    public Message()
    {
        this.ID = null;
        this.properties = new TreeMap<>();
        this.hasToRebuildID = false;
    }

    public String getID()
    {
        if ( hasToRebuildID )
        {
            rebuildID();

            hasToRebuildID = false;
        }

        return ID;
    }

    public boolean contains( final String key )
    {
        return properties.containsKey( key );
    }

    public Object getProperty( final String key )
    {
        return properties.get( key );
    }

    public void removeProperty( final String key )
    {
        properties.remove( key );

        hasToRebuildID = true;
    }

    public void setProperty( final String key ,
                             final Object value )
    {
        if ( value == null )
        {
            properties.remove( key );
        }
        else
        {
            properties.put( key ,
                            value );
        }

        hasToRebuildID = true;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "Message{ID=" );
        sb.append( getID() );

        for ( final Entry<String , Object> entry : properties.entrySet() )
        {
            sb.append( ", " )
                .append( entry.getKey() )
                .append( '=' )
                .append( entry.getValue() );
        }

        sb.append( '}' );

        return sb.toString();
    }

    // PRIVATE
    private String ID;
    private final TreeMap<String , Object> properties;
    private boolean hasToRebuildID;

    private void rebuildID()
    {
        final StringBuilder sb = new StringBuilder();
        for ( final Entry<String , Object> entry : properties.entrySet() )
        {
            sb.append( entry.getKey() )
                .append( '#' )
                .append( entry.getValue().toString() );
        }

        ID = DigestUtils.md5Hex( sb.toString() );
    }
}
