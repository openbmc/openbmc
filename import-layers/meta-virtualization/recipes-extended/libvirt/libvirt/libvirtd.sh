#!/bin/sh
### BEGIN INIT INFO
# Provides: libvirtd
# Required-Start: $local_fs $network dbus
# Required-Stop: $local_fs $network dbus
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 6
### END INIT INFO

if [ -f /lib/lsb/init-functions ]
then
    . /lib/lsb/init-functions
else
    # int log_begin_message (char *message)
    log_begin_msg () {
        if [ -z "$1" ]; then
	    return 1
        fi
        echo " * $@"
    }

    # int log_end_message (int exitstatus)
    log_end_msg () {
	
    # If no arguments were passed, return
	[ -z "$1" ] && return 1
	
    # Only do the fancy stuff if we have an appropriate terminal
    # and if /usr is already mounted
	TPUT=/usr/bin/tput
	EXPR=/usr/bin/expr
	if [ -x $TPUT ] && [ -x $EXPR ] && $TPUT hpa 60 >/dev/null 2>&1; then
	    COLS=`$TPUT cols`
	    if [ -n "$COLS" ]; then
		COL=`$EXPR $COLS - 7`
	    else
		COL=73
	    fi
	    UP=`$TPUT cuu1`
	    END=`$TPUT hpa $COL`
	    START=`$TPUT hpa 0`
	    RED=`$TPUT setaf 1`
	    NORMAL=`$TPUT op`
	    if [ $1 -eq 0 ]; then
		echo "$UP$END[ ok ]"
	    else
		echo -e "$UP$START $RED*$NORMAL$END[${RED}fail${NORMAL}]"
	    fi
	else
	    if [ $1 -eq 0 ]; then
		echo "   ...done."
	    else
		echo "   ...fail!"
	    fi
	fi
	return $1
    }
    
    log_warning_msg () {
	if log_use_fancy_output; then
	    YELLOW=`$TPUT setaf 3`
	    NORMAL=`$TPUT op`
	    echo "$YELLOW*$NORMAL $@"
	else
	    echo "$@"
	fi
    }

fi

case "$1" in
        start)
                if [ -e /var/run/libvirtd.pid ]; then
                        if [ -d /proc/$(cat /var/run/libvirtd.pid) ]; then
                                echo "virtualization library already started; not starting."
                        else
                                echo "Removing stale PID file /var/run/libvirtd.pid."
                                rm -f /var/run/libvirtd.pid
                        fi
                fi
                log_begin_msg "Starting virtualization library daemon: libvirtd"
                if [ ! -e /var/run/libvirtd.pid ]; then
                    start-stop-daemon -K -x /usr/bin/dnsmasq --pidfile /var/run/libvirt/network/default.pid
                fi
		start-stop-daemon --start --quiet --pidfile /var/run/libvirtd.pid --exec /usr/sbin/libvirtd -- --daemon --listen
                log_end_msg $?
                ;;
        stop)
                log_begin_msg "Stopping virtualization library daemon: libvirtd"
		start-stop-daemon --stop --quiet --retry 3 --exec /usr/sbin/libvirtd --pidfile /var/run/libvirtd.pid
                log_end_msg $?
                rm -f /var/run/libvirtd.pid
                ;;
        restart)
                $0 stop
                sleep 1
                $0 start
                ;;
        *)
                echo "Usage: $0 {start|stop|restart}"
                exit 1
                ;;
esac
