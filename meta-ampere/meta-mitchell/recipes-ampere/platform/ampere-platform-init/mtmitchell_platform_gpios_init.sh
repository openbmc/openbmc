#!/bin/bash

# shellcheck disable=SC2154
# shellcheck source=/dev/null

source /usr/sbin/gpio-lib.sh

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
    cnt=10
    pgood=""
    while [ $cnt -gt 0 ];
    do
        pgood=$(busctl get-property org.openbmc.control.Power /org/openbmc/control/power0 org.openbmc.control.Power pgood | cut -d' ' -f2)
        if [[ "$pgood" != '' ]]; then
            break;
        fi
        cnt=$(( cnt - 1 ))
        sleep 1
    done

    if [ "$pgood" == '1' ]; then
        echo "PSU is on. Setting PSON to 0"
        gpio_name_set power-chassis-control 0
    else
        echo "pgood D-Bus property response as 0. PSU is off."
        # for unknown reason when stress reboot bmc power-control.exe detect power-chassis-good is 1 (power on)
        # But "busctl get-property org.openbmc.control.Power /org/openbmc/control/power0 org.openbmc.control.Power pgood" responses 0 (power off)
        # Add sleep 3 seconds after the pgood dbus reponse (power off) and recheck the power-chassis-good to confirm about the PSU power state
        sleep 3
        pgood=$(gpio_name_get power-chassis-good)
        if [ "$pgood" == '0' ]; then
            echo "power-chassis-good reponse as 0. Confirm PSU is off. Setting PSON to 1."
            gpio_name_set power-chassis-control 1
        fi
    fi
    gpio_name_set host0-sysreset-n 1

    # gpio-leds is controlling bmc-ready, not by gpio
    echo 1 > /sys/class/leds/bmc-ready/brightness

    echo "Set default FAN speed to 60%"
    for filename in /sys/class/hwmon/*/pwm*
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

export input_gpios_in_ac=(
    # add device enable, mux setting, device select gpios
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
)

export output_low_gpios_in_bmc_reboot=(
    "rtc-battery-voltage-read-enable"
    "s0-rtc-lock"
    "hpm-fw-recovery"
    "led-fault"
    "spi-nor-access"
    "host0-special-boot"
)

export input_gpios_in_bmc_reboot=(
    "s0-vrd-fault-n"
    "s1-vrd-fault-n"
    "irq-n"
    "presence-ps0"
    "presence-ps1"
    "hsc-12vmain-alt2-n"
    "eth-phy-int-n"
    "s0-pcp-oc-warn-n"
    "s1-pcp-oc-warn-n"
    "cpu-bios-recover"
    "s0-heartbeat"
    "hs-scout-proc-hot"
    "s0-vr-hot-n"
    "s1-vr-hot-n"
    "hsc-12vmain-alt1-n"
    "power-chassis-good"
    "s0-ddr-save"
    "soc-spi-nor-access"
    "presence-cpu0"
    "jtag-dbgr-prsnt-n"
    "ps0-ac-loss-n"
    "ps1-ac-loss-n"
    "s1-ddr-save"
    "sys-pgood"
    "presence-cpu1"
    "s0-fault-alert"
    "s0-sys-auth-failure-n"
    "host0-ready"
    "ocp-pgood"
    "s1-fault-alert"
    "s1-fw-boot-ok"
    "s0-spi-auth-fail-n"
    "s1-sys-auth-failure-n"
    "cpld-s1-spi-auth-fail-n"
    "ps0-pgood"
    "ps1-pgood"
    "s0-soc-pgood"
)
