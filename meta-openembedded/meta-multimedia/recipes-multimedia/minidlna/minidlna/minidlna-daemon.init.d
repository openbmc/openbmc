#!/bin/sh

NAME="minidlna"
DAEMON=/usr/sbin/minidlnad
SCRIPTNAME=/etc/init.d/$NAME
PIDFILE=/var/run/$NAME.pid
CONF=/etc/$NAME.conf
ARGS="-f $CONF"

# Exit if the package is not installed
[ -x "$DAEMON" ] || exit 0

start_function() {

    export PATH=$PWD:$PATH

    if [ -f ${PIDFILE} ]; then
        echo "$SCRIPTNAME already running with PID #`cat $PIDFILE` ( according to ${PIDFILE} )";
        exit 0
    fi
    
    $DAEMON $ARGS
 
    pid=$!

    if [ "$pid" != "" ]; then
        echo -n "$pid" > ${PIDFILE}
    fi
}

stop_function() {

    export PATH=$PWD:$PATH

    if [ ! -e "${PIDFILE}" ]; then
        echo "${SCRIPTNAME} not running ( according to ${PIDFILE} )";
        exit 1;
    fi
    PID=`cat ${PIDFILE}`
    kill -INT ${PID}
    rm -f ${PIDFILE}
}

case $1 in
    "start")
        start_function
        ;;
    "stop")
        stop_function
        ;;
    *)
    echo "Usage: $0 {start | stop}"

esac
