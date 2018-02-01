#!/usr/bin/env bash
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
#
# Produces script data to be consumed by gnuplot. There are two possible plots
# depending if either the -S parameter is present or not:
#
#     * without -S: Produces a histogram listing top N recipes/tasks versus
#       stats. The first stat defined in the -s parameter is the one taken
#       into account for ranking
#     * -S: Produces a histogram listing tasks versus stats.  In this case,
#       the value of each stat is the sum for that particular stat in all recipes found.
#       Stats values  are in descending order defined by the first stat defined on -s
#
# EXAMPLES
#
# 1. Top recipes' tasks taking into account utime
#
#     $ buildstats-plot.sh -s utime | gnuplot -p
#
# 2. Tasks versus utime:stime
#
#     $ buildstats-plot.sh -s utime:stime -S | gnuplot -p
#
# 3. Tasks versus IO write_bytes:IO read_bytes
#
#     $ buildstats-plot.sh -s 'IO write_bytes:IO read_bytes' -S | gnuplot -p
#
# AUTHORS
# Leonardo Sandoval <leonardo.sandoval.gonzalez@linux.intel.com>
#

set -o nounset
set -o errexit

BS_DIR="tmp/buildstats"
N=10
STATS="utime"
SUM=""
OUTDATA_FILE="$PWD/buildstats-plot.out"

function usage {
    CMD=$(basename $0)
    cat <<EOM
Usage: $CMD [-b buildstats_dir] [-t do_task]
  -b buildstats The path where the folder resides
                (default: "$BS_DIR")
  -n N          Top N recipes to display. Ignored if -S is present
                (default: "$N")
  -s stats      The stats to be matched. If more that one stat, units
                should be the same because data is plot as histogram.
                (see buildstats.sh -h for all options) or any other defined
                (build)stat separated by colons, i.e. stime:utime
                (default: "$STATS")
  -S            Sum values for a particular stat for found recipes
  -o            Output data file.
                (default: "$OUTDATA_FILE")
  -h            Display this help message
EOM
}

# Parse and validate arguments
while getopts "b:n:s:o:Sh" OPT; do
	case $OPT in
	b)
		BS_DIR="$OPTARG"
		;;
	n)
		N="$OPTARG"
		;;
	s)
	        STATS="$OPTARG"
	        ;;
	S)
	        SUM="y"
	        ;;
	o)
	        OUTDATA_FILE="$OPTARG"
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

# Get number of stats
IFS=':'; statsarray=(${STATS}); unset IFS
nstats=${#statsarray[@]}

# Get script folder, use to run buildstats.sh
CD=$(dirname $0)

# Parse buildstats recipes to produce a single table
OUTBUILDSTATS="$PWD/buildstats.log"
$CD/buildstats.sh -H -s "$STATS" -H > $OUTBUILDSTATS

# Get headers
HEADERS=$(cat $OUTBUILDSTATS | sed -n -e '1s/ /-/g' -e '1s/:/ /gp')

echo -e "set boxwidth 0.9 relative"
echo -e "set style data histograms"
echo -e "set style fill solid 1.0 border lt -1"
echo -e "set xtics rotate by 45 right"

# Get output data
if [ -z "$SUM" ]; then
    cat $OUTBUILDSTATS | sed -e '1d' | sort -k3 -n -r | head -$N > $OUTDATA_FILE
    # include task at recipe column
    sed -i -e "1i\
${HEADERS}" $OUTDATA_FILE
    echo -e "set title \"Top task/recipes\""
    echo -e "plot for [COL=3:`expr 3 + ${nstats} - 1`] '${OUTDATA_FILE}' using COL:xtic(stringcolumn(1).' '.stringcolumn(2)) title columnheader(COL)"
else

    # Construct datatamash sum argument (sum 3 sum 4 ...)
    declare -a sumargs
    j=0
    for i in `seq $nstats`; do
	sumargs[j]=sum; j=$(( $j + 1 ))
	sumargs[j]=`expr 3 + $i - 1`;  j=$(( $j + 1 ))
    done

    # Do the processing with datamash
    cat $OUTBUILDSTATS | sed -e '1d' | datamash -t ' ' -g1 ${sumargs[*]} | sort -k2 -n -r > $OUTDATA_FILE

    # Include headers into resulted file, so we can include gnuplot xtics
    HEADERS=$(echo $HEADERS | sed -e 's/recipe//1')
    sed -i -e "1i\
${HEADERS}" $OUTDATA_FILE

    # Plot
    echo -e "set title \"Sum stats values per task for all recipes\""
    echo -e "plot for [COL=2:`expr 2 + ${nstats} - 1`] '${OUTDATA_FILE}' using COL:xtic(1) title columnheader(COL)"
fi

