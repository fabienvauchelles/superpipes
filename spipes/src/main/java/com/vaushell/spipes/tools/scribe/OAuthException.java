/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools.scribe;

/**
 *
 * @author
 */
public class OAuthException
        extends Exception
{
    // PRIVATE
    public OAuthException( int httpCode ,
                             int apiCode ,
                             String message )
    {
        super( message );

        this.apiCode = apiCode;
        this.httpCode = httpCode;
    }

    public int getApiCode()
    {
        return apiCode;
    }

    public void setApiCode( int apiCode )
    {
        this.apiCode = apiCode;
    }

    public int getHttpCode()
    {
        return httpCode;
    }

    public void setHttpCode( int httpCode )
    {
        this.httpCode = httpCode;
    }

    @Override
    public String toString()
    {
        return "A_OAuthException{" + super.toString() + ", apiCode=" + apiCode + ", httpCode=" + httpCode + '}';
    }
    // PRIVATE
    private int apiCode;
    private int httpCode;
}
