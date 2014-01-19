/*
 * Copyright (C) 2013 Fabien Vauchelles (fabien_AT_vauchelles_DOT_com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3, 29 June 2007, of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package com.vaushell.spipes.tools.scribe.tumblr;

import com.vaushell.spipes.tools.scribe.OAuthException;
import java.util.List;

/**
 * Tumblr Exception.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public final class TumblrException
    extends OAuthException
{
    // PUBLIC
    public TumblrException( final int httpCode ,
                            final int apiCode ,
                            final String message ,
                            final List<String> errors )
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

    /**
     * Add an error.
     *
     * @param error the Error
     */
    public void addError( final String error )
    {
        errors.add( error );
    }

    @Override
    public String getMessage()
    {
        return super.getMessage() + " / errors=" + errors;
    }

    @Override
    public String toString()
    {
        return "TumblrException{" + super.toString() + ", errors=" + errors + '}';
    }
    // PRIVATE
    private final List<String> errors;
}
