#!/bin/sh
HOST_NAME=`uname -n`
if [ ! -e /var/lib/tripwire/${HOST_NAME}.twd ] ; then
	echo "****    WARNING: Tripwire database for ${HOST_NAME} not found.    ****"
	echo "**** Run "/etc/tripwire/twinstall.sh" and/or "tripwire --init". ****"
	# Note: /etc/tripwire/twinstall.sh creates and initializes tripwire
	# database (i.e tripwire --init).
	# Example: . /etc/tripwire/twinstall.sh 2> /dev/null
fi
