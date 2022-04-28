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
    # gpio-leds is controlling bmc-ready, not by gpio
    echo 1 > /sys/class/leds/bmc-ready/brightness

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
    "vrd-sel"
    "spd-sel"
    "ext-high-temp-n"
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
    "s0-pcp-oc-warn-n"
    "s1-pcp-oc-warn-n"
    "cpu-bios-recover"
    "s0-heartbeat"
    "hs-scout-proc-hot"
    "s0-vr-hot-n"
    "s1-vr-hot-n"
    "hsc-12vmain-alt1-n"
    "power-chassis-good"
    "power-button"
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
    "reset-button"
    "ps0-pgood"
    "ps1-pgood"
    "s0-soc-pgood"
)
