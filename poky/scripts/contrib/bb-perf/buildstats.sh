#!/bin/bash
#
# Copyright (c) 2011, Intel Corporation.
# All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
#
# DESCRIPTION
# Given 'buildstats' data (generate by bitbake when setting
# USER_CLASSES ?= "buildstats" on local.conf), task names and a stats values
# (these are the ones preset on the buildstats files), outputs
# '<task> <recipe> <value_1> <value_2> ... <value_n>'. The units are the ones
# defined at buildstats, which in turn takes data from /proc/[pid] files
#
# Some useful pipelines
#
# 1. Tasks with largest stime (Amount of time that this process has been scheduled
#    in kernel mode) values
# $ buildstats.sh -b <buildstats> -s stime | sort -k3 -n -r | head
#
# 2. Min, max, sum utime (Amount  of  time  that  this process has been scheduled
#    in user mode) per task (in needs GNU datamash)
# $ buildstats.sh -b <buildstats> -s utime | datamash -t' ' -g1 min 3 max 3 sum 3 | sort -k4 -n -r
#
# AUTHORS
# Leonardo Sandoval <leonardo.sandoval.gonzalez@linux.intel.com>
#

# Stats, by type
TIME="utime:stime:cutime:cstime"
IO="IO wchar:IO write_bytes:IO syscr:IO read_bytes:IO rchar:IO syscw:IO cancelled_write_bytes"
RUSAGE="rusage ru_utime:rusage ru_stime:rusage ru_maxrss:rusage ru_minflt:rusage ru_majflt:\
rusage ru_inblock:rusage ru_oublock:rusage ru_nvcsw:rusage ru_nivcsw"

CHILD_RUSAGE="Child rusage ru_utime:Child rusage ru_stime:Child rusage ru_maxrss:Child rusage ru_minflt:\
Child rusage ru_majflt:Child rusage ru_inblock:Child rusage ru_oublock:Child rusage ru_nvcsw:\
Child rusage ru_nivcsw"

BS_DIR="tmp/buildstats"
TASKS="compile:configure:fetch:install:patch:populate_lic:populate_sysroot:unpack"
STATS="$TIME"
HEADER="" # No header by default

function usage {
CMD=$(basename $0)
cat <<EOM
Usage: $CMD [-b buildstats_dir] [-t do_task]
  -b buildstats The path where the folder resides
                (default: "$BS_DIR")
  -t tasks      The tasks to be computed
                (default: "$TASKS")
  -s stats      The stats to be matched. Options: TIME, IO, RUSAGE, CHILD_RUSAGE
                or any other defined buildstat separated by colons, i.e. stime:utime
                (default: "$STATS")
                Default stat sets:
                    TIME=$TIME
                    IO=$IO
                    RUSAGE=$RUSAGE
                    CHILD_RUSAGE=$CHILD_RUSAGE
  -h            Display this help message
EOM
}

# Parse and validate arguments
while getopts "b:t:s:Hh" OPT; do
	case $OPT in
	b)
		BS_DIR="$OPTARG"
		;;
	t)
		TASKS="$OPTARG"
		;;
	s)
		STATS="$OPTARG"
		;;
	H)
	        HEADER="y"
	        ;;
	h)
		usage
		exit 0
		;;
	*)
		usage
		exit 1
		;;
	esac
done

# Ensure the buildstats folder exists
if [ ! -d "$BS_DIR" ]; then
	echo "ERROR: $BS_DIR does not exist"
	usage
	exit 1
fi

stats=""
IFS=":"
for stat in ${STATS}; do
	case $stat in
	    TIME)
		stats="${stats}:${TIME}"
		;;
	    IO)
		stats="${stats}:${IO}"
		;;
	    RUSAGE)
		stats="${stats}:${RUSAGE}"
		;;
	    CHILD_RUSAGE)
		stats="${stats}:${CHILD_RUSAGE}"
		;;
	    *)
		stats="${STATS}"
	esac
done

# remove possible colon at the beginning
stats="$(echo "$stats" | sed -e 's/^://1')"

# Provide a header if required by the user
[ -n "$HEADER" ] && { echo "task:recipe:$stats"; }

for task in ${TASKS}; do
    task="do_${task}"
    for file in $(find ${BS_DIR} -type f -name ${task} | awk 'BEGIN{ ORS=""; OFS=":" } { print $0,"" }'); do
        recipe="$(basename $(dirname $file))"
	times=""
	for stat in ${stats}; do
	    [ -z "$stat" ] && { echo "empty stats"; }
	    time=$(sed -n -e "s/^\($stat\): \\(.*\\)/\\2/p" $file)
	    # in case the stat is not present, set the value as NA
	    [ -z "$time" ] && { time="NA"; }
	    # Append it to times
	    if [ -z "$times" ]; then
		times="${time}"
	    else
		times="${times} ${time}"
	    fi
	done
        echo "${task} ${recipe} ${times}"
    done
done
