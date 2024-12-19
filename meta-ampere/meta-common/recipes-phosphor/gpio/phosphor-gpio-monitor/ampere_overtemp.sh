#!/bin/bash

# Assert/Deassert system fault led
ampere_add_redfishevent.sh OpenBMC.0.1.CPUThermalTrip "$1"
busctl set-property xyz.openbmc_project.LED.GroupManager /xyz/openbmc_project/led/groups/overtemp_fault xyz.openbmc_project.Led.Group Asserted b true
obmcutil chassisoff
sleep 10
busctl set-property xyz.openbmc_project.LED.GroupManager /xyz/openbmc_project/led/groups/overtemp_fault xyz.openbmc_project.Led.Group Asserted b false
