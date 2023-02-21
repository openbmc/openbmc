#!/bin/sh

sleep 1

# get psu firmware file name as firmware version
version=$(find /tmp/ -name "*.svf")
filename=$(basename $version .svf)
filename+=".svf"

if [ -z "$version" ]
then 
   echo "No PSU FW found."
   echo "VERSION_ID=N/A" > /var/lib/phosphor-bmc-code-mgmt/psu-release
else
   # Call loadsvf to process PSU upgrade
   loadsvf -d /dev/jtag0 -s `echo $version`

   # Use file name as VERSION_ID to identify the psu FW version
   echo "VERSION_ID=$filename" > /var/lib/phosphor-bmc-code-mgmt/psu-release
fi