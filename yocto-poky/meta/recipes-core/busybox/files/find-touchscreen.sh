#!/bin/sh

if grep -q "input:.*-e0.*,3,.*a0,1,\|ads7846" /sys/class/$MDEV/device/modalias ; then
	ln -sf /dev/$MDEV /dev/input/touchscreen0
fi

