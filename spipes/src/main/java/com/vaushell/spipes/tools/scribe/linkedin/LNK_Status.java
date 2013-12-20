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

package com.vaushell.spipes.tools.scribe.linkedin;

import java.util.Objects;

/**
 * A LinkedIn status.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class LNK_Status
{

    // PUBLIC
    public LNK_Status( final String ID ,
                       final String message ,
                       final String url ,
                       final String urlShorten ,
                       final String urlName ,
                       final String urlDescription ,
                       final LNK_User person ,
                       final long timestamp )
    {
        this.ID = ID;
        this.message = message;
        this.url = url;
        this.urlShorten = urlShorten;
        this.urlName = urlName;
        this.urlDescription = urlDescription;
        this.person = person;
        this.timestamp = timestamp;
    }

    public String getID()
    {
        return ID;
    }

    public void setID( final String ID )
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

    public String getURL()
    {
        return url;
    }

    public void setURL( final String url )
    {
        this.url = url;
    }

    public String getURLshorten()
    {
        return urlShorten;
    }

    public void setURLshorten( final String urlShorten )
    {
        this.urlShorten = urlShorten;
    }

    public String getURLname()
    {
        return urlName;
    }

    public void setURLname( final String urlName )
    {
        this.urlName = urlName;
    }

    public String getURLdescription()
    {
        return urlDescription;
    }

    public void setURLdescription( final String urlDescription )
    {
        this.urlDescription = urlDescription;
    }

    public LNK_User getPerson()
    {
        return person;
    }

    public void setPerson( final LNK_User person )
    {
        this.person = person;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( final long timestamp )
    {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;

        hash = 59 * hash + Objects.hashCode( this.ID );
        hash = 59 * hash + Objects.hashCode( this.message );
        hash = 59 * hash + Objects.hashCode( this.url );
        hash = 59 * hash + Objects.hashCode( this.urlShorten );
        hash = 59 * hash + Objects.hashCode( this.urlName );
        hash = 59 * hash + Objects.hashCode( this.urlDescription );
        hash = 59 * hash + Objects.hashCode( this.person );
        hash = 59 * hash + (int) ( this.timestamp ^ ( this.timestamp >>> 32 ) );

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

        final LNK_Status other = (LNK_Status) obj;
        if ( !Objects.equals( this.ID ,
                              other.ID ) )
        {
            return false;
        }

        if ( !Objects.equals( this.message ,
                              other.message ) )
        {
            return false;
        }

        if ( !Objects.equals( this.url ,
                              other.url ) )
        {
            return false;
        }

        if ( !Objects.equals( this.urlShorten ,
                              other.urlShorten ) )
        {
            return false;
        }

        if ( !Objects.equals( this.urlName ,
                              other.urlName ) )
        {
            return false;
        }

        if ( !Objects.equals( this.urlDescription ,
                              other.urlDescription ) )
        {
            return false;
        }

        if ( !Objects.equals( this.person ,
                              other.person ) )
        {
            return false;
        }

        if ( this.timestamp != other.timestamp )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "LNK_Status{" + "ID=" + ID + ", message=" + message + ", url=" + url + ", urlShorten=" + urlShorten + ", urlName=" + urlName + ", urlDescription=" + urlDescription + ", person=" + person + ", timestamp=" + timestamp + '}';
    }

    // PRIVATE
    private String ID;
    private String message;
    private String url;
    private String urlShorten;
    private String urlName;
    private String urlDescription;
    private LNK_User person;
    private long timestamp;
}
