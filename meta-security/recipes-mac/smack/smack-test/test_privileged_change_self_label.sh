#!/bin/sh

initial_label=`cat /proc/self/attr/current 2>/dev/null`
modified_label="test_label"

echo "$modified_label" >/proc/self/attr/current 2>/dev/null

new_label=`cat /proc/self/attr/current 2>/dev/null`

if [ "$new_label" != "$modified_label" ]; then
	# restore proper label
	echo $initial_label >/proc/self/attr/current
	echo "Privileged process could not change its label"
	exit 1
fi

echo "$initial_label" >/proc/self/attr/current 2>/dev/null
exit 0