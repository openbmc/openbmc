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
# Given a 'buildstats' path (created by bitbake when setting
# USER_CLASSES ?= "buildstats" on local.conf) and task names, outputs
# '<task> <recipe> <elapsed time>' for all recipes. Elapsed times are in
# seconds, and task should be given without the 'do_' prefix.
#
# Some useful pipelines
#
# 1. Tasks with largest elapsed times
# $ buildstats.sh -b <buildstats> | sort -k3 -n -r | head
#
# 2. Min, max, sum per task (in needs GNU datamash)
# $ buildstats.sh -b <buildstats> | datamash -t' ' -g1 min 3 max 3 sum 3 | sort -k4 -n -r
#
# AUTHORS
# Leonardo Sandoval <leonardo.sandoval.gonzalez@linux.intel.com>
#
BS_DIR="tmp/buildstats"
TASKS="compile:configure:fetch:install:patch:populate_lic:populate_sysroot:unpack"

function usage {
CMD=$(basename $0)
cat <<EOM
Usage: $CMD [-b buildstats_dir] [-t do_task]
  -b buildstats The path where the folder resides
                (default: "$BS_DIR")
  -t tasks      The tasks to be computed
                (default: "$TASKS")
  -h            Display this help message
EOM
}

# Parse and validate arguments
while getopts "b:t:h" OPT; do
	case $OPT in
	b)
		BS_DIR="$OPTARG"
		;;
	t)
		TASKS="$OPTARG"
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

RECIPE_FIELD=1
TIME_FIELD=4

tasks=(${TASKS//:/ })
for task in "${tasks[@]}"; do
    task="do_${task}"
    for file in $(find ${BS_DIR} -type f -name ${task}); do
        recipe=$(sed -n -e "/$task/p" ${file} | cut -d ':' -f${RECIPE_FIELD})
        time=$(sed -n -e "/$task/p" ${file} | cut -d ':' -f${TIME_FIELD} | cut -d ' ' -f2)
        echo "${task} ${recipe} ${time}"
    done
done
