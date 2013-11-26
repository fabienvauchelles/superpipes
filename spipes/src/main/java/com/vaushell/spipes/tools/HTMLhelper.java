/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.tools;

import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class HTMLhelper
{
    /**
     * Remove html tags and normalize html chararacters
     *
     * @param s
     * @return
     */
    public static String cleanHTML( String s )
    {
        if ( s == null )
        {
            return null;
        }

        return StringEscapeUtils.unescapeHtml( s.replaceAll( "<[^>]+>" ,
                                                             "" ) );
    }
}
