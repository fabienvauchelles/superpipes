/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.bitly;

import java.net.URI;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public interface I_URIshorten
        extends I_URI
{
    // PUBLIC
    public URI getURIsource();

    public void setURIsource( URI uriSource );
}
