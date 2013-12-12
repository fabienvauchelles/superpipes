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

package com.vaushell.spipes.nodes.bitly;

import java.net.URI;

/**
 * Represents the source, unmodified URI.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public interface I_URIshorten
    extends I_URI
{
    // PUBLIC
    /**
     * Get the source URI.
     *
     * @return the source URI
     */
    URI getURIsource();

    /**
     * Set the source URI.
     *
     * @param uriSource the source URI
     */
    void setURIsource( URI uriSource );
}
