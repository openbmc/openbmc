#!/bin/bash
#
# This script is used to get the MAC Address from FRU Inventory information

ETHERNET_INTERFACE="eth1"
ETHERNET_NCSI="eth0"
ENV_ETH="eth1addr"
ENV_MAC_ADDR=$(fw_printenv | grep $ENV_ETH)

# Workaround to dhcp NC-SI eth0 interface when BMC boot up
ifconfig ${ETHERNET_NCSI} down
ifconfig ${ETHERNET_NCSI} up

# Read FRU Board Custom Field 1 to get the MAC address
for i in {1..10}; do
	if CUSTOM_FIELD_1=$(busctl get-property xyz.openbmc_project.FruDevice /xyz/openbmc_project/FruDevice/Mt_Jade_Motherboard xyz.openbmc_project.FruDevice BOARD_INFO_AM1);
	then
		MAC_ADDR=$(echo "$CUSTOM_FIELD_1" | cut -d "\"" -f 2)
		echo "mac-update: detect BMC MAC $MAC_ADDR at loop $i"
		break
	fi
	sleep 2
done

# Check if BMC MAC address is exported
if [ -z "${MAC_ADDR}" ]; then
	echo "ERROR: No BMC MAC address is detected from FRU Inventory information!"
	# Return 1 so that systemd knows the service failed to start
	exit 1
fi

# Check if BMC MAC address is exported
if [[ $ENV_MAC_ADDR =~ $MAC_ADDR ]]; then
	echo "WARNING: BMC MAC address already exist!"
	exit 0
fi

# Request to update the MAC address
if ! fw_setenv ${ENV_ETH} "${MAC_ADDR}";
then
	echo "ERROR: Fail to set MAC address to ${ENV_ETH}"
	exit 1
fi

# Request to restart the service
ifconfig ${ETHERNET_INTERFACE} down
if ! ifconfig ${ETHERNET_INTERFACE} hw ether "${MAC_ADDR}";
then
	echo "ERROR: Can not update MAC ADDR to ${ETHERNET_INTERFACE}"
	exit 1
fi
ifconfig ${ETHERNET_INTERFACE} up

echo "Successfully update the MAC address ${MAC_ADDR} to ${ENV_ETH} and ${ETHERNET_INTERFACE}"
