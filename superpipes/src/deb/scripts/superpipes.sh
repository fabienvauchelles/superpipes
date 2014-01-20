#!/bin/sh

# Copyright (C) 2013 Fabien Vauchelles (fabien_AT_vauchelles_DOT_com).
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3, 29 June 2007, of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
# MA 02110-1301  USA

NAME="superpipes"
DESC="$NAME service"

# The path to Jsvc
EXEC=`which jsvc`

# The path to the folder containing the jar
FILE_PATH="/usr/local/$NAME"

# Our classpath including our jar file and the Apache Commons Daemon library
CLASS_PATH="$FILE_PATH/lib/*.jar:$FILE_PATH/$NAME.jar"

# The fully qualified name of the class to execute
CLASS="com.vaushell.superpipes.DaemonApp"

# The file that will contain our process identification number (pid) for other scripts/programs that need to access it.
PID="$HOME/.$NAME/$NAME.pid"

# System.out writes to this file...
LOG_OUT="$HOME/.$NAME/$NAME.out"

# System.err writes to this file...
LOG_ERR="$HOME/.$NAME/$NAME.err"

jsvc_exec()
{
    $EXEC -cp "$CLASS_PATH" -outfile "$LOG_OUT" -errfile "$LOG_ERR" -pidfile "$PID" $1 "$CLASS" "$2" "$3"
}

if [ ! -d "$HOME/.$NAME" ]; then
	mkdir "$HOME/.$NAME"
fi

case "$1" in
    start)
        echo "Starting the $DESC..."

        # Start the service
        jsvc_exec "" "$2" "$3"

        echo "The $DESC has started."
    ;;
    stop)
        echo "Stopping the $DESC..."

        # Stop the service
        jsvc_exec "-stop" "$2" "$3"

        echo "The $DESC has stopped."
    ;;
    restart)
        if [ -f "$PID" ]; then

            echo "Restarting the $DESC..."

            # Stop the service
            jsvc_exec "-stop" "$2" "$3"

            # Start the service
            jsvc_exec "$2" "$3"

            echo "The $DESC has restarted."
        else
            echo "Daemon not running, no action taken"
            exit 1
        fi
            ;;
    *)
    echo "Usage: $FILE_PATH/$NAME {start|stop|restart} <configuration.xml> <datas directory>" >&2
    exit 3
    ;;
esac

