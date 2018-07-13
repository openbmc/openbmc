#!/bin/sh

### BEGIN INIT INFO
# Provides:		tcsd trousers
# Required-Start:	$local_fs $remote_fs $network
# Required-Stop:	$local_fs $remote_fs $network
# Should-Start:
# Should-Stop:
# Default-Start:	2 3 4 5
# Default-Stop:		0 1 6
# Short-Description:	starts tcsd
# Description:		tcsd belongs to the TrouSerS TCG Software Stack
### END INIT INFO

PATH=/sbin:/bin:/usr/sbin:/usr/bin
DAEMON=/usr/sbin/tcsd
NAME=tcsd
DESC="Trusted Computing daemon"
USER="tss"

test -x "${DAEMON}" || exit 0

# Read configuration variable file if it is present
[ -r /etc/default/$NAME ] && . /etc/default/$NAME

case "${1}" in
	start)
		echo "Starting $DESC: "

		if [ ! -e /dev/tpm* ]
		then
			echo "device driver not loaded, skipping."
			exit 0
		fi

		start-stop-daemon --start --quiet --oknodo \
			--pidfile /var/run/${NAME}.pid --make-pidfile --background \
			--user ${USER} --chuid ${USER} \
			--exec ${DAEMON} -- ${DAEMON_OPTS} --foreground
		RETVAL="$?"
		echo "$NAME."
		exit $RETVAL
		;;

	stop)
		echo "Stopping $DESC: "

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
