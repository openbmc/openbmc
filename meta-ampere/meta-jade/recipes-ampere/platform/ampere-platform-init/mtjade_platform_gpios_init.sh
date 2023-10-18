#!/bin/bash

function pre-platform-init() {
    echo "Do pre platform init"
}


function post-platform-init() {
    echo "Do post platform init"
}

export output_high_gpios_in_ac=(
    # add device enable, mux setting, device select gpios
    "ext-hightemp-n"
    "vr-pmbus-sel-n"
    "i2c6-reset-n"
    "i2c-backup-sel"
    "power-chassis-control"
    "host0-shd-req-n"
    "host0-sysreset-n"
    "s0-spi-auth-fail-n"
)

export output_low_gpios_in_ac=(
    # add device enable, mux setting, device select gpios
    "ocp-main-pwren"
    "spi0-program-sel"
    "spi0-backup-sel"
    "s0-rtc-lock"
    "host0-special-boot"
    "s1-special-boot"
)

export input_gpios_in_ac=(
    # add device enable, mux setting, device select gpios
)

export output_high_gpios_in_bmc_reboot=(
    "bmc-vga-en-n"
)

export output_low_gpios_in_bmc_reboot=(
    "spi0-backup-sel"
)

export input_gpios_in_bmc_reboot=(
    "s0-i2c9-alert-n"
    "s1-i2c9-alert-n"
    "s1-i2c9-alert-n"
    "s0-vr-hot-n"
    "s1-vr-hot-n"
)
