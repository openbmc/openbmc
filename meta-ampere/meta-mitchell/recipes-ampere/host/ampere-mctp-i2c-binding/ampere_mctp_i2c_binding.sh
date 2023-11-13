#!/bin/bash

# shellcheck disable=SC2046

function sx_present() {
	index=$1
	retVal=1
	objects=$(busctl call xyz.openbmc_project.ObjectMapper \
		/xyz/openbmc_project/object_mapper \
		xyz.openbmc_project.ObjectMapper GetObject sas \
		/xyz/openbmc_project/inventory/system/chassis/motherboard/cpu"$index" \
		1 xyz.openbmc_project.Inventory.Item)
	if [[ "$objects" == "" ]]; then
		state=$(gpioget $(gpiofind presence-cpu"$index"))
		if [[ "$state" == "0" ]]; then
			retVal=0
		fi
	else
		service=$(echo "$objects" | cut -d" " -f3 | cut -d"\"" -f2)
		if [[ "$objects" != "" ]]; then
			present=$(busctl get-property "$service" \
				/xyz/openbmc_project/inventory/system/chassis/motherboard/cpu"$index" \
				xyz.openbmc_project.Inventory.Item Present | cut -d" " -f2)
			if [[ "$present" == "true" ]]; then
				retVal=0
			fi
		fi
	fi

	echo "$retVal"
}

function s0_mctp_ready() {
	retVal="1"
	if i2cget -f -y 3 0x4f 0 >> /dev/null
	then
		retVal="0"
	fi
	echo "$retVal"
}

function s1_mctp_ready() {
	retVal="1"
	state=$(gpioget $(gpiofind s1-pcp-pgood))
	if [ "$state" == "1" ]; then
		if i2cget -f -y 3 0x4e 0 >> /dev/null
		then
			retVal="0"
		fi
	fi
	echo "$retVal"
}

function s0_sensor_available() {
	cnt=30
	retVal="1"
	while [ $cnt -gt 0 ]; do
		state=$(busctl get-property xyz.openbmc_project.PLDM \
			/xyz/openbmc_project/sensors/temperature/S0_ThrotOff_Temp \
			xyz.openbmc_project.Sensor.Value Value)
		if [[ "$state" != "" ]]; then
			retVal="0"
			break
		fi
		cnt=$((cnt - 1))
		sleep 1
	done
	echo "$retVal"
}

function add_endpoints() {
	# Add S0 MCTP endpoint
	cnt=20
	while [ $cnt -gt 0 ]; do
		state=$(s0_mctp_ready)
		if [[ "$state" == "0" ]]; then
			busctl call au.com.codeconstruct.MCTP1 \
				/au/com/codeconstruct/mctp1/interfaces/mctpi2c3 au.com.codeconstruct.MCTP.BusOwner1 \
				SetupEndpoint ay 1 0x4f
			ret=$?
			if [ $ret -eq 0 ]; then
				break
			fi
		fi
		cnt=$((cnt - 1))
		sleep 1
	done

	# Add S1 MCTP endpoint
	present=$(sx_present 1)
	if [[ "$present" == "1" ]]; then
		return
	fi

	state=$(s0_sensor_available)
	if [[ "$state" == "1" ]]; then
		return
	fi

	# wait for S1 mctp ready in 180 seconds
	cnt=180
	while [ $cnt -gt 0 ]; do
		state=$(s1_mctp_ready)
		if [ "$state" == "0" ]; then
			busctl call au.com.codeconstruct.MCTP1 \
				/au/com/codeconstruct/mctp1/interfaces/mctpi2c3 au.com.codeconstruct.MCTP.BusOwner1 \
				SetupEndpoint ay 1 0x4e
			ret=$?
			if [ $ret -eq 0 ]; then
				break
			fi
		fi
		cnt=$((cnt - 1))
		sleep 1
	done
}

function remove_endpoints() {
	busctl call au.com.codeconstruct.MCTP1 \
		/au/com/codeconstruct/mctp1/networks/1/endpoints/20 \
		au.com.codeconstruct.MCTP.Endpoint1 Remove

	present=$(sx_present 1)
	if [[ "$present" == "1" ]]; then
		exit
	fi

	busctl call au.com.codeconstruct.MCTP1 \
		/au/com/codeconstruct/mctp1/networks/1/endpoints/22 \
		au.com.codeconstruct.MCTP.Endpoint1 Remove
}

if [ "$1" == "add_endpoints" ]; then
	add_endpoints
elif [ "$1" == "remove_endpoints" ]; then
	remove_endpoints
fi

exit 0
