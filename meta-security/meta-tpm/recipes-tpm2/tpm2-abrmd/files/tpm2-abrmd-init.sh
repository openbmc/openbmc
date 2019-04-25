#!/bin/sh

### BEGIN INIT INFO
# Provides:		tpm2-abrmd
# Required-Start:	$local_fs $remote_fs $network
# Required-Stop:	$local_fs $remote_fs $network
# Should-Start:
# Should-Stop:
# Default-Start:	2 3 4 5
# Default-Stop:		0 1 6
# Short-Description:	starts tpm2-abrmd
# Description:		tpm2-abrmd implements the TCG resource manager
### END INIT INFO

PATH=/sbin:/bin:/usr/sbin:/usr/bin
DAEMON=/usr/sbin/tpm2-abrmd
NAME=tpm2-abrmd
DESC="TCG TSS2 Access Broker and Resource Management daemon"
USER="tss"

test -x "${DAEMON}" || exit 0

# Read configuration variable file if it is present
[ -r /etc/default/$NAME ] && . /etc/default/$NAME

case "${1}" in
	start)
		echo -n "Starting $DESC: "

		if [ ! -e /dev/tpm* ]
		then
			echo "device driver not loaded, skipping."
			exit 0
		fi

		start-stop-daemon --start --quiet --oknodo --background --pidfile /var/run/${NAME}.pid --user ${USER} --chuid ${USER} --exec ${DAEMON} -- ${DAEMON_OPTS}
		RETVAL="$?"
		echo "$NAME."
		[ "$RETVAL" = 0 ] && pidof $DAEMON > /var/run/${NAME}.pid
		exit $RETVAL
		;;

	stop)
		echo -n "Stopping $DESC: "

		start-stop-daemon --stop --quiet --oknodo --pidfile /var/run/${NAME}.pid --user ${USER} --exec ${DAEMON}
		RETVAL="$?"
                echo  "$NAME."
		rm -f /var/run/${NAME}.pid
		exit $RETVAL
		;;

	restart|force-reload)
		"${0}" stop
		sleep 1
		"${0}" start
		exit $?
		;;
	*)
		echo "Usage: ${NAME} {start|stop|restart|force-reload|status}" >&2
		exit 3
		;;
esac

exit 0
