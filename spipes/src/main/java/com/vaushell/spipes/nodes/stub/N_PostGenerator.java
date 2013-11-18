/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.stub;

import com.vaushell.spipes.model.posts.PostsFactory;
import com.vaushell.spipes.model.posts.Post;
import com.vaushell.spipes.nodes.A_Node;
import com.vaushell.spipes.tools.ValuesGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class N_PostGenerator
        extends A_Node
{
    // PUBLIC
    public N_PostGenerator()
    {
    }

    @Override
    public void run()
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] start thread " );
        }

        while ( isActive() )
        {
            try
            {
                Post post = PostsFactory.INSTANCE.create( ValuesGenerator.getRandomText( 20 ,
                                                                                         30 ) ,
                                                          ValuesGenerator.getRandomWord( 10 ,
                                                                                         20 ) ,
                                                          ValuesGenerator.getRandomText( 5 ,
                                                                                         8 ) ,
                                                          ValuesGenerator.getRandomText( 15 ,
                                                                                         20 ) ,
                                                          ValuesGenerator.getRandomWordSet( 3 ,
                                                                                            8 ) );

                sendMessage( post );

                try
                {
                    Thread.sleep( Long.parseLong( getValue( "frequency" ) ) );
                }
                catch( InterruptedException ignore )
                {
                }
            }
            catch( Throwable th )
            {
                logger.error( "Error" ,
                              th );
            }
        }

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] stop thread" );
        }
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_PostGenerator.class );
}
