#!/bin/sh 
### BEGIN INIT INFO
# Provides:             psplash
# Required-Start:
# Required-Stop:
# Default-Start:        S
# Default-Stop:
### END INIT INFO

read CMDLINE < /proc/cmdline
for x in $CMDLINE; do
        case $x in
        psplash=false)
		echo "Boot splashscreen disabled" 
		exit 0;
                ;;
        esac
done

export TMPDIR=/mnt/.psplash
mount tmpfs -t tmpfs $TMPDIR -o,size=40k

rotation=0
if [ -e /etc/rotation ]; then
	read rotation < /etc/rotation
fi

/usr/bin/psplash --angle $rotation &

