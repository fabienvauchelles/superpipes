/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes;

import java.util.Objects;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public abstract class A_Message
{
    // PUBLIC
    public A_Message( String ID )
    {
        this.ID = ID;
    }

    public String getID()
    {
        return ID;
    }

    public void setID( String ID )
    {
        this.ID = ID;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;

        hash = 97 * hash + Objects.hashCode( this.ID );

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

        final A_Message other = (A_Message) obj;
        if ( !Objects.equals( this.ID ,
                              other.ID ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public String toString()
    {
        return "A_Message{" + "ID=" + ID + '}';
    }
    // PRIVATE
    private String ID;
}
