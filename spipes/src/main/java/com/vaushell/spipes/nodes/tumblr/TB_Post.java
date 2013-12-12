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

package com.vaushell.spipes.nodes.tumblr;

import com.vaushell.spipes.nodes.bitly.I_URIshorten;
import com.vaushell.spipes.nodes.filters.done.I_Identifier;
import java.net.URI;
import java.util.Set;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class TB_Post
    implements I_Identifier , I_URIshorten
{
    // PUBLIC
    public TB_Post( String message ,
                    URI uri ,
                    URI uriSource ,
                    String uriName ,
                    String uriDescription ,
                    Set<String> tags )
    {
        this.ID = Long.MIN_VALUE;
        this.message = message;
        this.uri = uri;
        this.uriSource = uriSource;
        this.uriName = uriName;
        this.uriDescription = uriDescription;
        this.tags = tags;
    }

    @Override
    public String getID()
    {
        return Long.toString( ID );
    }

    @Override
    public void setID( String ID )
    {
        this.ID = Long.parseLong( ID );
    }

    public long getTumblrID()
    {
        return ID;
    }

    public void setTumblrID( long ID )
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

    public Set<String> getTags()
    {
        return tags;
    }

    public void setTags(
        Set<String> tags )
    {
        this.tags = tags;
    }
    // PRIVATE
    private long ID;
    private String message;
    private URI uri;
    private URI uriSource;
    private String uriName;
    private String uriDescription;
    private Set<String> tags;
}
