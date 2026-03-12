#!/bin/sh
#
### BEGIN INIT INFO
# Provides:          redis-server
# Required-Start:    $network
# Required-Stop:     $network
# Default-Start:     S 2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Redis, a key-value store
# Description:       Redis is an open source, advanced key-value store.
#                    http://redis.io
### END INIT INFO

test -f /usr/bin/redis-server || exit 0

ARGS="/etc/redis/redis.conf"

case "$1" in
    start)
	echo "Starting redis-server..."
        start-stop-daemon --start --quiet --exec /usr/bin/redis-server -- $ARGS
	;;
    stop)
        echo "Stopping redis-server..."
        start-stop-daemon --stop --quiet --exec /usr/bin/redis-server
	;;
    restart)
        echo "Stopping redis-server..."
        start-stop-daemon --stop --quiet --exec /usr/bin/redis-server

        # Since busybox implementation ignores --retry arguments repeatedly check
        # if the process is still running and try another signal after a timeout,
        # efectively simulating a stop with --retry=TERM/5/KILL/5 schedule.
        waitAfterTerm=5000000 # us / 5000 ms / 5 s
        waitAfterKill=5000000 # us / 5000 ms / 5 s
        waitStep=100000 # us / 100 ms / 0.1 s
        waited=0
        start-stop-daemon --stop --test --quiet --exec /usr/bin/redis-server
        processOff=$?
        while [ $processOff -eq 0 ] && [ $waited -le $waitAfterTerm ] ; do
            usleep ${waitStep}
            ((waited+=${waitStep}))
            start-stop-daemon --stop --test --quiet --exec /usr/bin/redis-server
            processOff=$?
        done
        if [ $processOff -eq 0 ] ; then
            start-stop-daemon --stop --signal KILL --exec /usr/bin/redis-server
            start-stop-daemon --stop --test --quiet --exec /usr/bin/redis-server
            processOff=$?
        fi
        waited=0
        while [ $processOff -eq 0 ] && [ $waited -le $waitAfterKill ] ; do
            usleep ${waitStep}
            ((waited+=${waitStep}))
            start-stop-daemon --stop --test --quiet --exec /usr/bin/redis-server
            processOff=$?
        done
        # Here $processOff will indicate if waiting and retrying according to
        # the schedule ended in a successfull stop or not.

	echo "Starting redis-server..."
        start-stop-daemon --start --quiet --exec /usr/bin/redis-server -- $ARGS
	;;
    *)
	echo "Usage: /etc/init.d/redis-server {start|stop|restart}"
	exit 1
	;;
esac

exit 0

