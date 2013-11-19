/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.posts;

import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class Post
        implements I_Identifier
{
    // PUBLIC
    public Post( String ID ,
                 String message ,
                 String url ,
                 String urlName ,
                 String urlDescription ,
                 Set<String> tags )
    {
        this.ID = ID;
        this.message = message;
        this.url = url;
        this.urlName = urlName;
        this.urlDescription = urlDescription;
        this.tags = tags;
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

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl( String url )
    {
        this.url = url;
    }

    public String getUrlName()
    {
        return urlName;
    }

    public void setUrlName( String urlName )
    {
        this.urlName = urlName;
    }

    public String getUrlDescription()
    {
        return urlDescription;
    }

    public void setUrlDescription( String urlDescription )
    {
        this.urlDescription = urlDescription;
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
    public int hashCode()
    {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode( this.ID );
        hash = 61 * hash + Objects.hashCode( this.message );
        hash = 61 * hash + Objects.hashCode( this.url );
        hash = 61 * hash + Objects.hashCode( this.urlName );
        hash = 61 * hash + Objects.hashCode( this.urlDescription );
        hash = 61 * hash + Objects.hashCode( this.tags );
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
        final Post other = (Post) obj;
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
        if ( !Objects.equals( this.urlDescription ,
                              other.urlDescription ) )
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
        return "Post{" + "ID=" + ID + ", message=" + message + ", url=" + url + ", urlName=" + urlName + ", urlDescription=" + urlDescription + ", tags=" + tags + '}';
    }
    // PRIVATE
    private String ID;
    private String message;
    private String url;
    private String urlName;
    private String urlDescription;
    private Set<String> tags;
}
