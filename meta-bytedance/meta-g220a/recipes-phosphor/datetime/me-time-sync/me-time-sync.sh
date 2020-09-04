#!/bin/bash

set -e

# Get time from ME via ipmb
# The last 4 bytes are the epoch time, e.g.
# (iyyyyay) 0 11 0 72 0 4 18 169 82 95
ret=$(busctl call xyz.openbmc_project.Ipmi.Channel.Ipmb "/xyz/openbmc_project/Ipmi/Channel/Ipmb" org.openbmc.Ipmb sendRequest yyyyay 0x01 0x0a 0x00 0x48 0)

IFS=' ' read -r -a a <<< "${ret}"

if [ "${a[1]}" -ne 0 ]
then
  echo "Failed to get time from ME: ${ret}"
  exit 1
fi

t0=$((${a[7]}))
t1=$((${a[8]}*256))
t2=$((${a[9]}*256*256))
t3=$((${a[10]}*256*256*256))
t=$((${t0}+${t1}+${t2}+${t3}))
echo "Setting date to ${t}"

date -s @${t}
