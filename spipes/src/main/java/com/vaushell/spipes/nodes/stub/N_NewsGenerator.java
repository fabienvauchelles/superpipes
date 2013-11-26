/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.stub;

import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.nodes.rss.News;
import com.vaushell.spipes.nodes.rss.NewsFactory;
import com.vaushell.spipes.tools.ValuesGenerator;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_NewsGenerator
        extends A_Node
{
    // PUBLIC
    public N_NewsGenerator()
    {
    }

    @Override
    public void prepare()
            throws Exception
    {
    }

    @Override
    public void terminate()
            throws Exception
    {
    }

    // PROTECTED
    @Override
    protected void loop()
            throws URISyntaxException
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] generate post" );
        }

        News news = NewsFactory.INSTANCE.create( ValuesGenerator.getRandomText( 10 ,
                                                                                20 ) ,
                                                 ValuesGenerator.getRandomText( 20 ,
                                                                                30 ) ,
                                                 new URI( "http://" + ValuesGenerator.getRandomWord( 10 ,
                                                                                                     20 ) ) ,
                                                 ValuesGenerator.getRandomText( 1 ,
                                                                                2 ) ,
                                                 ValuesGenerator.getRandomText( 100 ,
                                                                                200 ) ,
                                                 ValuesGenerator.getRandomWordSet( 3 ,
                                                                                   8 ) ,
                                                 new Date() );

        sendMessage( news );
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_NewsGenerator.class );
}
