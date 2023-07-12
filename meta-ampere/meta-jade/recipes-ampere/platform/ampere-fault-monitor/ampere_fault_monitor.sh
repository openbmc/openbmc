#!/bin/bash

# This script monitors fan, over-temperature, PSU, CPU/SCP failure and update fault LED status

# shellcheck disable=SC2004
# shellcheck disable=SC2046

# common variables
	warning_fault_flag='/tmp/fault_warning'
	error_fault_flag='/tmp/fault_err'
	overtemp_fault_flag='/tmp/fault_overtemp'
	fault_RAS_UE_flag='/tmp/fault_RAS_UE'

	blink_rate=100000

	fault="false"

	on="true"
	off="false"

	gpio_fault="false"

# fan variables
	fan_failed="false"
	fan_failed_flag='/tmp/fan_failed'

# PSU variables
	psu_failed="false"
	psu_bus=6
	psu0_addr=0x58
	psu1_addr=0x59
	status_word_cmd=0x79
	# Following the PMBus Specification
	# Bit[1]: CML faults
	# Bit[2]: Over temperature faults
	# Bit[3]: Under voltage faults
	# Bit[4]: Over current faults
	# Bit[5]: Over voltage fault
	# Bit[10]: Fan faults
	psu_fault_bitmask=0x43e

# led variables
	led_service='xyz.openbmc_project.LED.GroupManager'
	led_fault_path='/xyz/openbmc_project/led/groups/system_fault'
	led_fault_interface='xyz.openbmc_project.Led.Group'
	fault_led_status=$off

# functions declaration
check_fan_failed() {
	if [[ -f $fan_failed_flag ]]; then
		fan_failed="true"
	else
		fan_failed="false"
	fi
}

turn_on_off_fault_led() {
	busctl set-property $led_service $led_fault_path $led_fault_interface Asserted b "$1" >> /dev/null
}

check_psu_failed() {
	local psu0_presence
	local psu1_presence
	local psu0_value
	local psu1_value

	psu0_presence=$(gpioget $(gpiofind presence-ps0))
	psu0_failed="true"
	if [ "$psu0_presence" == "0" ]; then
		# PSU0 presence, monitor the PSUs using pmbus, check the STATUS_WORD
		psu0_value=$(i2cget -f -y $psu_bus $psu0_addr $status_word_cmd w)
		psu0_bit_fault=$(($psu0_value & $psu_fault_bitmask))
		if [ "$psu0_bit_fault" == "0" ]; then
			psu0_failed="false"
		fi
	fi

	psu1_presence=$(gpioget $(gpiofind presence-ps1))
	psu1_failed="true"
	if [ "$psu1_presence" == "0" ]; then
		# PSU1 presence, monitor the PSUs using pmbus, check the STATUS_WORD
		psu1_value=$(i2cget -f -y $psu_bus $psu1_addr $status_word_cmd w)
		psu1_bit_fault=$(($psu1_value & $psu_fault_bitmask))
		if [ "$psu1_bit_fault" == "0" ]; then
			psu1_failed="false"
		fi
	fi

	if [ "$psu0_failed" == "true" ] || [ "$psu1_failed" == "true" ]; then
		psu_failed="true"
	else
		psu_failed="false"
	fi
}

check_fault() {
	if [[ "$fan_failed" == "true" ]] || [[ "$psu_failed" == "true" ]] \
									|| [[ "$gpio_fault" == "true" ]] \
									|| [[ "$RAS_UE_occured" == "true" ]] \
									|| [[ "$overtemp_occured" == "true" ]]; then
		fault="true"
	else
		fault="false"
	fi
}

control_fault_led() {
	if [ "$fault" == "true" ]; then
		if [ "$fault_led_status" == $off ]; then
			turn_on_off_fault_led $on
			fault_led_status=$on
		fi
	else
		if [ "$fault_led_status" == $on ]; then
			turn_on_off_fault_led $off
			fault_led_status=$off
		fi
	fi
}

blink_fault_led() {
	if [ "$fault_led_status" == $off ]; then
		turn_on_off_fault_led $on
		usleep $blink_rate
		turn_on_off_fault_led $off
	else
		turn_on_off_fault_led $off
		usleep $blink_rate
		turn_on_off_fault_led $on
	fi
}

check_gpio_fault() {
	if [[ -f $error_fault_flag ]]; then
		gpio_fault="true"
	else
		if [ -f $warning_fault_flag ]; then
			blink_fault_led
			rm $warning_fault_flag
		fi
		gpio_fault="false"
	fi
}

check_RAS_UE_occured() {
	if [[ -f $fault_RAS_UE_flag ]]; then
		echo "RAS UE error occured, turn on fault LED"
		RAS_UE_occured="true"
	else
		RAS_UE_occured="false"
	fi
}

check_overtemp_occured() {
	if [[ -f $overtemp_fault_flag ]]; then
		echo "Over temperature occured, turn on fault LED"
		overtemp_occured="true"
	else
		overtemp_occured="false"
	fi
}

# daemon start
while true
do
	check_gpio_fault
	check_fan_failed
	check_overtemp_occured
	check_RAS_UE_occured

	# Monitors PSU presence
	check_psu_failed

	check_fault
	control_fault_led
	sleep 2
done

exit 1
