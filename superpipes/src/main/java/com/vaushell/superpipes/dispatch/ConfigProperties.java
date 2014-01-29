/*
 * Copyright (C) 2014 Fabien Vauchelles (fabien_AT_vauchelles_DOT_com).
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

package com.vaushell.superpipes.dispatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormatter;

/**
 * Config properties.
 *
 * @author Fabien Vauchelles (fabien_AT_vauchelles_DOT_com)
 */
public class ConfigProperties
{
    // PUBLIC
    public static final List<ConfigProperties> EMPTY_COMMONS = Collections.emptyList();

    public ConfigProperties()
    {
        this.properties = new Properties();
        this.commons = new ArrayList<>();
    }

    /**
     * Add common configuration.
     *
     * @param commons Common configuration.
     */
    public void addCommons( final List<ConfigProperties> commons )
    {
        this.commons.addAll( commons );
    }

    /**
     * Tests if the specified object is a key in this hashtable.
     *
     * @param key possible key
     * @return if and only if the specified object is a key in this hashtable, as determined by the equals method; false
     * otherwise.
     */
    public boolean containsKey( final String key )
    {
        return properties.containsKey( key );
    }

    /**
     * Tests if this hashtable maps no keys to values.
     *
     * @return true if this hashtable maps no keys to values; false otherwise.
     */
    public boolean isEmpty()
    {
        return properties.isEmpty();
    }

    /**
     * Read configurations properties. Must inside tags 'params' with 'param'.
     *
     * @param cNode properties
     */
    public void readProperties( final HierarchicalConfiguration cNode )
    {
        properties.clear();

        if ( cNode != null )
        {
            final List<HierarchicalConfiguration> hConfs = cNode.configurationsAt( "params.param" );
            if ( hConfs != null )
            {
                for ( final HierarchicalConfiguration hConf : hConfs )
                {
                    final String name = hConf.getString( "[@name]" );
                    final String value = hConf.getString( "[@value]" );

                    if ( name != null && name.length() > 0 && value != null && value.length() > 0 )
                    {
                        setProperty( name ,
                                     value );
                    }
                }
            }
        }
    }

    /**
     * Calls the Hashtable method put. Enforces use of strings for property keys and values. The value returned is the result of
     * the Hashtable call to put.
     *
     * @param key the key to be placed into this property list.
     * @param value the value corresponding to key.
     */
    public void setProperty( final String key ,
                             final String value )
    {
        properties.setProperty( key ,
                                value );
    }

    /**
     * Get config. throws IllegalArgumentException if the key is not a long or doesn't exist.
     *
     * @param key Key of parameter.
     * @return the value.
     */
    public Long getConfigLong( final String key )
    {
        final String value = getConfig( key ,
                                        false );

        try
        {
            return Long.valueOf( value );
        }
        catch( final NumberFormatException ex )
        {
            throw new IllegalArgumentException( "Property '" + key + "' must be a long" ,
                                                ex );
        }
    }

