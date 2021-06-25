#!/bin/sh
#
# oe-time-dd-test records how much time it takes to 
# write <count> number of kilobytes to the filesystem.
# It also records the number of processes that are in
# running (R), uninterruptible sleep (D) and interruptible
# sleep (S) state from the output of "top" command.
# The purporse of this script is to find which part of
# the build system puts stress on the filesystem io and
# log all the processes.

usage() {
        echo "Usage: $0 <count>"
}

TIMEOUT=15

if [ $# -ne 1 ]; then
        usage
        exit 1
fi

uptime
timeout ${TIMEOUT} dd if=/dev/zero of=oe-time-dd-test.dat bs=1024 count=$1 conv=fsync
if [ $? -ne 0 ]; then
	echo "Timeout used: ${TIMEOUT}"
	echo "start: top output"
	top -c -b -n1 -w 512
	echo "end: top output"
	echo "start: iostat"
	iostat -y -z -x 5 1
	echo "end: iostat"
	echo "start: cooker log"
	tail -30 tmp*/log/cooker/*/console-latest.log
	echo "end: cooker log"
fi
