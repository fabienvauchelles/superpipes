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

    // PROTECTED
    @Override
    protected void prepare()
            throws Exception
    {
    }

    @Override
    protected void loop()
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] generate post" );
        }

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
    }

    @Override
    protected void terminate()
            throws Exception
    {
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( N_PostGenerator.class );
}
