/*
 * Copyright (C) 2014 Fabien Vauchelles (fabien_AT_vauchelles_DOT_com).
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

package com.vaushell.superpipes.dispatch;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Tags.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class Tags
    implements Serializable
{
    // PUBLIC
    public Tags( final String... mtags )
    {
        this.mtags = new TreeSet<>();

        for ( final String mtag : mtags )
        {
            this.mtags.add( mtag );
        }
    }

    /**
     * Add a tag.
     *
     * @param tag Tag
     */
    public void add( final String tag )
    {
        if ( tag == null || tag.isEmpty() )
        {
            return;
        }

        mtags.add( tag.toLowerCase( Locale.ENGLISH ) );
    }

    /**
     * Remove a tag.
     *
     * @param tag Tag
     */
    public void remove( final String tag )
    {
        if ( tag == null || tag.isEmpty() )
        {
            return;
        }

        mtags.remove( tag.toLowerCase( Locale.ENGLISH ) );
    }

    /**
     * Clear all tags.
     */
    public void clear()
    {
        mtags.clear();
    }

    /**
     * Is tag inside.
     *
     * @param tag Tag
     * @return true if it's inside.
     */
    public boolean contains( final String tag )
    {
        if ( tag == null || tag.isEmpty() )
        {
            return false;
        }

        return mtags.contains( tag.toLowerCase( Locale.ENGLISH ) );
    }

    /**
     * Is empty.
     *
     * @return true if it's empty.
     */
    public boolean isEmpty()
    {
        return mtags.isEmpty();
    }

    /**
     * Return tags count.
     *
     * @return count
     */
    public int size()
    {
        return mtags.size();
    }

    /**
     * Get all tags.
     *
     * @return all tags
     */
    public Set<String> getAll()
    {
        return mtags;
    }

    /**
     * Get an array of all tags.
     *
     * @return array of tags
     */
    public String[] toArray()
    {
        return mtags.toArray( new String[ mtags.size() ] );
    }

    @Override
    public int hashCode()
    {
        return 185 + Objects.hashCode( this.mtags );
    }

    @Override
    public boolean equals( final Object obj )
    {
        if ( obj == null )
        {
            return false;
        }

        if ( getClass() != obj.getClass() )
        {
            return false;
        }

        final Tags other = (Tags) obj;
        if ( !Objects.equals( this.mtags ,
                              other.mtags ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        for ( final String tag : mtags )
        {
            if ( sb.length() > 0 )
            {
                sb.append( ',' );
            }

            sb.append( tag );
        }

        return sb.insert( 0 ,
                          '{' ).append( '}' ).toString();
    }

    // PRIVATE
    private static final long serialVersionUID = 45692348346345L;
    private final Set<String> mtags;
}
