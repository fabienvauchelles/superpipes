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

package com.vaushell.superpipes.transforms.date;

import com.vaushell.superpipes.dispatch.Message;
import com.vaushell.superpipes.transforms.A_Transform;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exclude message which has no published-date or the published date is not in a range.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class T_Date
    extends A_Transform
{
    // PUBLIC
    public T_Date()
    {
        super();

        this.df = DateTimeFormat.forPattern( "dd/MM/yyyy HH:mm:ss" );
    }

    @Override
    public void prepare()
        throws Exception
    {
        final String minDateStr = getConfig( "date-min" ,
                                             true );
        if ( minDateStr != null )
        {
            minCal = df.parseDateTime( minDateStr );
        }

        final String maxDateStr = getConfig( "date-max" ,
                                             true );
        if ( maxDateStr != null )
        {
            maxCal = df.parseDateTime( maxDateStr );
        }
    }

    @Override
    public Message transform( final Message message )
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNode().getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + Message.
                formatSimple( message ) );
        }

        if ( !message.contains( Message.KeyIndex.PUBLISHED_DATE ) )
        {
            return null;
        }

        final DateTime cal = (DateTime) message.getProperty( Message.KeyIndex.PUBLISHED_DATE );
        if ( minCal != null && minCal.isAfter( cal ) )
        {
            return null;
        }

        if ( maxCal != null && maxCal.isBefore( cal ) )
        {
            return null;
        }

        return message;
    }

    @Override
    public void terminate()
        throws Exception
    {
        // Nothing
    }

    // PRIVATE
    private static final Logger LOGGER = LoggerFactory.getLogger( T_Date.class );
    private DateTime minCal;
    private DateTime maxCal;
    private final DateTimeFormatter df;
}
