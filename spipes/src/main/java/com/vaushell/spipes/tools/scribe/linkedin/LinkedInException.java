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

package com.vaushell.spipes.tools.scribe.linkedin;

import com.vaushell.spipes.tools.scribe.OAuthException;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
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
