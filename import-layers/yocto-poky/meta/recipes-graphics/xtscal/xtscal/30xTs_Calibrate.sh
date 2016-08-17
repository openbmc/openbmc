#!/bin/sh

. /etc/formfactor/config

if [ "$HAVE_TOUCHSCREEN" = "1" ]; then
	n=1
	while [ ! -z $TSLIB_TSDEVICE ] && [ ! -f /etc/pointercal ] && [ $n -le 5 ]
	do
	   /usr/bin/xtscal
	   sleep 1
	   n=$(($n+1))
	done
fi
