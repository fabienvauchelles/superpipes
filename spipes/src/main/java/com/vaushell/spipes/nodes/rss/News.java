/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.rss;

import com.vaushell.spipes.nodes.bitly.I_URIshorten;
import com.vaushell.spipes.nodes.filters.date.I_Date;
import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.net.URI;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class News
        implements I_Identifier , I_Date , I_URIshorten
{
    // PUBLIC
    public static News create( String title ,
                               String description ,
                               URI uri ,
                               URI uriSource ,
                               String author ,
                               String content ,
                               Set<String> tags ,
                               Date date )
    {
        if ( title == null || title.length() <= 0 || uri == null || uri.toString().length() <= 0 )
        {
            throw new NullPointerException( "Title or URL can not be null" );
        }

        if ( tags == null )
        {
            throw new NullPointerException();
        }

        // Calculate ID
        StringBuilder sb = new StringBuilder();
        sb.append( title );

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

        TreeSet<String> correctedTags = new TreeSet<>();
        for ( String tag : tags )
        {
            String correctedTag = tag.toLowerCase();

            correctedTags.add( correctedTag );
            sb.append( correctedTag );
        }

        String ID = DigestUtils.md5Hex( sb.toString() );

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

    @Override
    public URI getURI()
    {
        return uri;
    }

    @Override
    public void setURI( URI uri )
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
    public URI getURIsource()
    {
        return uriSource;
    }

    @Override
    public void setURIsource( URI uriSource )
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
        hash = 37 * hash + Objects.hashCode( this.date );
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
        return "News{" + "ID=" + ID + ", title=" + title + ", description=" + description + ", uri=" + uri + ", uriSource=" + uriSource + ", author=" + author + ", content=" + content + ", tags=" + tags + ", date=" + date + '}';
    }

    // DEFAULT
    News( String ID ,
          String title ,
          String description ,
          URI uri ,
          URI uriSource ,
          String author ,
          String content ,
          Set<String> tags ,
          Date date )
    {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.uri = uri;
        this.uriSource = uriSource;
        this.author = author;
        this.content = content;
        this.tags = tags;
        this.date = date;
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
    private Date date;
}
