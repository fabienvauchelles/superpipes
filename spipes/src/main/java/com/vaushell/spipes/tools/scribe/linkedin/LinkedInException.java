/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools.scribe.linkedin;

import com.vaushell.spipes.tools.scribe.OAuthException;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class LinkedInException
        extends OAuthException
{
    // PUBLIC
    public LinkedInException( int httpCode ,
                              int apiCode ,
                              String message ,
                              int status )
    {
        super( httpCode ,
               apiCode ,
               message );

        this.status = status;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus( int status )
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "FacebookException{" + super.toString() + ", status=" + status + '}';
    }
    // PRIVATE
    private int status;
}
