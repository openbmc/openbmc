#!/bin/bash
#
# This script is used to get the MAC Address from FRU Inventory information

ETHERNET_INTERFACE="eth0"
ENV_ETH="eth1addr"
ENV_MAC_ADDR=`fw_printenv`

# Check if BMC MAC address is exported
if [[ $ENV_MAC_ADDR =~ $ENV_ETH ]]; then
    echo "WARNING: BMC MAC address already exist!"
    exit 0
fi

# Read FRU Board Custom Field 1 to get the MAC address
CUSTOM_FIELD_1=`busctl get-property xyz.openbmc_project.Inventory.Manager /xyz/openbmc_project/inventory/system/chassis/motherboard xyz.openbmc_project.Inventory.Item.NetworkInterface MACAddress`
MAC_ADDR=`echo $CUSTOM_FIELD_1 | cut -d "\"" -f 2`

# Check if BMC MAC address is exported
if [ -z "${MAC_ADDR}" ]; then
    echo "ERROR: No BMC MAC address is detected from FRU Inventory information!"
    # Return 1 so that systemd knows the service failed to start
    exit 1
fi

# Request to update the MAC address
fw_setenv ${ENV_ETH} ${MAC_ADDR}

if [[ $? -ne 0 ]]; then
   echo "ERROR: Fail to set MAC address to ${ENV_ETH}"
   exit 1
fi

# Request to restart the service
ifconfig ${ETHERNET_INTERFACE} down
ifconfig ${ETHERNET_INTERFACE} hw ether ${MAC_ADDR}
if [[ $? -ne 0 ]]; then
   echo "ERROR: Can not update MAC ADDR to ${ETHERNET_INTERFACE}"
   exit 1
fi
ifconfig ${ETHERNET_INTERFACE} up

echo "Successfully update the MAC address ${MAC_ADDR} to ${ENV_ETH} and ${ETHERNET_INTERFACE}"
