/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
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
