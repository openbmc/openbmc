#!/bin/bash

# This script monitors S0/S1 fault GPIO and detects errors or warnings from CPUs
#
# According to OpenBMC_Software_Funcional_Specification, section 3.16,
#
# When the BMC detects the GPIO_FAULT signal indicating an SCP booting failure:
# •  If a non-critical error/warning from the SCP occurs, the BMC blinks the Fault LED once.
# •  If a critical error from the SCP occurs, the BMC turns on the Fault LED.
# The BMC monitors the GPIO_FAULT signal from the SCP during SCP booting to determine whether
# the error is non-critical or critical. A fatal error is indicated when the signal is On and then Off
# continuously, followed by a “quiet” period of about three seconds, and this pattern repeats. If the “quiet”
# period is longer than three seconds, the error is non-fatal. The BMC must set up appropriate debounce
# times to detect such errors. The BMC is expected to turn on the Fault LED forever for fatal errors, or to
# turn on the Fault LED and turn it off when the fault clears for non-fatal errors.
#
# Usage: <app_name> <socket 0/1>

# shellcheck source=/dev/null
source /usr/sbin/gpio-lib.sh

# global variables
	error_flag='/tmp/fault_err'
	warning_flag='/tmp/fault_warning'

	duty_cycle=250000
	scan_pulse=100000
	blank_num=8

	curr_pattern=0
	prev_pattern=0

	gpio_status=0
	repeat=0

	socket=$1

	socket1_present=15
	socket1_status=1

	S0_fault_gpio=73
	S1_fault_gpio=201

map_event_name() {
	case $curr_pattern in
		1)
			event_name="RAS_GPIO_INVALID_LCS"
			;;
		2)
			event_name="RAS_GPIO_FILE_HDR_INVALID"
			;;
		3)
			event_name="RAS_GPIO_FILE_INTEGRITY_INVALID"
			;;
		4)
			event_name="RAS_GPIO_KEY_CERT_AUTH_ERR"
			;;
		5)
			event_name="RAS_GPIO_CNT_CERT_AUTH_ERR"
			;;
		6)
			event_name="RAS_GPIO_I2C_HARDWARE_ERR"
			;;
		7)
			event_name="RAS_GPIO_CRYPTO_ENGINE_ERR"
			;;
		8)
			event_name="RAS_GPIO_ROTPK_EFUSE_INVALID"
			;;
		9)
			event_name="RAS_GPIO_SEED_EFUSE_INVALID"
			;;
		10)
			event_name="RAS_GPIO_LCS_FROM_EFUSE_INVALID"
			;;
		11)
			event_name="RAS_GPIO_PRIM_ROLLBACK_EFUSE_INVALID"
			;;
		12)
			event_name="RAS_GPIO_SEC_ROLLBACK_EFUSE_INVALID"
			;;
		13)
			event_name="RAS_GPIO_HUK_EFUSE_INVALID"
			;;
		14)
			event_name="RAS_GPIO_CERT_DATA_INVALID"
			;;
		15)
			event_name="RAS_GPIO_INTERNAL_HW_ERR"
			;;
		*)
			event_name="NOT_SUPPORT"
			;;
	esac
}

detect_patern_repeat() {
	local prev=0
	local curr=0
	local cnt=13

	while true
	do
		usleep $scan_pulse
		gpio_status=$(cat /sys/class/gpio/gpio"$gpio_Id"/value)
		prev=$curr
		curr=$gpio_status
		if [ "$prev" == 0 ] && [ "$curr" == 1 ]; then
			# patern start repeating, check if previous and current pattern are the same
			repeat=1
			break
		fi
		if [ "$cnt" == 0 ]; then
			map_event_name
			echo "detected a warning from fault GPIO #$fault_gpio $socket, event $event_name"
			# pattern not repeat, this is a warning, turn on warning flag
			touch $warning_flag
			break
		fi
		cnt=$(( cnt - 1 ))
	done
}

detect_pattern() {
	local cnt_falling_edge=0
	local cnt_blank=0

	local prev=0
	local curr=0

	while true
	do
		prev=$curr
		curr=$gpio_status
		# count the falling edges, if they appear, just reset cnt_blank
		if [ "$prev" == 1 ] && [ "$curr" == 0 ]; then
			cnt_falling_edge=$(( cnt_falling_edge + 1 ))
			cnt_blank=0
			continue
		# check if we are in the quite gap
		elif [ "$prev" == 0 ] && [ "$curr" == 0 ]; then
			cnt_blank=$(( cnt_blank + 1 ))
			if [ "$cnt_blank" == "$blank_num" ]; then
				# echo "pattern number falling_edge=$cnt_falling_edge blank=$cnt_blank"
				curr_pattern=$cnt_falling_edge
				# after count all falling edges, now check if patern repeat after 3s
				detect_patern_repeat
				break
			fi
		fi
		usleep $scan_pulse
		gpio_status=$(cat /sys/class/gpio/gpio"$gpio_Id"/value)
	done
}

gpio_config_input() {
	echo "$gpio_Id" > /sys/class/gpio/export
	echo "in" > /sys/class/gpio/gpio"${gpio_Id}"/direction
}

init_sysfs_fault_gpio() {
	gpio_Id=$(gpio_number "$fault_gpio")
	if [ -d /sys/class/gpio/gpio"$gpio_Id" ]; then
		return
	fi
	gpio_config_input "$fault_gpio"
}

# init
if [ "$socket" == "0" ]; then
	fault_gpio=$S0_fault_gpio
else
	socket1_status=$(gpioget 0 "$socket1_present")
	if [ "$socket1_status" == 1 ]; then
		echo "socket 1 not present"
		exit 1
	fi
	fault_gpio=$S1_fault_gpio
fi

init_sysfs_fault_gpio

# daemon start
while true
do
	# detect when pattern starts
	if [ "$gpio_status" == 1 ]; then
		# now, there is something on gpio, check if that is a pattern
		detect_pattern
		if [ "$repeat" == 1 ] && [ "$prev_pattern" == "$curr_pattern" ]; then
			map_event_name
			echo "detected an error from fault GPIO #$fault_gpio $socket, event#$curr_pattern $event_name"
			touch $error_flag
			repeat=0
		fi
		prev_pattern=$curr_pattern
		curr_pattern=0
		continue
	fi
	usleep $duty_cycle
	gpio_status=$(cat /sys/class/gpio/gpio"$gpio_Id"/value)

done

exit 1
