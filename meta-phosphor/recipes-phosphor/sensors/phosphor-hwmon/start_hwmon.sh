#!/bin/bash

action=$1
devpath=$2
of_fullname=$3

#Use of_fullname if it's there, otherwise use devpath.

path=$of_fullname
if [ -z "$path" ]
then
    path=$devpath

    if [[ "$path" =~ (.*)/hwmon/hwmon[0-9]+$ ]];
    then
        path=${BASH_REMATCH[1]}
    fi
fi

path="${path//-/\\x2d}"
path="${path//:/\\x3a}"

systemctl $action 'xyz.openbmc_project.Hwmon@'$path'.service'
