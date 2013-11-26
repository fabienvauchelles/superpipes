/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools.scribe.tumblr;

import com.vaushell.spipes.tools.scribe.OAuthException;
import java.util.List;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class TumblrException
        extends OAuthException
{
    // PRIVATE
    public TumblrException( int httpCode ,
                            int apiCode ,
                            String message ,
                            List<String> errors )
    {
        super( httpCode ,
               apiCode ,
               message );

        this.errors = errors;
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public void addError( String error )
    {
        errors.add( error );
    }

    @Override
    public String getMessage()
    {
        return super.getMessage() + " / errors=" + errors;
    }
    // PRIVATE
    private List<String> errors;
}
