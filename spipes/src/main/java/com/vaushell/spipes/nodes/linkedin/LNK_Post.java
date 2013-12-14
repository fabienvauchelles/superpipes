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

package com.vaushell.spipes.nodes.linkedin;

import com.vaushell.spipes.nodes.bitly.I_URI;
import com.vaushell.spipes.transforms.done.I_Identifier;
import java.net.URI;
import java.util.Objects;

/**
 * A LinkedIn post.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class LNK_Post
    implements I_Identifier , I_URI
{
    // PUBLIC
    public LNK_Post( final String message ,
                     final URI uri ,
                     final String uriName ,
                     final String uriDescription )
    {
        this.ID = null;
        this.message = message;
        this.uri = uri;
        this.uriName = uriName;
        this.uriDescription = uriDescription;
    }

    @Override
    public String getID()
    {
        return ID;
    }

    @Override
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

    @Override
    public URI getURI()
    {
        return uri;
    }

    @Override
    public void setURI( final URI uri )
    {
        this.uri = uri;
    }

    public String getURIname()
    {
        return uriName;
    }

    public void setURIname( final String uriName )
    {
        this.uriName = uriName;
    }

    public String getURIdescription()
    {
        return uriDescription;
    }

    public void setURIdescription( final String uriDescription )
    {
        this.uriDescription = uriDescription;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;

        hash = 41 * hash + Objects.hashCode( this.ID );
        hash = 41 * hash + Objects.hashCode( this.message );
        hash = 41 * hash + Objects.hashCode( this.uri );
        hash = 41 * hash + Objects.hashCode( this.uriName );
        hash = 41 * hash + Objects.hashCode( this.uriDescription );

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

        final LNK_Post other = (LNK_Post) obj;
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

        if ( !Objects.equals( this.uri ,
                              other.uri ) )
        {
            return false;
        }

        if ( !Objects.equals( this.uriName ,
                              other.uriName ) )
        {
            return false;
        }

        if ( !Objects.equals( this.uriDescription ,
                              other.uriDescription ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "LNK_Post{" + "ID=" + ID + ", message=" + message + ", uri=" + uri + ", uriName=" + uriName + ", uriDescription=" + uriDescription + '}';
    }
    // PRIVATE
    private String ID;
    private String message;
    private URI uri;
    private String uriName;
    private String uriDescription;
}
