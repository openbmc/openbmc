#!/bin/sh -eu

function override_avs_settings()
{
    BUS=$1

    i2cset -y ${BUS} 0x44 0x00 0x00 b    # VCS
    i2cset -y ${BUS} 0x44 0x24 0x044C w  # VOUT_MAX 1100mV
    i2cset -y ${BUS} 0x44 0x40 0x0456 w  # VOUT_OV_FAULT_LIMIT 1110mV
    i2cset -y ${BUS} 0x44 0x25 0x0438 w  # VOUT_MARGING_HIGH 1080mV
    i2cset -y ${BUS} 0x44 0x26 0x03D4 w  # VOUT_MARGING_LOW 980mV
    i2cset -y ${BUS} 0x44 0x44 0x024E w  # VOUT_UV_FAULT_LIMIT 590mV
    i2cset -y ${BUS} 0x44 0x2B 0x0258 w  # VOUT_MIN 600mV

    i2cset -y ${BUS} 0x44 0x00 0x01 b    # VDD
    i2cset -y ${BUS} 0x44 0x24 0x044C w  # VOUT_MAX 1100mV
    i2cset -y ${BUS} 0x44 0x40 0x0456 w  # VOUT_OV_FAULT_LIMIT 1110mV
    i2cset -y ${BUS} 0x44 0x25 0x041A w  # VOUT_MARGING_HIGH 1050mV
    i2cset -y ${BUS} 0x44 0x26 0x03B6 w  # VOUT_MARGING_LOW 950mV
    i2cset -y ${BUS} 0x44 0x44 0x024E w  # VOUT_UV_FAULT_LIMIT 590mV
    i2cset -y ${BUS} 0x44 0x2B 0x0258 w  # VOUT_MIN 600mV

    i2cset -y ${BUS} 0x44 0x00 0xFF b   # All pages (VCS & VDD)
    i2cset -y ${BUS} 0x44 0x01 0xB0 b   # Enable

    i2cset -y ${BUS} 0x46 0x00 0x01 b    # VDN
    i2cset -y ${BUS} 0x46 0x01 0xB0 b    # Enable
}

override_avs_settings 4
override_avs_settings 5
