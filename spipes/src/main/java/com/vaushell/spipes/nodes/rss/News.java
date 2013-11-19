/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.rss;

import com.vaushell.spipes.nodes.A_Message;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class News
        extends A_Message
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
        super( ID );

        this.title = title;
        this.description = description;
        this.uri = uri;
        this.author = author;
        this.content = content;
        this.tags = tags;
        this.date = date;
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

    public Date getDate()
    {
        return date;
    }

    public void setDate( Date date )
    {
        this.date = date;
    }

    @Override
    public int hashCode()
    {
        int hash = super.hashCode();

        hash = 41 * hash + Objects.hashCode( this.title );
        hash = 41 * hash + Objects.hashCode( this.description );
        hash = 41 * hash + Objects.hashCode( this.uri );
        hash = 41 * hash + Objects.hashCode( this.author );
        hash = 41 * hash + Objects.hashCode( this.content );
        hash = 41 * hash + Objects.hashCode( this.tags );
        hash = 41 * hash + Objects.hashCode( this.date );

        return hash;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( !super.equals( obj ) )
        {
            return false;
        }

        final News other = (News) obj;

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
        return "News{" + super.toString() + ", title=" + title + ", description=" + description + ", uri=" + uri + ", author=" + author + ", content=" + content + ", tags=" + tags + ", date=" + date + '}';
    }
    // PRIVATE
    private String title;
    private String description;
    private String uri;
    private String author;
    private String content;
    private Set<String> tags;
    private Date date;
}
