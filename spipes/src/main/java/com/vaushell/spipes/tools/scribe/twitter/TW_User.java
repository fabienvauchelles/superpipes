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
 * A Twitter user.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TW_User
{
    // PUBLIC
    public TW_User( final long ID ,
                    final String name ,
                    final String screenName )
    {
        this.ID = ID;
        this.name = name;
        this.screenName = screenName;
    }

    public long getID()
    {
        return ID;
    }

    public void setID( final long ID )
    {
        this.ID = ID;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public String getScreenName()
    {
        return screenName;
    }

    public void setScreenName( final String screenName )
    {
        this.screenName = screenName;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;

        hash = 29 * hash + (int) ( this.ID ^ ( this.ID >>> 32 ) );
        hash = 29 * hash + Objects.hashCode( this.name );
        hash = 29 * hash + Objects.hashCode( this.screenName );

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

        final TW_User other = (TW_User) obj;
        if ( this.ID != other.ID )
        {
            return false;
        }

        if ( !Objects.equals( this.name ,
                              other.name ) )
        {
            return false;
        }

        if ( !Objects.equals( this.screenName ,
                              other.screenName ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "TW_User{" + "ID=" + ID + ", name=" + name + ", screenName=" + screenName + '}';
    }

    // PRIVATE
    private long ID;
    private String name;
    private String screenName;

}
