#!/bin/sh
#
# start/stop rpcbind daemon.

### BEGIN INIT INFO
# Provides:          rpcbind
# Required-Start:    $network
# Required-Stop:     $network
# Default-Start:     S 2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: RPC portmapper replacement
# Description:       rpcbind is a server that converts RPC (Remote
#                    Procedure Call) program numbers into DARPA
#                    protocol port numbers. It must be running in
#                    order to make RPC calls. Services that use
#                    RPC include NFS and NIS.
### END INIT INFO

# Source function library.
. /etc/init.d/functions

test -f /sbin/rpcbind || exit 0

OPTIONS=""
if [ -f /etc/default/rpcbind ]
then
    . /etc/default/rpcbind
elif [ -f /etc/rpcbind.conf ]
then
    . /etc/rpcbind.conf
fi

start ()
{
    echo -n "Starting rpcbind daemon..."
    if pidof /sbin/rpcbind >/dev/null; then
        echo "already running."
        exit 0
    fi
    start-stop-daemon --start --quiet --exec /sbin/rpcbind -- "$@"
    if [ $? -eq 0 ]; then
        echo "done."
    else
        echo "failed."
    fi
}

stop ()
{
    echo "Stopping rpcbind daemon..."
    if ! pidof /sbin/rpcbind >/dev/null; then
        echo "not running."
        return 0
    fi
    start-stop-daemon --stop --quiet --exec /sbin/rpcbind
    if [ $? -eq 0 ]; then
        echo "done."
    else
        echo "failed."
    fi
}

case "$1" in
    start)
        start $OPTIONS
        ;;
    stop)
        stop
        ;;
    force-reload)
        stop
        start $OPTIONS
        ;;
    restart)
        stop
        start $OPTIONS
        ;;
    status)
        status /sbin/rpcbind
        ;;
    *)
        echo "Usage: /etc/init.d/rpcbind {start|stop|force-reload|restart|status}"
        exit 1
        ;;
esac

exit $?
