/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.posts;

import com.vaushell.spipes.nodes.A_Message;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class Post
        extends A_Message
{
    // PUBLIC
    public Post( String ID ,
                 String message ,
                 String url ,
                 String urlName ,
                 String urlDescription ,
                 Set<String> tags )
    {
        super( ID );

        this.message = message;
        this.url = url;
        this.urlName = urlName;
        this.urlDescription = urlDescription;
        this.tags = tags;
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
        int hash = super.hashCode();

        hash = 41 * hash + Objects.hashCode( this.message );
        hash = 41 * hash + Objects.hashCode( this.url );
        hash = 41 * hash + Objects.hashCode( this.urlName );
        hash = 41 * hash + Objects.hashCode( this.urlDescription );
        hash = 41 * hash + Objects.hashCode( this.tags );

        return hash;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( !super.equals( obj ) )
        {
            return false;
        }

        final Post other = (Post) obj;
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
        return "Post{" + super.toString() + ", message=" + message + ", url=" + url + ", urlName=" + urlName + ", urlDescription=" + urlDescription + ", tags=" + tags + '}';
    }
    // PRIVATE
    private String message;
    private String url;
    private String urlName;
    private String urlDescription;
    private Set<String> tags;
}
