/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools.scribe.fb;

import com.vaushell.spipes.tools.scribe.OAuthException;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class FacebookException
        extends OAuthException
{
    // PRIVATE
    public FacebookException( int httpCode ,
                              int apiCode ,
                              String message ,
                              String type )
    {
        super( httpCode ,
               apiCode ,
               message );

        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return "FacebookException{" + super.toString() + ", type=" + type + '}';
    }
    // PRIVATE
    private String type;
}
