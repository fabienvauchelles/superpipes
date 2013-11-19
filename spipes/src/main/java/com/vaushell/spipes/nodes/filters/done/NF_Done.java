/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.spipes.nodes.filters.done;

import com.vaushell.spipes.nodes.A_Node;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fabien Vauchelles (fabien AT vauchelles DOT com)
 */
public class NF_Done
        extends A_Node
{
    // PUBLIC
    public NF_Done()
    {
        this.ids = new HashSet<>();
        this.path = null;
    }

    // PROTECTED
    @Override
    protected void prepare()
            throws IOException
    {
        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] load already done ids" );
        }

        // Config
        path = Paths.get( getMainConfig( "datas-directory" ) ,
                          getNodeID() ,
                          "done.dat" );

        Files.createDirectories( path.getParent() );

        // Load previous ID
        if ( Files.exists( path ) )
        {
            try( BufferedReader bfr = Files.newBufferedReader( path ,
                                                               Charset.forName( "utf-8" ) ) )
            {
                String line;
                while ( ( line = bfr.readLine() ) != null )
                {
                    ids.add( line );
                }
            }
        }
    }

    @Override
    protected void loop()
            throws IOException , InterruptedException
    {
        I_Identifier message = (I_Identifier) getLastMessageOrWait();

        if ( logger.isTraceEnabled() )
        {
            logger.trace( "[" + getNodeID() + "] filter message : " + message );
        }

        if ( !ids.contains( message.getID() ) )
        {
            // Transfer message
            sendMessage( message );

            // Save message ID. Won't be replay
            ids.add( message.getID() );

            try( BufferedWriter bfw = Files.newBufferedWriter( path ,
                                                               Charset.forName( "utf-8" ) ,
                                                               StandardOpenOption.APPEND ,
                                                               StandardOpenOption.CREATE ) )
            {
                bfw.write( message.getID() );
                bfw.newLine();
            }
        }
    }

    @Override
    protected void terminate()
    {
    }
    // PRIVATE
    private final static Logger logger = LoggerFactory.getLogger( NF_Done.class );
    private HashSet<String> ids;
    private Path path;
}
