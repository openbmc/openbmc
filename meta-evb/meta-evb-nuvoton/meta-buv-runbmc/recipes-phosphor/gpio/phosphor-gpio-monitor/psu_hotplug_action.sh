#!/bin/bash

echo "reload sensor config $1"

# 0: Invenotry, 1: Entity
if [ $1 = "1" ]; then
    # stop service
    systemctl stop xyz.openbmc_project.psusensor.service
    sleep 0.5s

    # start service
    systemctl start xyz.openbmc_project.psusensor.service
    sleep 0.5s
fi
