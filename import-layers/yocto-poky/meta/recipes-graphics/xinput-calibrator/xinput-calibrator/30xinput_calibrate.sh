#!/bin/sh

. /etc/formfactor/config

if [ "$HAVE_TOUCHSCREEN" = "1" ]; then
	/usr/bin/xinput_calibrator_once.sh
fi
