#!/bin/bash

# input string should be like: CPU_true_90 or MEMORY_false_50
if [ -z "$1" ]; then
  echo "invalid information for utilization"
  exit 1
fi


# get utilization type
util="${1%%_*}"
un=255 # 0xff invalid event data
if [ "$util" = "CPU" ]; then
  #0xA6
  un=166
elif [ "$util" = "Memory" ]; then
  #0xA7
  un=167
fi
params="${1#*_}"

# get assert/de-assert
assert_m=assert
assert="${params%%_*}"
if [ "$assert" != "true" ]; then
  assert_m=deassert
fi
params="${params#*_}"

# get usage
usage="${params%%_*}"

# get ipmid memory usage
ipmid_mem=0 # do not set value when set CPU health SEL
if [ "$util" = "Memory" ]; then
  data=`top -n 1 |grep ipmid$` # get mem info like: 432 1 root S 16816 4% 0 0% ipmid
  data="${data%%%*}" # remove info after mem usage(right of %): 432 1 root S 16816 4
  ipmid_mem="${data##* }" # remove data left of space
fi

# IpmiSelAdd format ssaybq
# - message
# - object path
# - array length, fixed 3
# - array data (event data 1~3)
# - assert (as true, de-assert as false)
# - generator ID

# write SEL
busctl call `mapper get-service /xyz/openbmc_project/Logging/IPMI` /xyz/openbmc_project/Logging/IPMI xyz.openbmc_project.Logging.IPMI IpmiSelAdd ssaybq "OEM BMC health utilization ${assert_m}" "/xyz/openbmc_project/sensors/oem_health/utilization" 3 ${un} ${usage} ${ipmid_mem} ${assert} 0x2000

# DEBUG print
echo "type: ${util}, code: ${un}, assert: ${assert}, usage: ${usage}, ipmi mem usage: ${ipmid_mem}"
