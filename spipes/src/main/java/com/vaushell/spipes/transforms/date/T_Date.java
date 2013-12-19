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

package com.vaushell.spipes.transforms.date;

import com.vaushell.spipes.Message;
import com.vaushell.spipes.transforms.A_Transform;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

        this.df = new SimpleDateFormat( "dd/MM/yyyy HH:ss" ,
                                        Locale.ENGLISH );
    }

    @Override
    public void prepare()
        throws Exception
    {
        final String minDateStr = getConfig( "date-min" );
        if ( minDateStr != null )
        {
            try
            {
                minDate = df.parse( minDateStr );
            }
            catch( final ParseException ex )
            {
                // Nothing
            }
        }

        final String maxDateStr = getConfig( "date-max" );
        if ( maxDateStr != null )
        {
            try
            {
                maxDate = df.parse( maxDateStr );
            }
            catch( final ParseException ex )
            {
                // Nothing
            }
        }
    }

    @Override
    public Message transform( final Message message )
        throws Exception
    {
        if ( LOGGER.isTraceEnabled() )
        {
            LOGGER.trace( "[" + getNodeID() + "/" + getClass().getSimpleName() + "] transform message : " + message );
        }

        if ( !message.contains( Message.KeyIndex.PUBLISHED_DATE ) )
        {
            return null;
        }

        final Date date = new Date( (Long) message.getProperty( Message.KeyIndex.PUBLISHED_DATE ) );
        if ( minDate != null && minDate.after( date ) )
        {
            return null;
        }

        if ( maxDate != null && maxDate.before( date ) )
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
    private Date minDate;
    private Date maxDate;
    private final SimpleDateFormat df;
}
