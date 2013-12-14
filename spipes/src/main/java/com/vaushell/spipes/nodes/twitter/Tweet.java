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

package com.vaushell.spipes.nodes.twitter;

import com.vaushell.spipes.transforms.done.I_Identifier;
import java.util.Objects;

/**
 * A tweet.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class Tweet
    implements I_Identifier
{
    // PUBLIC
    public Tweet( final String message )
    {
        this.ID = Long.MIN_VALUE;
        this.message = message;
    }

    @Override
    public String getID()
    {
        return Long.toString( ID );
    }

    public long getTweetID()
    {
        return ID;
    }

    @Override
    public void setID( final String ID )
    {
        this.ID = Long.parseLong( ID );
    }

    public void setTweetID( final long ID )
    {
        this.ID = ID;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( final String message )
    {
        this.message = message;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;

        hash = 83 * hash + (int) ( this.ID ^ ( this.ID >>> 32 ) );
        hash = 83 * hash + Objects.hashCode( this.message );

        return hash;
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

        final Tweet other = (Tweet) obj;
        if ( this.ID != other.ID )
        {
            return false;
        }

        if ( !Objects.equals( this.message ,
                              other.message ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "Tweet{" + "ID=" + ID + ", message=" + message + '}';
    }
    // PRIVATE
    private long ID;
    private String message;
}
