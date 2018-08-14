#! /bin/sh
### BEGIN INIT INFO
# Provides:          lldpd
# Required-Start:    $remote_fs $network $syslog
# Required-Stop:     $network $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: LLDP daemon
# Description:       lldpd is a 802.1AB implementation, a L2 network
#                    discovery protocol. It also supports CDP, EDP and
#                    various other protocols.
### END INIT INFO

# Do NOT "set -e"

log_daemon_msg() {
    echo $*
}

log_end_msg() {
    if [ $1 -eq 0 ]; then
        success $*
    else
        failure $*
    fi
}

log_failure_msg() {
    echo $*
}

log_success_msg() {
    echo $*
}


# PATH should only include /usr/* if it runs after the mountnfs.sh script
PATH=/sbin:/usr/sbin:/bin:/usr/bin
DESC="LLDP daemon"
NAME=lldpd
DAEMON=/usr/sbin/$NAME
DAEMON_ARGS=""
PIDFILE=/var/run/$NAME.pid
SCRIPTNAME=/etc/init.d/$NAME
CHROOT=/var/run/$NAME

# Exit if the package is not installed
[ -x "$DAEMON" ] || exit 0

# Read configuration variable file if it is present
[ -r /etc/default/$NAME ] && . /etc/default/$NAME

# LSB log_* functions
. /etc/init.d/functions

do_start()
{
	start-stop-daemon --start --quiet --pidfile $PIDFILE --exec $DAEMON -- \
		$DAEMON_ARGS \
		|| return 2
}

do_stop()
{
	start-stop-daemon --stop --quiet --retry=TERM/30/KILL/5 --pidfile $PIDFILE --name $NAME
	RETVAL="$?"
	[ "$RETVAL" = 2 ] && return 2
	start-stop-daemon --stop --quiet --oknodo --retry=0/30/KILL/5 --exec $DAEMON
	[ "$?" = 2 ] && return 2
	rm -f $PIDFILE
	return "$RETVAL"
}

do_reload() {
	start-stop-daemon --stop --signal 1 --quiet --pidfile $PIDFILE --name $NAME
	return 0
}

case "$1" in
  start)
	[ "$VERBOSE" != no ] && log_daemon_msg "Starting $DESC" "$NAME"
	do_start
	case "$?" in
		0|1) [ "$VERBOSE" != no ] && log_end_msg 0 ;;
		2) [ "$VERBOSE" != no ] && log_end_msg 1 ;;
	esac
	;;
  stop)
	[ "$VERBOSE" != no ] && log_daemon_msg "Stopping $DESC" "$NAME"
	do_stop
	case "$?" in
		0|1) [ "$VERBOSE" != no ] && log_end_msg 0 ;;
		2) [ "$VERBOSE" != no ] && log_end_msg 1 ;;
	esac
	;;
  reload)
	log_daemon_msg "Reloading $DESC" "$NAME"
	do_reload
	log_end_msg $?
	;;
  restart|force-reload)
	log_daemon_msg "Restarting $DESC" "$NAME"
	do_stop
	case "$?" in
	  0|1)
		do_start
		case "$?" in
			0) log_end_msg 0 ;;
			1) log_end_msg 1 ;; # Old process is still running
			*) log_end_msg 1 ;; # Failed to start
		esac
		;;
	  *)
	  	# Failed to stop
		log_end_msg 1
		;;
	esac
	;;
  status)
	status_of_proc $DAEMON $NAME -p $PIDFILE && exit 0 || exit $?
	;;
  *)
	echo "Usage: $SCRIPTNAME {start|stop|restart|reload|force-reload|status}" >&2
	exit 3
	;;
esac

:
