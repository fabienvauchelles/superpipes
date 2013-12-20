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

package com.vaushell.spipes.tools.scribe.tumblr;

import java.util.Objects;

/**
 * A Tumblr blog.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TB_Blog
{
    // PUBLIC
    public TB_Blog( final String ID ,
                    final String title ,
                    final String description ,
                    final String url )
    {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getID()
    {
        return ID;
    }

    public void setID( final String ID )
    {
        this.ID = ID;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( final String title )
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public String getURL()
    {
        return url;
    }

    public void setURL( final String url )
    {
        this.url = url;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;

        hash = 31 * hash + Objects.hashCode( this.ID );
        hash = 31 * hash + Objects.hashCode( this.title );
        hash = 31 * hash + Objects.hashCode( this.description );
        hash = 31 * hash + Objects.hashCode( this.url );

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

        final TB_Blog other = (TB_Blog) obj;
        if ( !Objects.equals( this.ID ,
                              other.ID ) )
        {
            return false;
        }

        if ( !Objects.equals( this.title ,
                              other.title ) )
        {
            return false;
        }

        if ( !Objects.equals( this.description ,
                              other.description ) )
        {
            return false;
        }

        if ( !Objects.equals( this.url ,
                              other.url ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "TB_Blog{" + "ID=" + ID + ", title=" + title + ", description=" + description + ", url=" + url + '}';
    }

    // PRIVATE
    private String ID;
    private String title;
    private String description;
    private String url;
}
