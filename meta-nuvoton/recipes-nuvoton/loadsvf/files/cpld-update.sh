#!/bin/sh

sleep 1

# get cpld firmware file name as firmware version
version=$(find /tmp/ -name "*.svf")
filename=$(basename $version .svf)
filename+=".svf"

if [ -z "$version" ]
then 
   echo "No CPLD FW found."
   echo "VERSION_ID=N/A" > /var/lib/phosphor-bmc-code-mgmt/cpld-release
else
   # Call loadsvf to process CPLD upgrade
   loadsvf -d /dev/jtag0 -s `echo $version`

   # Use file name as VERSION_ID to identify the cpld FW version
   echo "VERSION_ID=$filename" > /var/lib/phosphor-bmc-code-mgmt/cpld-release
fi