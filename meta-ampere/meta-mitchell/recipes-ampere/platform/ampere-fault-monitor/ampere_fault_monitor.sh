#!/bin/bash
# This script monitors fan, over-temperature, PSU, CPU/SCP failure and update fault LED status

# shellcheck disable=SC2004
# shellcheck source=/dev/null
source /usr/sbin/gpio-lib.sh

# common variables
	on=1
	off=0

    overtemp_fault_flag='/tmp/fault_overtemp'

# gpio fault
	gpio_fault="false"
	gpio_fault_flag="/tmp/gpio_fault"

# fan variables
	fan_failed="false"
	fan_failed_flag='/tmp/fan_failed'

# PSU variables
	psu_failed="false"
	psu_bus=2
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
	fan_fault_led_status=$off
	psu_fault_led_status=$off
	led_bus=15
	led_addr=0x22
	led_port0_config=0x06
	led_port0_output=0x02

# functions declaration
check_fan_failed() {
	if [[ -f $fan_failed_flag ]]; then
		fan_failed="true"
	else
		fan_failed="false"
	fi
}

turn_on_off_fan_fault_led() {
	# Control fan fault led via CPLD's I2C at slave address 0x22, I2C16.
	# Get Port0 value
	p0_val=$(i2cget -f -y $led_bus $led_addr $led_port0_config)
	p0_val=$(("$p0_val" & ~1))
	# Config CPLD's IOepx Port0[0] from input to output, clear IOepx Port0[0].
	i2cset -f -y $led_bus $led_addr $led_port0_config $p0_val

	# Get led value
	led_st=$(i2cget -f -y $led_bus $led_addr $led_port0_output)

	if [ "$1" == $on ]; then
		led_st=$(("$led_st" | 1))
	else
		led_st=$(("$led_st" & ~1))
	fi

	# Turn on/off fan fault led
	i2cset -f -y $led_bus $led_addr $led_port0_output $led_st
}

turn_on_off_psu_fault_led() {
	# Control psu fault led via CPLD's I2C at slave address 0x22, I2C16.
	# Get Port1 value
	p1_val=$(i2cget -f -y $led_bus $led_addr $led_port0_config)
	p1_val=$(("$p1_val" & ~2))
	# Config CPLD's IOepx Port0[1] from input to output, clear IOepx Port0[1].
	i2cset -f -y $led_bus $led_addr $led_port0_config $p1_val

	# Get led value
	led_st=$(i2cget -f -y $led_bus $led_addr $led_port0_output)
	if [ "$1" == $on ]; then
		led_st=$(("$led_st" | 2))
	else
		led_st=$(("$led_st" & ~2))
	fi

	# Turn on/off psu fault led
	i2cset -f -y $led_bus $led_addr $led_port0_output $led_st
}

control_fan_fault_led() {
	if [ "$fan_failed" == "true" ]; then
		if [ "$fan_fault_led_status" == $off ]; then
			turn_on_off_fan_fault_led $on
			fan_fault_led_status=$on
		fi
	else
		if [ "$fan_fault_led_status" == $on ]; then
			turn_on_off_fan_fault_led $off
			fan_fault_led_status=$off
		fi
	fi
}

check_psu_failed() {
	local psu0_presence
	local psu1_presence
	local psu0_value
	local psu1_value

	psu0_presence=$(gpio_name_get presence-ps0)
	psu0_failed="true"
	if [ "$psu0_presence" == "0" ]; then
		# PSU0 presence, monitor the PSUs using pmbus, check the STATUS_WORD
		psu0_value=$(i2cget -f -y $psu_bus $psu0_addr $status_word_cmd w)
		psu0_bit_fault=$(($psu0_value & $psu_fault_bitmask))
		if [ "$psu0_bit_fault" == "0" ]; then
			psu0_failed="false"
		fi
	fi

	psu1_presence=$(gpio_name_get presence-ps1)
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

control_psu_fault_led() {
	if [ "$psu_failed" == "true" ]; then
		if [ "$psu_fault_led_status" == $off ]; then
			turn_on_off_psu_fault_led $on
			psu_fault_led_status=$on
		fi
	else
		if [ "$psu_fault_led_status" == $on ]; then
			turn_on_off_psu_fault_led $off
			psu_fault_led_status=$off
		fi
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


check_gpio_fault() {
    if [[ -f $gpio_fault_flag ]]; then
        echo "GPIO fault event(s) occured, turn on fault LED"
        gpio_fault="true"
    else
        gpio_fault="false"
    fi
}

check_fault() {
	if [[ "$fan_failed" == "true" ]] || [[ "$psu_failed" == "true" ]] \
                                    || [[ "$overtemp_occured" == "true" ]] \
                                    || [[ "$gpio_fault" == "true" ]]; then
		fault="true"
	else
		fault="false"
	fi
}

# The System Fault Led turns on upon the system error, update the System Fault Led
# based on the Fan fault status and PSU fault status
control_sys_fault_led() {
	# Turn on/off the System Fault Led
	if [ "$fault" == "true" ]; then
		gpio_name_set led-fault $on
	else
		gpio_name_set led-fault $off
	fi
}

# daemon start
while true
do
	#  Monitors Fan speeds
	check_fan_failed
	# Monitors PSU presence
	check_psu_failed

	check_overtemp_occured
	check_gpio_fault
	# Check fault to update fail
	check_fault
	control_sys_fault_led

	control_fan_fault_led
	control_psu_fault_led

	sleep 2
done

exit 1
