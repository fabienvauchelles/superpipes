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

package com.vaushell.spipes.nodes.rss;

import com.vaushell.spipes.transforms.bitly.I_URIshorten;
import com.vaushell.spipes.transforms.date.I_Date;
import com.vaushell.spipes.transforms.done.I_Identifier;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * A RSS news.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class News
    implements I_Identifier , I_Date , I_URIshorten
{
    // PUBLIC
    /**
     * Create a RSS news.
     *
     * @param title RSS' title
     * @param description RSS's description
     * @param uri RSS' URI
     * @param uriSource RSS' source
     * @param author RSS' author
     * @param content RSS' content
     * @param tags RSS' tags set
     * @param date RSS' creation date
     * @return the news
     */
    public static News create( final String title ,
                               final String description ,
                               final URI uri ,
                               final URI uriSource ,
                               final String author ,
                               final String content ,
                               final Set<String> tags ,
                               final Date date )
    {
        if ( title == null || title.isEmpty()
             || uri == null || uri.toString().isEmpty()
             || tags == null )
        {
            throw new IllegalArgumentException();
        }

        // Calculate ID
        final StringBuilder sb = new StringBuilder( title );

        if ( description != null && description.length() > 0 )
        {
            sb.append( description );
        }

        sb.append( uri );

        if ( author != null && author.length() > 0 )
        {
            sb.append( author );
        }

        if ( content != null && content.length() > 0 )
        {
            sb.append( content );
        }

        if ( date != null )
        {
            sb.append( date.toString() );
        }

        final TreeSet<String> correctedTags = new TreeSet<>();
        for ( final String tag : tags )
        {
            final String correctedTag = tag.toLowerCase( Locale.ENGLISH );

            correctedTags.add( correctedTag );
            sb.append( correctedTag );
        }

        final String ID = DigestUtils.md5Hex( sb.toString() );

        return new News( ID ,
                         title ,
                         description ,
                         uri ,
                         uriSource ,
                         author ,
                         content ,
                         correctedTags ,
                         date );
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

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor( final String author )
    {
        this.author = author;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent( final String content )
    {
        this.content = content;
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
    public Date getDate()
    {
        if ( dateInMs == null )
        {
            return null;
        }
        else
        {
            return new Date( dateInMs );
        }
    }

    @Override
    public void setDate( final Date date )
    {
        if ( date == null )
        {
            this.dateInMs = null;
        }
        else
        {
            this.dateInMs = date.getTime();
        }
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

    @Override
    public int hashCode()
    {
        int hash = 5;

        hash = 37 * hash + Objects.hashCode( this.ID );
        hash = 37 * hash + Objects.hashCode( this.title );
        hash = 37 * hash + Objects.hashCode( this.description );
        hash = 37 * hash + Objects.hashCode( this.uri );
        hash = 37 * hash + Objects.hashCode( this.uriSource );
        hash = 37 * hash + Objects.hashCode( this.author );
        hash = 37 * hash + Objects.hashCode( this.content );
        hash = 37 * hash + Objects.hashCode( this.tags );
        hash = 37 * hash + Objects.hashCode( this.dateInMs );

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

        final News other = (News) obj;
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

        if ( !Objects.equals( this.author ,
                              other.author ) )
        {
            return false;
        }

        if ( !Objects.equals( this.content ,
                              other.content ) )
        {
            return false;
        }

        if ( !Objects.equals( this.tags ,
                              other.tags ) )
        {
            return false;
        }

        if ( !Objects.equals( this.dateInMs ,
                              other.dateInMs ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "News{" + "ID=" + ID + ", title=" + title + ", description=" + description + ", uri=" + uri + ", uriSource=" + uriSource + ", author=" + author + ", content=" + content + ", tags=" + tags + ", dateInMs=" + dateInMs + '}';
    }

    // DEFAULT
    News( final String ID ,
          final String title ,
          final String description ,
          final URI uri ,
          final URI uriSource ,
          final String author ,
          final String content ,
          final Set<String> tags ,
          final Date date )
    {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.uri = uri;
        this.uriSource = uriSource;
        this.author = author;
        this.content = content;
        this.tags = tags;

        if ( date != null )
        {
            this.dateInMs = date.getTime();
        }
    }
    // PRIVATE
    private String ID;
    private String title;
    private String description;
    private URI uri;
    private URI uriSource;
    private String author;
    private String content;
    private Set<String> tags;
    private Long dateInMs;
}
