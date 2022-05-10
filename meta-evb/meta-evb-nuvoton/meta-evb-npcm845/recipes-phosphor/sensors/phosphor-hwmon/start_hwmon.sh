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
    if [[ "$path" =~ (.*)/hwmon[0-9]+$ ]];
    then
        path=${BASH_REMATCH[1]}
    fi
fi

# Needed to re-do escaping used to avoid bitbake separator conflicts
path="${path//:/--}"
# Needed to escape prior to being used as a unit argument
path="$(systemd-escape "$path")"
systemctl --no-block "$action" "xyz.openbmc_project.Hwmon@$path.service"
