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
import java.util.Set;

/**
 * A Tumblr post.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class TB_Post
{
    // PUBLIC
    public TB_Post( final long ID ,
                    final String message ,
                    final String url ,
                    final String urlName ,
                    final String urlDescription ,
                    final String type ,
                    final String slug ,
                    final long timestamp ,
                    final Set<String> tags ,
                    final TB_Blog blog )
    {
        this.ID = ID;
        this.message = message;
        this.url = url;
        this.urlName = urlName;
        this.urlDescription = urlDescription;
        this.type = type;
        this.slug = slug;
        this.timestamp = timestamp;
        this.tags = tags;
        this.blog = blog;
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

    public String getURLdescription()
    {
        return urlDescription;
    }

    public void setURLdescription( final String urlDescription )
    {
        this.urlDescription = urlDescription;
    }

    public String getType()
    {
        return type;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    public String getSlug()
    {
        return slug;
    }

    public void setSlug( final String slug )
    {
        this.slug = slug;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( final long timestamp )
    {
        this.timestamp = timestamp;
    }

    public Set<String> getTags()
    {
        return tags;
    }

    public void setTags( final Set<String> tags )
    {
        this.tags = tags;
    }

    public TB_Blog getBlog()
    {
        return blog;
    }

    public void setBlog( final TB_Blog blog )
    {
        this.blog = blog;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;

        hash = 67 * hash + (int) ( this.ID ^ ( this.ID >>> 32 ) );
        hash = 67 * hash + Objects.hashCode( this.message );
        hash = 67 * hash + Objects.hashCode( this.url );
        hash = 67 * hash + Objects.hashCode( this.urlName );
        hash = 67 * hash + Objects.hashCode( this.urlDescription );
        hash = 67 * hash + Objects.hashCode( this.type );
        hash = 67 * hash + Objects.hashCode( this.slug );
        hash = 67 * hash + (int) ( this.timestamp ^ ( this.timestamp >>> 32 ) );
        hash = 67 * hash + Objects.hashCode( this.tags );
        hash = 67 * hash + Objects.hashCode( this.blog );

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

        final TB_Post other = (TB_Post) obj;
        if ( this.ID != other.ID )
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

        if ( !Objects.equals( this.urlDescription ,
                              other.urlDescription ) )
        {
            return false;
        }

        if ( !Objects.equals( this.type ,
                              other.type ) )
        {
            return false;
        }

        if ( !Objects.equals( this.slug ,
                              other.slug ) )
        {
            return false;
        }

        if ( this.timestamp != other.timestamp )
        {
            return false;
        }

        if ( !Objects.equals( this.tags ,
                              other.tags ) )
        {
            return false;
        }

        if ( !Objects.equals( this.blog ,
                              other.blog ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "TB_Post{" + "ID=" + ID + ", message=" + message + ", url=" + url + ", urlName=" + urlName + ", urlDescription=" + urlDescription + ", type=" + type + ", slug=" + slug + ", timestamp=" + timestamp + ", tags=" + tags + ", blog=" + blog + '}';
    }

    // PRIVATE
    private long ID;
    private String message;
    private String url;
    private String urlName;
    private String urlDescription;
    private String type;
    private String slug;
    private long timestamp;
    private Set<String> tags;
    private TB_Blog blog;
}
