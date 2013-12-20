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

package com.vaushell.spipes.tools.scribe.twitter;

import java.util.Objects;

/**
 * A tweet.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TW_Tweet
{
    // PUBLIC
    public TW_Tweet( final long ID ,
                     final String message ,
                     final TW_User user ,
                     final long createdTime )
    {
        this.ID = ID;
        this.message = message;
        this.user = user;
        this.createdTime = createdTime;
    }

    public long getID()
    {
        return ID;
    }

    public void setID( final long ID )
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

    public TW_User getUser()
    {
        return user;
    }

    public void setUser( final TW_User user )
    {
        this.user = user;
    }

    public long getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime( final long createdTime )
    {
        this.createdTime = createdTime;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;

        hash = 59 * hash + (int) ( this.ID ^ ( this.ID >>> 32 ) );
        hash = 59 * hash + Objects.hashCode( this.message );
        hash = 59 * hash + Objects.hashCode( this.user );
        hash = 59 * hash + (int) ( this.createdTime ^ ( this.createdTime >>> 32 ) );

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

        final TW_Tweet other = (TW_Tweet) obj;
        if ( this.ID != other.ID )
        {
            return false;
        }

        if ( !Objects.equals( this.message ,
                              other.message ) )
        {
            return false;
        }

        if ( !Objects.equals( this.user ,
                              other.user ) )
        {
            return false;
        }

        if ( this.createdTime != other.createdTime )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "TW_Tweet{" + "ID=" + ID + ", message=" + message + ", user=" + user + ", createdTime=" + createdTime + '}';
    }

    // PRIVATE
    private long ID;
    private String message;
    private TW_User user;
    private long createdTime;
}
