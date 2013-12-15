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

package com.vaushell.spipes.nodes.shaarli;

import com.vaushell.spipes.transforms.bitly.I_URIshorten;
import com.vaushell.spipes.transforms.done.I_Identifier;
import java.net.URI;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class ShareLink
    implements I_Identifier , I_URIshorten
{
    // PUBLIC
    public static ShareLink create( final String ID ,
                                    final String title ,
                                    final String description ,
                                    final URI uri ,
                                    final URI uriSource ,
                                    final URI permaURI ,
                                    final Set<String> tags )
    {
        if ( ID == null || ID.isEmpty()
             || title == null || title.isEmpty()
             || uri == null || uri.toString().isEmpty()
             || permaURI == null || permaURI.toString().isEmpty()
             || tags == null )
        {
            throw new IllegalArgumentException();
        }

        return new ShareLink( ID ,
                              title ,
                              description ,
                              uri ,
                              uriSource ,
                              permaURI ,
                              tags );
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

    @Override
    public URI getURIsource()
    {
        return uriSource;
    }

    @Override
    public void setURIsource( final URI uriSource )
    {
        this.uriSource = uriSource;
    }

    public URI getPermaURI()
    {
        return permaURI;
    }

    public void setPermaURI( final URI permaURI )
    {
        this.permaURI = permaURI;
    }

    public Set<String> getTags()
    {
        return tags;
    }

    public void setTags( final Set<String> tags )
    {
        this.tags = tags;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode( this.ID );
        hash = 53 * hash + Objects.hashCode( this.title );
        hash = 53 * hash + Objects.hashCode( this.description );
        hash = 53 * hash + Objects.hashCode( this.uri );
        hash = 53 * hash + Objects.hashCode( this.uriSource );
        hash = 53 * hash + Objects.hashCode( this.permaURI );
        hash = 53 * hash + Objects.hashCode( this.tags );
        return hash;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        final ShareLink other = (ShareLink) obj;
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
        if ( !Objects.equals( this.uri ,
                              other.uri ) )
        {
            return false;
        }
        if ( !Objects.equals( this.uriSource ,
                              other.uriSource ) )
        {
            return false;
        }
        if ( !Objects.equals( this.permaURI ,
                              other.permaURI ) )
        {
            return false;
        }
        if ( !Objects.equals( this.tags ,
                              other.tags ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "ShareLink{" + "ID=" + ID + ", title=" + title + ", description=" + description + ", uri=" + uri + ", uriSource=" + uriSource + ", permaURI=" + permaURI + ", tags=" + tags + '}';
    }

    // PRIVATE
    private String ID;
    private String title;
    private String description;
    private URI uri;
    private URI uriSource;
    private URI permaURI;
    private Set<String> tags;

    private ShareLink( final String ID ,
                       final String title ,
                       final String description ,
                       final URI uri ,
                       final URI uriSource ,
                       final URI permaURI ,
                       final Set<String> tags )
    {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.uri = uri;
        this.uriSource = uriSource;
        this.permaURI = permaURI;
        this.tags = tags;
    }

}
