/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.rss;

import com.vaushell.spipes.nodes.filters.date.I_Date;
import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class News
        implements I_Identifier , I_Date
{
    // PUBLIC
    public News( String ID ,
                 String title ,
                 String description ,
                 String uri ,
                 String author ,
                 String content ,
                 Set<String> tags ,
                 Date date )
    {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.uri = uri;
        this.author = author;
        this.content = content;
        this.tags = tags;
        this.date = date;
    }

    @Override
    public String getID()
    {
        return ID;
    }

    @Override
    public void setID( String ID )
    {
        this.ID = ID;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle( String title )
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri( String uri )
    {
        this.uri = uri;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor( String author )
    {
        this.author = author;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent( String content )
    {
        this.content = content;
    }

    public Set<String> getTags()
    {
        return tags;
    }

    public void setTags(
            Set<String> tags )
    {
        this.tags = tags;
    }

    @Override
    public Date getDate()
    {
        return date;
    }

    @Override
    public void setDate( Date date )
    {
        this.date = date;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode( this.ID );
        hash = 29 * hash + Objects.hashCode( this.title );
        hash = 29 * hash + Objects.hashCode( this.description );
        hash = 29 * hash + Objects.hashCode( this.uri );
        hash = 29 * hash + Objects.hashCode( this.author );
        hash = 29 * hash + Objects.hashCode( this.content );
        hash = 29 * hash + Objects.hashCode( this.tags );
        hash = 29 * hash + Objects.hashCode( this.date );
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
        if ( !Objects.equals( this.date ,
                              other.date ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "News{" + "ID=" + ID + ", title=" + title + ", description=" + description + ", uri=" + uri + ", author=" + author + ", tags=" + tags + ", date=" + date + '}';
    }
    // PRIVATE
    private String ID;
    private String title;
    private String description;
    private String uri;
    private String author;
    private String content;
    private Set<String> tags;
    private Date date;
}
