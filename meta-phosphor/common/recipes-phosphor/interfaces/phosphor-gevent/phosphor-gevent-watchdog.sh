#!/bin/bash

ENTROPY_THRESHOLD=100
if [ "$#" -gt 0 ]; then
    re='^[0-9]+$'
    if ! [[ $1 =~ $re ]] ; then
        echo "error: $1 Not a number" >&2; exit 1
    fi
    ENTROPY_THRESHOLD=$1
fi
available_entropy=`cat /proc/sys/kernel/random/entropy_avail`

systemctl is-active phosphor-gevent.service
if [ $? -ne 0 ]; then
    if [ "$available_entropy" -gt "$ENTROPY_THRESHOLD" ]; then
        echo "available_entropy greater than $ENTROPY_THRESHOLD."
        echo "Starting phosphor-gevent.service."
        systemctl start phosphor-gevent.service
    fi
fi
