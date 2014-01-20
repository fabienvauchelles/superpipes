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

package com.vaushell.superpipes.tools.scribe.linkedin;

import java.util.Objects;

/**
 * A LinkedIn user.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class LNK_User
{
    // PUBLIC
    public LNK_User( final String ID ,
                     final String firstName ,
                     final String lastName ,
                     final String job )
    {
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.job = job;
    }

    public String getID()
    {
        return ID;
    }

    public void setID( final String ID )
    {
        this.ID = ID;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( final String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( final String lastName )
    {
        this.lastName = lastName;
    }

    public String getJob()
    {
        return job;
    }

    public void setJob( final String job )
    {
        this.job = job;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;

        hash = 37 * hash + Objects.hashCode( this.ID );
        hash = 37 * hash + Objects.hashCode( this.firstName );
        hash = 37 * hash + Objects.hashCode( this.lastName );
        hash = 37 * hash + Objects.hashCode( this.job );

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

        final LNK_User other = (LNK_User) obj;
        if ( !Objects.equals( this.ID ,
                              other.ID ) )
        {
            return false;
        }

        if ( !Objects.equals( this.firstName ,
                              other.firstName ) )
        {
            return false;
        }

        if ( !Objects.equals( this.lastName ,
                              other.lastName ) )
        {
            return false;
        }

        if ( !Objects.equals( this.job ,
                              other.job ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "LNK_User{" + "ID=" + ID + ", firstName=" + firstName + ", lastName=" + lastName + ", job=" + job + '}';
    }

    /**
     * Format user to full user name.
     *
     * @param user user object.
     * @return a username.
     */
    public static String formatName( final LNK_User user )
    {
        if ( user == null )
        {
            return null;
        }

        if ( user.getFirstName() == null )
        {
            if ( user.getLastName() == null )
            {
                return null;
            }
            else
            {
                return user.getLastName();
            }
        }
        else
        {
            if ( user.getLastName() == null )
            {
                return user.getFirstName();
            }
            else
            {
                return user.getFirstName() + ' ' + user.getLastName();
            }
        }
    }

    // PRIVATE
    private String ID;
    private String firstName;
    private String lastName;
    private String job;
}
