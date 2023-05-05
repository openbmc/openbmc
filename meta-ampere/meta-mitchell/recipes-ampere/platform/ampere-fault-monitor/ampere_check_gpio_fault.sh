#!/bin/bash

# This script monitors S0/S1 GPIO fault and detects errors from CPUs
#
# So far, there is no specification describes the behavior of LED when an error (a pattern is detected) occurs.
# So when detecting a pattern, we simply set the gpio fault flag and turn on the SYS LED.
#
# The Parttern will in the format:
# <minor_byte> <quite_gap_1second> <major_byte> <stop_condition_low_for_3seconds>
#
# Ex: pattern minor_byte=0x03, major_byte=0x02, you will see the waveform like
# _1010100...(quite gap, low for 1 second)..0111111111000000000111111111110000000000...(stop condition, low for 3 seconds)..
#
# Usage: <app_name> <socket 0/1>
#
# shellcheck source=/dev/null
source /usr/sbin/gpio-lib.sh

# global variables
    error_flag='/tmp/gpio_fault'

    # the command "cat /sys/class/gpio/gpio"$gpio_Id"/value" itself, takes 10ms~35ms to complete, depends on CPU loading
    polling_minor_byte_rate=0
    polling_major_byte_rate=200000
    polling_rate=$polling_minor_byte_rate

    # the mount of low to ensure that already get out of minor_byte and is in quite gap
    # this value depends on the polling_minor_byte_rate
    max_low_in_minor_byte=9

    # the mount of low to ensure that already get out of major_byte and is in stop condition
    # this value depends on the polling_major_byte_rate
    max_low_in_major_byte=9

    max_low=$max_low_in_minor_byte

    # state machines:
    # detecting_minor_byte=0
    # detecting_major_byte=1
    curr_state=0

    minor_byte=0
    major_byte=0

    gpio_status=0

    socket=$1

    socket1_present=151
    socket1_status=1

    S0_fault_gpio='s0-fault-alert'
    S1_fault_gpio='s1-fault-alert'

map_event_name() {
    case $major_byte in
        2)
            event_major="FAULT_LED_BOOT_ERROR"
            case $minor_byte in
                1)
                    event_minor="SOC_BOOTDEV_INIT_SEC_ERROR"
                    ;;
                2)
                    event_minor="SECJMP_FAIL_ERROR"
                    ;;
                3)
                    event_minor="UART_INIT_WARN"
                    ;;
                4)
                    event_minor="UART_TX_WARN"
                    ;;
                5)
                    event_minor="SOC_ROMPATCH_BAD_ERROR"
                    ;;
                6)
                    event_minor="SOC_ROMPATCH_RANGE_ERROR"
                    ;;
                7)
                    event_minor="SPI_INIT_ERROR"
                    ;;
                8)
                    event_minor="SPI_TX_ERROR"
                    ;;
                9)
                    event_minor="SPINOR_UNKNOW_DEVICE_WARN"
                    ;;
                10)
                    event_minor="EEPROM_BAD_NVP_HEADER_WARN"
                    ;;
                11)
                    event_minor="EEPROM_BAD_NVP_FIELD_WARN"
                    ;;
                12)
                    event_minor="EEPROM_BAD_CHECKSUM_ERROR_WARN"
                    ;;
                13)
                    event_minor="I2C_DMA_ERROR"
                    ;;
                14)
                    event_minor="I2C_TIMEOUT_ERROR"
                    ;;
                15)
                    event_minor="SOC_BOOTDEV_SPI_LOAD_ERROR"
                    ;;
                16)
                    event_minor="SOC_BOOTDEV_AUTHENTICATION_ERROR"
                    ;;
                17)
                    event_minor="PCP_POWERUP_FAILED"
                    ;;
                18)
                    event_minor="PCP_POWERDOWN_FAILED"
                    ;;
                19)
                    event_minor="CPUPLL_INIT_FAILED"
                    ;;
                20)
                    event_minor="MESHPLL_INIT_FAILED"
                    ;;
                *)
                    event_minor="NOT_SUPPORT"
            esac
            ;;
        3)
            event_major="FAULT_LED_FW_LOAD_ERROR"
            case $minor_byte in
                9)
                    event_minor="LFS_ERROR"
                    ;;
                *)
                    event_minor="NOT_SUPPORT"
            esac
            ;;
        4)
            event_major="FAULT_LED_SECURITY_ERROR"
            case $minor_byte in
                1)
                    event_minor="SEC_INVALID_KEY_CERT"
                    ;;
                2)
                    event_minor="SEC_INVALID_CONT_CERT"
                    ;;
                3)
                    event_minor="SEC_INVALID_ROOT_KEY"
                    ;;
                4)
                    event_minor="SEC_INVALID_SECPRO_KEY"
                    ;;
                5)
                    event_minor="SEC_INVALID_KEY_CERT_SIG"
                    ;;
                6)
                    event_minor="SEC_INVALID_CONT_CERT_SIG"
                    ;;
                7)
                    event_minor="SEC_INVALID_IMAGE_HASH"
                    ;;
                8)
                    event_minor="SEC_INVALID_PRI_VERSION"
                    ;;
                9)
                    event_minor="SEC_HUK_MISMATCH"
                    ;;
                10)
                    event_minor="SEC_FUSE_BLOW_CERT_WITHOUT_SPECIAL_BOOT_PIN"
                    ;;
                11)
                    event_minor="SEC_INVALID_CERT_SUBTYPE_STRUCT"
                    ;;
                12)
                    event_minor="SEC_TMMCFG_FAIL"
                    ;;
                13)
                    event_minor="SEC_INVALID_LCS_FROM_EFUSE"
                    ;;
                14)
                    event_minor="SEC_EFUSE_WRITE_FAILED"
                    ;;
                15)
                    event_minor="SEC_INVALID_CERT_VALUE"
                    ;;
                16)
                    event_minor="SEC_INVALID_CERT_VERSION"
                    ;;
                *)
                    event_minor="NOT_SUPPORT"
                    ;;
            esac
            ;;
        5)
            event_major="FAULT_LED_EXCEPTION_ERROR"
            case $minor_byte in
                1)
                    event_minor="KERNEL_EXCEPTION_UNKNOWN_REASON_ERROR"
                    ;;
                2)
                    event_minor="KERNEL_EXCEPTION_HARD_FAULT_ERROR"
                    ;;
                3)
                    event_minor="KERNEL_EXCEPTION_BUS_FAULT_ERROR"
                    ;;
                4)
                    event_minor="KERNEL_EXCEPTION_MEMMANAGE_FAULT_ERROR"
                    ;;
                5)
                    event_minor="KERNEL_EXCEPTION_USAGE_FAULT_ERROR"
                    ;;
                *)
                    event_minor="NOT_SUPPORT"
                    ;;
            esac
            ;;
        *)
            event_major="NOT_SUPPORT"
            ;;
    esac
}

