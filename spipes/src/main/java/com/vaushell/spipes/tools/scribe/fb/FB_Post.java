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
import org.joda.time.DateTime;

/**
 * A Facebook Post.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class FB_Post
{
    // PUBLIC
    public FB_Post( final String ID ,
                    final String message ,
                    final String url ,
                    final String urlName ,
                    final String urlCaption ,
                    final String urlDescription ,
                    final FB_User from ,
                    final DateTime createdTime )
    {
        this.ID = ID;
        this.message = message;
        this.url = url;
        this.urlName = urlName;
        this.urlCaption = urlCaption;
        this.urlDescription = urlDescription;
        this.from = from;
        this.createdTime = createdTime;
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

    public String getURLname()
    {
        return urlName;
    }

    public void setURLname( final String urlName )
    {
        this.urlName = urlName;
    }

    public String getURLcaption()
    {
        return urlCaption;
    }

    public void setURLcaption( final String urlCaption )
    {
        this.urlCaption = urlCaption;
    }

    public String getURLdescription()
    {
        return urlDescription;
    }

    public void setURLdescription( final String urlDescription )
    {
        this.urlDescription = urlDescription;
    }

    public FB_User getFrom()
    {
        return from;
    }

    public void setFrom( final FB_User from )
    {
        this.from = from;
    }

    public DateTime getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime( final DateTime createdTime )
    {
        this.createdTime = createdTime;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;

        hash = 67 * hash + Objects.hashCode( this.ID );
        hash = 67 * hash + Objects.hashCode( this.message );
        hash = 67 * hash + Objects.hashCode( this.url );
        hash = 67 * hash + Objects.hashCode( this.urlName );
        hash = 67 * hash + Objects.hashCode( this.urlCaption );
        hash = 67 * hash + Objects.hashCode( this.urlDescription );
        hash = 67 * hash + Objects.hashCode( this.from );
        hash = 67 * hash + Objects.hashCode( this.createdTime );

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

        final FB_Post other = (FB_Post) obj;
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

        if ( !Objects.equals( this.urlName ,
                              other.urlName ) )
        {
            return false;
        }

        if ( !Objects.equals( this.urlCaption ,
                              other.urlCaption ) )
        {
            return false;
        }

        if ( !Objects.equals( this.urlDescription ,
                              other.urlDescription ) )
        {
            return false;
        }

        if ( !Objects.equals( this.from ,
                              other.from ) )
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
        return "FB_Post{" + "ID=" + ID + ", message=" + message + ", url=" + url + ", urlName=" + urlName + ", urlCaption=" + urlCaption + ", urlDescription=" + urlDescription + ", from=" + from + ", createdTime=" + createdTime + '}';
    }

    // PRIVATE
    private String ID;
    private String message;
    private String url;
    private String urlName;
    private String urlCaption;
    private String urlDescription;
    private FB_User from;
    private DateTime createdTime;
}
