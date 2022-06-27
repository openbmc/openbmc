#!/bin/bash
#
# This script is used to get the BMC MAC Address from FRU EEPROM at Board Extra.
# and if the eth address is not exist on U-boot Enviroment, this script will update it.
#

function Usage () {
	echo "Usage:"
	echo "      ampere_update_mac.sh <ethX> <fru bus> <fru addr>"
	echo "Example:"
	echo "      ampere_update_mac.sh eth1 3 80"
}

function read_mac_address () {
	fruBus=$1
	fruAddr=$2

	if FRU_OBJECT_PATH=$(busctl tree xyz.openbmc_project.FruDevice | grep "/xyz/openbmc_project/FruDevice/" | tr -s '\n' ' ' | tr -d "|-" | tr -d '`')
	then
		IFS=' ' read -r -a FRU_OBJ_PATH_ARR <<< "$FRU_OBJECT_PATH"

		for fruObj in "${FRU_OBJ_PATH_ARR[@]}"
		do
			BUS_IDX_RW=$(busctl get-property xyz.openbmc_project.FruDevice "$fruObj" xyz.openbmc_project.FruDevice BUS)
			BUS_ADDR_RW=$(busctl get-property xyz.openbmc_project.FruDevice "$fruObj" xyz.openbmc_project.FruDevice ADDRESS)

			if [ -z "$BUS_IDX_RW" ] || [ -z "$BUS_IDX_RW" ]; then
				continue
			else
				BUS_IDX_CV=$(echo "$BUS_IDX_RW" | cut -d " " -f 2)
				BUS_ADDR_CV=$(echo "$BUS_ADDR_RW" | cut -d " " -f 2)
				if [ "$BUS_IDX_CV" != "$fruBus" ] || [ "$BUS_ADDR_CV" != "$fruAddr" ]; then
					continue
				fi
			fi

			MAC_ADDR_RAW=$(busctl get-property xyz.openbmc_project.FruDevice "$fruObj" xyz.openbmc_project.FruDevice BOARD_INFO_AM1)
			MAC_ADDR=$(echo "$MAC_ADDR_RAW" | cut -d "\"" -f 2)
			break
		done
	fi
	echo "$MAC_ADDR"
}

ETHERNET_INTERFACE=$1
BMC_FRU_BUS=$2
BMC_FRU_ADDR=$3

if [ -z "$BMC_FRU_ADDR" ];
then
	Usage
	exit
fi

# Check eth port
case ${ETHERNET_INTERFACE} in
	"eth0")
		ENV_PORT="1"
		;;
	"eth1")
		ENV_PORT="2"
		;;
	"eth2")
		ENV_PORT="3"
		;;
	*)
		Usage
		exit
		;;
esac

# Read FRU Board Custom Field 1 to get the MAC address
for i in {1..10}; do
	MAC_ADDR=$(read_mac_address "$BMC_FRU_BUS" "$BMC_FRU_ADDR")

	# Check if BMC MAC address is exported
	if [ -z "${MAC_ADDR}" ]; then
		sleep 2
		continue
	fi

	if echo "$MAC_ADDR" | grep -q -vE "^([0-9a-fA-F]{2}:){5}[0-9a-fA-F]{2}$" ; then
		echo "ERROR: No valid BMC MAC Address detected from BMC FRU! $MAC_ADDR"
		exit 0
	else
		echo "mac-update: detect BMC MAC $MAC_ADDR at loop $i"
		break
	fi
done

# Check if the Ethernet port has correct MAC Address
ETH_INCLUDE_MAC=$(ifconfig "${ETHERNET_INTERFACE}" | grep -i "$MAC_ADDR")
if [ -n "$ETH_INCLUDE_MAC" ]; then
	echo "BMC MAC Address is already configured"
	exit 0
fi

# Request to restart the service
ifconfig "${ETHERNET_INTERFACE}" down
fw_setenv bmc_macaddr "${MAC_ADDR}"

ifconfig "${ETHERNET_INTERFACE}" hw ether "${MAC_ADDR}"
retval=$?
if [[ $retval -ne 0 ]]; then
	echo "ERROR: Can not update MAC ADDR to ${ETHERNET_INTERFACE}"
	exit 1
fi
# Setting LAN MAC Address to xx:xx:xx:xx:xx:xx
ipmitool lan set "${ENV_PORT}" macaddr "${ETHERNET_INTERFACE}"
# Enableing BMC-generated ARP responses & Setting SNMP Community String to public
ipmitool lan set "${ENV_PORT}" arp respond on
ipmitool lan set "${ENV_PORT}" snmp public
ifconfig "${ETHERNET_INTERFACE}" up

echo "Successfully update the MAC address ${MAC_ADDR} to ${ETHERNET_INTERFACE}"
