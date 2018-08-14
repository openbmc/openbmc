#!/bin/sh
### BEGIN INIT INFO
# Provides: banner
# Required-Start:
# Required-Stop:
# Default-Start:     S
# Default-Stop:
### END INIT INFO

if [ ! -e /dev/tty ]; then
    /bin/mknod -m 0666 /dev/tty c 5 0
fi

if ( > /dev/tty0 ) 2>/dev/null; then
    vtmaster=/dev/tty0
elif ( > /dev/vc/0 ) 2>/dev/null; then
    vtmaster=/dev/vc/0
elif ( > /dev/console ) 2>/dev/null; then
    vtmaster=/dev/console
else
    vtmaster=/dev/null
fi
echo > $vtmaster
echo "Please wait: booting..." > $vtmaster