set_unset_gpio_fault_flag() {
    if [ ! -f $error_flag ] && [ "$1" == 1 ] ; then
        touch $error_flag
    elif [ -f $error_flag ] && [ "$1" == 0 ]; then
        rm $error_flag
    fi
}

toggle_state() {
    if [ "$curr_state" == 0 ]; then
        curr_state=1
        polling_rate=$polling_major_byte_rate
    else
        curr_state=0
        polling_rate=$polling_minor_byte_rate
        map_event_name
        echo "detected major_byte=$event_major, minor_byte=$event_minor"
        set_unset_gpio_fault_flag 1
    fi
}

save_pulse_of_byte() {
    if [ "$curr_state" == 0 ]; then
        minor_byte=$1
        #echo "minor_byte=$1"
    else
        major_byte=$1
        #echo "major_byte=$1"
    fi
}

# we do not care the pulse is 50ms or 500ms, what we care is that the number of high pulses
cnt_falling_edge_in_byte() {
    local cnt_falling_edge=0
    local cnt_low=0

    local prev=0
    local curr=0

    while true
    do
        prev=$curr
        curr=$gpio_status
        # count the falling edges, if they occur, just reset cnt_low
        if [ "$prev" == 1 ] && [ "$curr" == 0 ]; then
            cnt_falling_edge=$(( cnt_falling_edge + 1 ))
            cnt_low=0
            continue
        # check if we are in the quite gap or stop condition
        elif [ "$prev" == 0 ] && [ "$curr" == 0 ]; then
            cnt_low=$(( cnt_low + 1 ))
            if [ "$cnt_low" == "$max_low" ]; then
                save_pulse_of_byte "$cnt_falling_edge"
                toggle_state
                break
            fi
        fi
        usleep $polling_rate
        gpio_status=$(cat /sys/class/gpio/gpio"$gpio_Id"/value)
    done
}

gpio_config_input() {
    echo "$gpio_Id" > /sys/class/gpio/export
    echo "in" > /sys/class/gpio/gpio"${gpio_Id}"/direction
}

gpio_number() {
    local offset
    local gpioPin
    local str

    str=$(gpiofind "$1")
    if [ "$?" == '1' ]; then
        echo -1
    else
        gpioid=$(echo "$str"|cut -c 9)
        offset=$(echo "$str"|cut -d " " -f 2)
        gpioPin=$(("$offset" + ${AST2600_GPIO_BASE[$gpioid]}))
        echo "$gpioPin"
    fi
}

init_sysfs_fault_gpio() {
    gpio_Id=$(gpio_number "$fault_gpio")
    if [ "$gpio_Id" == "-1" ]; then
        echo "Invalid GPIO number"
        exit 1
    fi

    if [ -d /sys/class/gpio/gpio"$gpio_Id" ]; then
        return
    fi
    gpio_config_input "$gpio_Id"
}

# init
if [ "$socket" == "0" ]; then
	fault_gpio="$S0_fault_gpio"
else
	socket1_status=$(gpioget 0 "$socket1_present")
	if [ "$socket1_status" == 1 ]; then
		echo "socket 1 not present"
		exit 0
	fi
	fault_gpio=$S1_fault_gpio
fi

init_sysfs_fault_gpio

# daemon start
while true
do
    # detect when pattern starts
    if [ "$gpio_status" == 1 ]; then
        if [ "$curr_state" == 0 ]; then
            # detecting minor byte, set up minor byte variables
            max_low=$max_low_in_minor_byte
            polling_rate=$polling_minor_byte_rate
        else
            # detecting major byte, set up major byte variables
            max_low=$max_low_in_major_byte
            polling_rate=$polling_major_byte_rate
        fi
        # now, there is something on gpio, check if that is a byte pattern
        cnt_falling_edge_in_byte
    fi

    usleep $polling_rate
    gpio_status=$(cat /sys/class/gpio/gpio"$gpio_Id"/value)
done

exit 1
