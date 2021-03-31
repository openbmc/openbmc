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

if [ $# -ne 1 ]; then
        usage
        exit 1
fi

uptime
/usr/bin/time -f "%e" dd if=/dev/zero of=foo bs=1024 count=$1 conv=fsync
top -b -n 1 | grep -v "0      0      0" | grep -E ' [RSD] ' | cut -c 46-47 | sort | uniq -c
