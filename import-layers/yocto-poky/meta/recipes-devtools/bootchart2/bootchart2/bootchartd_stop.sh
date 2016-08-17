#!/bin/sh
### BEGIN INIT INFO
# Provides:          bootchartd_stop
# Required-Start:    $remote_fs $all
# Required-Stop: 
# Default-Start:     2 3 4 5
# Default-Stop:
# Short-Description: Stop bootchartd collection
# Description:       This script accompanies bootchartd from bootchart2. 
#                    bootchartd should stop detect the end of the boot process
#                    automatically if a window manager is launched, but for
#                    command-line only operating systems, this script should be
#                    used instead.
### END INIT INFO

/sbin/bootchartd stop

: exit 0
