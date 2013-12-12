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

package com.vaushell.spipes.nodes.filters.date;

import com.vaushell.spipes.nodes.A_Node;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class NF_Date
    extends A_Node
{
    // PUBLIC
    public NF_Date()
    {
        this.minDate = null;
        this.maxDate = null;

        this.df = new SimpleDateFormat( "dd/MM/yyyy HH:ss" );
    }

    @Override
    public void prepare()
        throws IOException
    {
        String minDateStr = getConfig( "date-min" );
        if ( minDateStr != null )
        {
            try
            {
                minDate = df.parse( minDateStr );
            }
            catch( ParseException ignore )
            {
            }
        }

        String maxDateStr = getConfig( "date-max" );
        if ( maxDateStr != null )
        {
            try
            {
                maxDate = df.parse( maxDateStr );
            }
            catch( ParseException ignore )
            {
            }
        }
    }

    @Override
    public void terminate()
    {
    }

    // PROTECTED
    @Override
    protected void loop()
        throws InterruptedException
    {
        I_Date message = (I_Date) getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] filter message : " + message );
        }

        if ( message.getDate() == null )
        {
            return;
        }

        if ( minDate != null && minDate.after( message.getDate() ) )
        {
            return;
        }

        if ( maxDate != null && maxDate.before( message.getDate() ) )
        {
            return;
        }

        sendMessage( message );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( NF_Date.class );
    private Date minDate;
    private Date maxDate;
    private SimpleDateFormat df;
}
