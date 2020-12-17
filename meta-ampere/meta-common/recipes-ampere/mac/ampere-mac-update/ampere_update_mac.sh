#!/bin/bash
#
# This script is used to get the MAC Address from FRU Inventory information
# Author : Hoang Nguyen  <hnguyen@amperecomputing.com>
# Modify : Chanh Nguyen  <chnguyen@amperecomputing.com>

ETHERNET_INTERFACE="eth1"
ETHERNET_NCSI="eth0"
ENV_ETH="eth1addr"
ENV_MAC_ADDR=`fw_printenv`

# Workaround to dhcp NC-SI eth0 interface when BMC boot up
ifconfig ${ETHERNET_NCSI} down
ifconfig ${ETHERNET_NCSI} up

# Check if BMC MAC address is exported
if [[ $ENV_MAC_ADDR =~ $ENV_ETH ]]; then
    echo "WARNING: BMC MAC address is alread updated!"
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
   echo "ERROR: fw_setenv failed"
   exit 1
fi

# Request to restart the service
ifconfig ${ETHERNET_INTERFACE} down
ifconfig ${ETHERNET_INTERFACE} hw ether ${MAC_ADDR}
ifconfig ${ETHERNET_INTERFACE} up

if [[ $? -ne 0 ]]; then
   echo "ERROR: Can not update MAC ADDR"
   exit 1
fi

echo "Updated the MAC address!"
