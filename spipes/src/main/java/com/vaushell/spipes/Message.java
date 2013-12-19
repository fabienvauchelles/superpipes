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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Message object.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class Message
    implements Serializable
{
    // PUBLIC
    /**
     * Properties indexes list.
     */
    public enum KeyIndex
    {
        // PUBLIC
        TITLE( "title" ),
        DESCRIPTION( "description" ),
        URI( "uri" ),
        URI_SOURCE( "uri-source" ),
        AUTHOR( "author" ),
        TAGS( "tags" ),
        PUBLISHED_DATE( "published-date" ),
        CONTENT( "content" );

        // PRIVATE
        private final String index;

        private KeyIndex( final String index )
        {
            this.index = index;
        }
    }

    /**
     * Create a message a full message.
     *
     * @param properties pair args are key (String or KeyIndex), impair args are value (Serializable)
     * @return the Message
     */
    public static Message create( final Object... properties )
    {
        if ( properties == null || properties.length % 2 == 1 )
        {
            throw new IllegalArgumentException();
        }

        final Message m = new Message();

        int i = 0;
        while ( i < properties.length )
        {
            final Serializable value = (Serializable) properties[ i + 1];

            final Object okey = properties[ i];
            if ( okey instanceof String )
            {
                m.setProperty( (String) okey ,
                               value );
            }
            else if ( okey instanceof KeyIndex )
            {
                m.setProperty( (KeyIndex) okey ,
                               value );
            }
            else
            {
                throw new ClassCastException();
            }

            i += 2;
        }

        return m;
    }

    /**
     * Return the message ID. Generate lazy initialization.
     *
     * @return the ID
     */
    public String getID()
    {
        if ( hasToRebuildID )
        {
            rebuildID();

            hasToRebuildID = false;
        }

        return ID;
    }

    /**
     * Does the message contain this property ?
     *
     * @param key Property index
     * @return true or not
     */
    public boolean contains( final KeyIndex key )
    {
        return contains( key.index );
    }

    /**
     * Does the message contain this property ?
     *
     * @param key Property index
     * @return true or not
     */
    public boolean contains( final String key )
    {
        return properties.containsKey( key );
    }

    /**
     * Return the property value.
     *
     * @param key Property index
     * @return the property value
     */
    public Serializable getProperty( final KeyIndex key )
    {
        return getProperty( key.index );
    }

    /**
     * Return the property value.
     *
     * @param key Property index
     * @return the property value
     */
    public Serializable getProperty( final String key )
    {
        return properties.get( key );
    }

    /**
     * Remove a property.
     *
     * @param key Property index
     */
    public void removeProperty( final KeyIndex key )
    {
        removeProperty( key.index );
    }

    /**
     * Remove a property.
     *
     * @param key Property index
     */
    public void removeProperty( final String key )
    {
        properties.remove( key );

        hasToRebuildID = true;
    }

    /**
     * Set the property value.
     *
     * @param key Property index
     * @param value Property value
     */
    public void setProperty( final KeyIndex key ,
                             final Serializable value )
    {
        setProperty( key.index ,
                     value );
    }

    /**
     * Set the property value.
     *
     * @param key Property index
     * @param value Property value
     */
    public void setProperty( final String key ,
                             final Serializable value )
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

    /**
     * Return the properties indexes list.
     *
     * @return a set of keys
     */
    public Set<String> getKeys()
    {
        return properties.keySet();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder( "Message{ID=" );
        sb.append( getID() );

        for ( final Entry<String , Serializable> entry : properties.entrySet() )
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
    private static final long serialVersionUID = 944934823467345234L;
    private transient String ID;
    private TreeMap<String , Serializable> properties;
    private transient boolean hasToRebuildID;

    private Message()
    {
        this.ID = null;
        this.properties = new TreeMap<>();
        this.hasToRebuildID = true;
    }

    private void rebuildID()
    {
        final StringBuilder sb = new StringBuilder();
        for ( final Entry<String , Serializable> entry : properties.entrySet() )
        {
            sb.append( entry.getKey() )
                .append( '#' )
                .append( entry.getValue().toString() );
        }

        ID = DigestUtils.md5Hex( sb.toString() );
    }

    private void writeObject( final ObjectOutputStream os )
        throws IOException
    {
        os.writeObject( properties );
    }

    @SuppressWarnings( "unchecked" )
    private void readObject( final ObjectInputStream is )
        throws IOException , ClassNotFoundException
    {
        this.ID = null;
        properties = (TreeMap<String , Serializable>) is.readObject();
        hasToRebuildID = true;
    }
}
