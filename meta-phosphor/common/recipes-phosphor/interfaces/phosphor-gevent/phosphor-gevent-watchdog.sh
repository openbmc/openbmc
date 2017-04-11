#!/bin/bash

ENTROPY_THRESHOLD=100
available_entropy=`cat /proc/sys/kernel/random/entropy_avail`

if [ "`systemctl is-active phosphor-gevent.service`" != "active" ]; then
    if [ "$available_entropy" -gt "$ENTROPY_THRESHOLD" ]; then
        echo "available_entropy greater than $ENTROPY_THRESHOLD. Starting phosphor-gevent.service"
        systemctl start phosphor-gevent.service
    fi
else
    if [ "$available_entropy" -lt "$ENTROPY_THRESHOLD" ]; then
        echo "available_entropy less than $ENTROPY_THRESHOLD. Stopping phosphor-gevent.service"
        systemctl stop phosphor-gevent.service
    fi
fi
