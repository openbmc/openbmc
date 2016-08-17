#!/bin/sh
### BEGIN INIT INFO
# Provides:          vchiq.sh
# Required-Start:    $remote_fs rmnologin
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: Create /dev/vchiq.
# Description:       Get the major number from /proc/devices and use it
#                    ti create /dev/vchiq
### END INIT INFO

rm -f /dev/vchiq

#Get the major number
major=$(awk "\$2==\"vchiq\" {print \$1}" /proc/devices)

if [ -z "$major" ]; then
	echo "Error: Cannot find vchiq in /proc/devices"
	exit 2
else
	mknod /dev/vchiq c "$major" 0
	chmod a+w /dev/vchiq
fi