    /**
     * Get config.
     *
     * @param key Key of parameter.
     * @param defaultValue Default value if the key doesn't exist.
     * @return the value.
     */
    public Long getConfigLong( final String key ,
                               final Long defaultValue )
    {
        final String value = getConfig( key ,
                                        true );

        if ( value == null )
        {
            return defaultValue;
        }
        else
        {
            try
            {
                return Long.valueOf( value );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "Property '" + key + "' must be a long" ,
                                                    ex );
            }
        }
    }

    /**
     * Get config. throws IllegalArgumentException if the key is not a long or doesn't exist.
     *
     * @param key Key of parameter.
     * @return the value.
     */
    public Duration getConfigDuration( final String key )
    {
        final String value = getConfig( key ,
                                        false );

        try
        {
            final long duration = Long.parseLong( value );
            if ( duration < 0L )
            {
                throw new IllegalArgumentException( "Property '" + key + "' can't be <=0. Should be null or empty." );
            }

            return new Duration( duration );
        }
        catch( final NumberFormatException ex )
        {
            throw new IllegalArgumentException( "Property '" + key + "' must be a long" ,
                                                ex );
        }
    }

    /**
     * Get config.
     *
     * @param key Key of parameter.
     * @param defaultValue Default value if the key doesn't exist.
     * @return the value.
     */
    public Duration getConfigDuration( final String key ,
                                       final Duration defaultValue )
    {
        final String value = getConfig( key ,
                                        true );

        if ( value == null )
        {
            return defaultValue;
        }
        else
        {
            try
            {
                final long duration = Long.parseLong( value );
                if ( duration < 0L )
                {
                    throw new IllegalArgumentException( "Property '" + key + "' can't be <=0. Should be null or empty." );
                }

                return new Duration( duration );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "Property '" + key + "' must be a long" ,
                                                    ex );
            }
        }
    }

    /**
     * Get config. throws IllegalArgumentException if the key is not an integer or doesn't exist.
     *
     * @param key Key of parameter.
     * @return the value.
     */
    public Integer getConfigInteger( final String key )
    {
        final String value = getConfig( key ,
                                        false );

        try
        {
            return Integer.valueOf( value );
        }
        catch( final NumberFormatException ex )
        {
            throw new IllegalArgumentException( "Property '" + key + "' must be an integer" ,
                                                ex );
        }
    }

    /**
     * Get config.
     *
     * @param key Key of parameter.
     * @param defaultValue Default value if the key doesn't exist.
     * @return the value.
     */
    public Integer getConfigInteger( final String key ,
                                     final Integer defaultValue )
    {
        final String value = getConfig( key ,
                                        true );

        if ( value == null )
        {
            return defaultValue;
        }
        else
        {
            try
            {
                return Integer.valueOf( value );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "Property '" + key + "' must be an integer" ,
                                                    ex );
            }
        }
    }

    /**
     * Get config. throws IllegalArgumentException if the key is not a double or doesn't exist.
     *
     * @param key Key of parameter.
     * @return the value.
     */
    public Double getConfigDouble( final String key )
    {
        final String value = getConfig( key ,
                                        false );

        try
        {
            return Double.valueOf( value );
        }
        catch( final NumberFormatException ex )
        {
            throw new IllegalArgumentException( "Property '" + key + "' must be an integer" ,
                                                ex );
        }
    }

    /**
     * Get config.
     *
     * @param key Key of parameter.
     * @param defaultValue Default value if the key doesn't exist.
     * @return the value.
     */
    public Double getConfigDouble( final String key ,
                                   final Double defaultValue )
    {
        final String value = getConfig( key ,
                                        true );

        if ( value == null )
        {
            return defaultValue;
        }
        else
        {
            try
            {
                return Double.valueOf( value );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "Property '" + key + "' must be an integer" ,
                                                    ex );
            }
        }
    }

    /**
     * Get config. throws IllegalArgumentException if the key is not a boolean or doesn't exist.
     *
     * @param key Key of parameter.
     * @return the value.
     */
    public Boolean getConfigBoolean( final String key )
    {
        final String value = getConfig( key ,
                                        false );

        try
        {
            return Boolean.valueOf( value );
        }
        catch( final NumberFormatException ex )
        {
            throw new IllegalArgumentException( "Property '" + key + "' must be a boolean" ,
                                                ex );
        }
    }

    /**
     * Get config.
     *
     * @param key Key of parameter.
     * @param defaultValue Default value if the key doesn't exist.
     * @return the value.
     */
    public Boolean getConfigBoolean( final String key ,
                                     final Boolean defaultValue )
    {
        final String value = getConfig( key ,
                                        true );

        if ( value == null )
        {
            return defaultValue;
        }
        else
        {
            try
            {
                return Boolean.valueOf( value );
            }
            catch( final NumberFormatException ex )
            {
                throw new IllegalArgumentException( "Property '" + key + "' must be a boolean" ,
                                                    ex );
            }
        }
    }

    /**
     * Get config. throws IllegalArgumentException if the key is not a datetime or doesn't exist.
     *
     * @param key Key of parameter.
     * @param fmt Format of the date.
     * @return the value.
     */
    public DateTime getConfigDateTime( final String key ,
                                       final DateTimeFormatter fmt )
    {
        final String value = getConfig( key ,
                                        false );

        return fmt.parseDateTime( value );
    }

    /**
     * Get config.
     *
     * @param key Key of parameter.
     * @param fmt Format of the date.
     * @param defaultValue Default value if the key doesn't exist.
     * @return the value.
     */
    public DateTime getConfigDateTime( final String key ,
                                       final DateTimeFormatter fmt ,
                                       final DateTime defaultValue )
    {
        final String value = getConfig( key ,
                                        true );

        if ( value == null )
        {
            return defaultValue;
        }
        else
        {
            return fmt.parseDateTime( value );
        }
    }

    /**
     * Get config. throws IllegalArgumentException if the key is not a string or doesn't exist.
     *
     * @param key Key of parameter.
     * @return the value.
     */
    public String getConfigString( final String key )
    {
        return getConfig( key ,
                          false );
    }

    /**
     * Get config.
     *
     * @param key Key of parameter.
     * @param defaultValue Default value if the key doesn't exist.
     * @return the value.
     */
    public String getConfigString( final String key ,
                                   final String defaultValue )
    {
        final String value = getConfig( key ,
                                        true );

        if ( value == null )
        {
            return defaultValue;
        }
        else
        {
            return value;
        }
    }

    // PRIVATE
    private final Properties properties;
    private final List<ConfigProperties> commons;

    /**
     * Retrieve node's parameter.
     *
     * @param key Key of parameter
     * @param acceptNull if false, the null value throw an IllegalArgumentException.
     * @return the value
     */
    private String getConfig( final String key ,
                              final boolean acceptNull )
    {
        if ( key == null || key.isEmpty() )
        {
            throw new IllegalArgumentException();
        }

        String value = properties.getProperty( key );
        if ( value != null && !value.isEmpty() )
        {
            return value;
        }

        for ( final ConfigProperties common : commons )
        {
            value = common.properties.getProperty( key );
            if ( value != null && !value.isEmpty() )
            {
                return value;
            }
        }

        if ( acceptNull )
        {
            return null;
        }

        throw new IllegalArgumentException( "Can't find property '" + key + "'" );
    }
}
