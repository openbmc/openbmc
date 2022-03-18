#!/bin/bash

# Check current Host status. Do nothing when the Host is currently ON
st=$(busctl get-property xyz.openbmc_project.State.Host \
	/xyz/openbmc_project/state/host0 xyz.openbmc_project.State.Host \
	CurrentHostState | cut -d"." -f6)
if [ "$st" == "Running\"" ]; then
	exit 0
fi

# Time out checking for Host ON is 60s
cnt=60
while [ "$cnt" -gt 0 ];
do
	cnt=$((cnt - 1))
	st=$(busctl call xyz.openbmc_project.State.HostCondition.Gpio \
		/xyz/openbmc_project/Gpios/host0 org.freedesktop.DBus.Properties \
		Get ss xyz.openbmc_project.Condition.HostFirmware \
		CurrentFirmwareCondition | cut -d"." -f6)
	if [ "$st" == "Running\"" ]; then
		exit 0
	fi
	sleep 1
done

exit 1
