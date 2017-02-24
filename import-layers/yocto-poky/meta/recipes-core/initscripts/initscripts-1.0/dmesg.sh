#!/bin/sh
### BEGIN INIT INFO
# Provides:             dmesg
# Required-Start:
# Required-Stop:
# Default-Start:        S
# Default-Stop:
### END INIT INFO

if [ -f /var/log/dmesg ]; then
	if LOGPATH=$(which logrotate); then
		$LOGPATH -f /etc/logrotate-dmesg.conf
	else
		mv -f /var/log/dmesg /var/log/dmesg.old
	fi
fi
dmesg -s 131072 > /var/log/dmesg
