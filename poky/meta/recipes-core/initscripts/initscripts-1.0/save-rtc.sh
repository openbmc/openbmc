#!/bin/sh
### BEGIN INIT INFO
# Provides:          save-rtc
# Required-Start:
# Required-Stop:     $local_fs hwclock
# Default-Start:     S
# Default-Stop:      0 6
# Short-Description: Store system clock into file
# Description:       
### END INIT INFO

TIMESTAMP_FILE=/etc/timestamp

[ -f /etc/default/timestamp ] && . /etc/default/timestamp

# Update the timestamp
date -u +%4Y%2m%2d%2H%2M%2S 2>/dev/null > "$TIMESTAMP_FILE"
