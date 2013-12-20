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

package com.vaushell.spipes.tools.scribe.fb;

import java.util.Objects;

/**
 * A Facebook User.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class FB_User
{
    // PUBLIC
    public FB_User( final String name ,
                    final String ID )
    {
        this.name = name;
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

    public String getID()
    {
        return ID;
    }

    public void setID( final String ID )
    {
        this.ID = ID;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;

        hash = 23 * hash + Objects.hashCode( this.name );
        hash = 23 * hash + Objects.hashCode( this.ID );

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

        final FB_User other = (FB_User) obj;
        if ( !Objects.equals( this.name ,
                              other.name ) )
        {
            return false;
        }

        if ( !Objects.equals( this.ID ,
                              other.ID ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "FB_User{" + "name=" + name + ", ID=" + ID + '}';
    }

    // PRIVATE
    private String name;
    private String ID;
}
