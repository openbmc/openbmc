#!/bin/sh
# we need to restart FRU service and PSU Monitor everything else shall be ok
systemctl stop xyz.openbmc_project.psusensor.service
systemctl stop xyz.openbmc_project.GxpFruDevice.service

systemctl start xyz.openbmc_project.GxpFruDevice.service
systemctl start xyz.openbmc_project.psusensor.service
