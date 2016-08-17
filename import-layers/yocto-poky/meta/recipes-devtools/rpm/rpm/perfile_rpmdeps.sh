#!/bin/bash

: ${RPMDEPS:=rpmdeps}

process() {
	while read file_name ; do
		printf "%s\t" ${file_name}
		if [ ! -d $file_name ]; then
			printf "%s " $($RPMDEPS $1 $file_name | sed -e 's,rpmlib(.*,,' -e 's,\([<>\=]\+ \+[^ ]*\),(\1),g')
		fi
		printf "\n"
	done
}

usage() {
	echo "$0 {-P|--provides} {-R|--requires} FILE ..."
}

while [ $# -gt 0 ]; do
   case "$1" in
	--rpmdeps)
		RPMDEPS=$2
		shift
		shift
		;;
	-R|--requires)
		process_type=--requires
		shift
		;;
	-P|--provides)
		process_type=--provides
		shift
		;;
	*)
		break;
		;;
   esac
done

if [ -z "$process_type" ]; then
	usage
	exit 1
fi

if [ $# -gt 0 ]; then
	find "$@" | process $process_type
	exit $?
fi

process $process_type
