/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.linkedin;

import com.vaushell.spipes.nodes.bitly.I_URI;
import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.net.URI;
import java.util.Objects;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class LNK_Post
        implements I_Identifier , I_URI
{
    // PUBLIC
    public LNK_Post( String message ,
                     URI uri ,
                     String uriName ,
                     String uriDescription )
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

    public String getUriName()
    {
        return uriName;
    }

    public void setUriName( String uriName )
    {
        this.uriName = uriName;
    }

    public String getUriDescription()
    {
        return uriDescription;
    }

    public void setUriDescription( String uriDescription )
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
