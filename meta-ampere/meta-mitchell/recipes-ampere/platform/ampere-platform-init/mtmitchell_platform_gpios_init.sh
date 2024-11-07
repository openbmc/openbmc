#!/bin/bash

# shellcheck disable=SC2046

function bind_rtc_driver() {
    # If rtc device can not present, bind the device
    if [[ ! -e /dev/rtc0 ]]; then
        echo "Bind rtc driver"
        echo 6-0051 > /sys/bus/i2c/drivers/rtc-pcf85063/bind
    fi
}

function pre-platform-init() {
    echo "Do pre platform init"
}

function post-platform-init() {

    # When BMC is rebooted, because PSON_L has pull up to P3V3_STB, it changes its
    # value to HIGH. Add code to check P3V3_STB and recover PSON_L to correct state
    # before setting BMC_RDY.
    pgood=$(gpioget $(gpiofind power-chassis-good))
    if [ "$pgood" == '1' ]; then
        echo "PSU is on. Setting PSON to 0"
        gpioset $(gpiofind power-chassis-control)=0
    else
        echo "PSU is off. Setting PSON to 1"
        gpioset $(gpiofind power-chassis-control)=1
    fi

    gpioset $(gpiofind host0-sysreset-n)=1

    echo "Set default FAN speed to 60%"
    for filename in /sys/class/hwmon/*/pwm*[1-6]
    do
        echo 153 > "$filename"
    done

    # Bind rtc driver
    bind_rtc_driver
}

export output_high_gpios_in_ac=(
    # add device enable, mux setting, device select gpios
    "spi0-backup-sel"
    "i2c-backup-sel"
)

export output_low_gpios_in_ac=(
    # add device enable, mux setting, device select gpios
    "spi0-program-sel"
    "ocp-main-pwren"
)

export output_high_gpios_in_bmc_reboot=(
    "host0-sysreset-n"
    "host0-pmin-n"
    "bmc-debug-mode"
    "vrd-sel"
    "spd-sel"
    "ext-high-temp-n"
    "fpga-program-b"
    "wd-disable-n"
    "hpm-stby-rst-n"
    "jtag-sel-s0"
    "cpld-user-mode"
    "jtag-srst-n"
    "host0-shd-req-n"
    "uart1-mode1"
    "uart2-mode1"
    "uart3-mode1"
    "uart4-mode1"
)

export output_low_gpios_in_bmc_reboot=(
    "s0-rtc-lock"
    "hpm-fw-recovery"
    "spi-nor-access"
    "host0-special-boot"
    "uart1-mode0"
    "uart2-mode0"
    "uart3-mode0"
    "uart4-mode0"
)
