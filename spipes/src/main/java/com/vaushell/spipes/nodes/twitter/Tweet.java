/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.twitter;

import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.util.Objects;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class Tweet
        implements I_Identifier
{
    // PUBLIC
    public Tweet( String message )
    {
        this.ID = Long.MIN_VALUE;
        this.message = message;
    }

    @Override
    public String getID()
    {
        return Long.toString( ID );
    }

    public long getTweetID()
    {
        return ID;
    }

    @Override
    public void setID( String ID )
    {
        this.ID = Long.parseLong( ID );
    }

    public void setTweetID( long ID )
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
    public int hashCode()
    {
        int hash = 7;
        hash = 83 * hash + (int) ( this.ID ^ ( this.ID >>> 32 ) );
        hash = 83 * hash + Objects.hashCode( this.message );
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
        final Tweet other = (Tweet) obj;
        if ( this.ID != other.ID )
        {
            return false;
        }
        if ( !Objects.equals( this.message ,
                              other.message ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Tweet{" + "ID=" + ID + ", message=" + message + '}';
    }
    // PRIVATE
    private long ID;
    private String message;
}
