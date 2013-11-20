/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.nodes.bitly.I_URI;
import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.net.URI;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class FB_Post
        implements I_Identifier , I_URI
{
    // PUBLIC
    public FB_Post( String message ,
                    URI uri ,
                    String urlName ,
                    String urlDescription ,
                    Set<String> tags )
    {
        this.ID = null;
        this.message = message;
        this.uri = uri;
        this.uriName = urlName;
        this.uriDescription = urlDescription;
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

    public String getURIname()
    {
        return uriName;
    }

    public void setURIname( String uriName )
    {
        this.uriName = uriName;
    }

    public String getURIdescription()
    {
        return uriDescription;
    }

    public void setURIdescription( String uriDescription )
    {
        this.uriDescription = uriDescription;
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
        hash = 61 * hash + Objects.hashCode( this.uri );
        hash = 61 * hash + Objects.hashCode( this.uriName );
        hash = 61 * hash + Objects.hashCode( this.uriDescription );
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
        final FB_Post other = (FB_Post) obj;
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
        return "FB_Post{" + "ID=" + ID + ", message=" + message + ", uri=" + uri + ", uriName=" + uriName + ", uriDescription=" + uriDescription + ", tags=" + tags + '}';
    }
    // PRIVATE
    private String ID;
    private String message;
    private URI uri;
    private String uriName;
    private String uriDescription;
    private Set<String> tags;
}
