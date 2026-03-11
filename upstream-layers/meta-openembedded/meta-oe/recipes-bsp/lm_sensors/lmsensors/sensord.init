#! /bin/sh
### BEGIN INIT INFO
# Provides:          sensord
# Required-Start:    $local_fs
# Should-Start:
# Required-Stop:     $local_fs
# Should-Stop:
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: sensord initscript
# Description:       Starts the sensord logging daemon
### END INIT INFO

PATH=/sbin:/usr/sbin:/bin:/usr/bin

DESC="sensors logging daemon"
NAME="sensord"
SENSORD=`which $NAME`

. /etc/init.d/functions || exit 1
. /etc/sensord.conf || exit 1

# Exit if the package is not installed
[ -x "$SENSORD" ] || exit 0

case "$1" in
    start)
        echo -n "Starting $DESC: $NAME... "
        start-stop-daemon -S -x $SENSORD -- $SENSORD_ARGS
        echo "done."
        ;;
    stop)
        echo -n "Stopping $DESC: $NAME... "
        start-stop-daemon -K -x $SENSORD
        echo "done."
        ;;
    restart)
        echo "Restarting $DESC: $NAME... "
        $0 stop
        $0 start
        echo "done."
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac

exit 0
