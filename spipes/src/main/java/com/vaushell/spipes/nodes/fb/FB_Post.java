/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.fb;

import com.vaushell.spipes.nodes.bitly.I_URIshorten;
import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.net.URI;
import java.util.Objects;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class FB_Post
        implements I_Identifier , I_URIshorten
{
    // PUBLIC
    public FB_Post( String message ,
                    URI uri ,
                    URI uriSource ,
                    String uriName ,
                    String uriCaption ,
                    String uriDescription )
    {
        this.ID = null;
        this.message = message;
        this.uri = uri;
        this.uriSource = uriSource;
        this.uriName = uriName;
        this.uriCaption = uriCaption;
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

    public String getURIcaption()
    {
        return uriCaption;
    }

    public void setURIcaption( String uriCaption )
    {
        this.uriCaption = uriCaption;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode( this.ID );
        hash = 97 * hash + Objects.hashCode( this.message );
        hash = 97 * hash + Objects.hashCode( this.uri );
        hash = 97 * hash + Objects.hashCode( this.uriSource );
        hash = 97 * hash + Objects.hashCode( this.uriName );
        hash = 97 * hash + Objects.hashCode( this.uriCaption );
        hash = 97 * hash + Objects.hashCode( this.uriDescription );
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
        if ( !Objects.equals( this.uriSource ,
                              other.uriSource ) )
        {
            return false;
        }
        if ( !Objects.equals( this.uriName ,
                              other.uriName ) )
        {
            return false;
        }
        if ( !Objects.equals( this.uriCaption ,
                              other.uriCaption ) )
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
        return "FB_Post{" + "ID=" + ID + ", message=" + message + ", uri=" + uri + ", uriSource=" + uriSource + ", uriName=" + uriName + ", uriCaption=" + uriCaption + ", uriDescription=" + uriDescription + '}';
    }
    // PRIVATE
    private String ID;
    private String message;
    private URI uri;
    private URI uriSource;
    private String uriName;
    private String uriCaption;
    private String uriDescription;
}
