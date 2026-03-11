#!/bin/bash
#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
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
	echo "$0 is used to detect i/o latency and runs commands to display host information."
	echo "The following commands are run in order:"
	echo "1) top -c -b -n1 -w 512"
	echo "2) iostat -y -z -x 5 1"
	echo "3) tail -30 tmp*/log/cooker/*/console-latest.log to gather cooker log."
	echo " "
	echo "Options:"
	echo "-c | --count <amount>		dd (transfer) <amount> KiB of data within specified timeout to detect latency."
	echo "				Must enable -t option."
	echo "-t | --timeout <time>		timeout in seconds for the <count> amount of data to be transferred."
	echo "-l | --log-only			run the commands without performing the data transfer."
	echo "-h | --help			show help"

}

run_cmds() {
    echo "start: top output"
	top -c -b -n1 -w 512
	echo "end: top output"
	echo "start: iostat"
	iostat -y -z -x 5 1
	echo "end: iostat"
	echo "start: cooker log"
	tail -30 tmp*/log/cooker/*/console-latest.log
	echo "end: cooker log"
}

if [ $# -lt 1 ]; then
	usage
	exit 1
fi

re_c='^[0-9]+$'
#re_t='^[0-9]+([.][0-9]+)?$'

while [[ $# -gt 0 ]]; do
	key="$1"

	case $key in
		-c|--count)
			COUNT=$2
			shift
			shift
			if ! [[ $COUNT =~ $re_c ]] || [[ $COUNT -le 0 ]] ; then
				usage
				exit 1
			fi
			;;
		-t|--timeout)
			TIMEOUT=$2
			shift
			shift
			if ! [[ $TIMEOUT =~ $re_c ]] || [[ $TIMEOUT -le 0 ]] ; then
				usage
				exit 1
			fi
			;;
		-l|--log-only)
			LOG_ONLY="true"
			shift
			shift
			;;
		-h|--help)
			usage
			exit 0
			;;
		*)
			usage
			exit 1
			;;
	esac
done


if [ "$LOG_ONLY" = "true" ] ; then
    uptime
    run_cmds
    exit
fi

if [ -z ${TIMEOUT+x} ] || [ -z ${COUNT+x} ] ; then
    usage
    exit 1
fi

uptime
echo "Timeout used: ${TIMEOUT}"
timeout ${TIMEOUT} dd if=/dev/zero of=oe-time-dd-test.dat bs=1024 count=${COUNT} conv=fsync
if [ $? -ne 0 ]; then
    run_cmds
fi
