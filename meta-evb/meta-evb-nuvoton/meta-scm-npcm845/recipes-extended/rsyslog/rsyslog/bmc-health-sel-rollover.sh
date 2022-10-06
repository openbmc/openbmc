#!/bin/sh
set -e
# get and increase rollover count script

# handle rollover count first
SEL_FILE="/var/log/ipmi_sel_rollover"
num=0
if [ ! -f ${SEL_FILE} ]; then
  num=1
else
  num=$(expr `cat ${SEL_FILE}` + 1)
fi
echo ${num} > ${SEL_FILE}

# write SEL rollover
busctl call `mapper get-service /xyz/openbmc_project/Logging/IPMI` /xyz/openbmc_project/Logging/IPMI xyz.openbmc_project.Logging.IPMI IpmiSelAdd ssaybq "OEM BMC health SEL rollover" "/xyz/openbmc_project/sensors/oem_health/sel_rollover" 3 171 ${num} 0 true 0x2000
