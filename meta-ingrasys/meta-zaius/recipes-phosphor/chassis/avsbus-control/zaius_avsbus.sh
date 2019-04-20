#!/bin/sh -e
# AVSBus control for PMBUS voltage regulator modules (VRMs)
# Switches output voltage target between
# - VOUT_COMMAND register (AVSBus disabled, default on Zaius)
# - AVSBus target output (AVSBus enabled, voltage set by host)

cpu0_i2c_bus="7"
cpu1_i2c_bus="8"
buses="$cpu0_i2c_bus $cpu1_i2c_bus"
vdd_i2c_addr_page="0x60:0x01"
vdn_i2c_addr_page="0x64:0x01"
vcs_i2c_addr_page="0x64:0x00"
addrs_pages="$vdd_i2c_addr_page $vdn_i2c_addr_page $vcs_i2c_addr_page"

i2c_path="/sys/bus/i2c/devices/"

# Usage: vrm_avs_enable <bus> <i2c_address> <page>
# Initializes the AVSBus VOUT setpoint to the value in PMBus VOUT_COMMAND
vrm_avs_enable()
{
    echo Enabling AVSBus on bus $1 VRM @$2 rail $3...
    echo 1 > $(echo ${i2c_path}/$1-$(printf "%04x" $2)/hwmon/hwmon*/avs$(printf "%d" $3)_enable)
}

# Usage: vrm_avs_disable <bus> <i2c_address> <page>
# Sets OPERATION PMBUS register to
# - Enable/Disable: On
# - VOUT Source: VOUT_COMMAND
# - AVSBus Copy: VOUT_COMMAND remains unchanged
vrm_avs_disable()
{
    echo Disabling AVSBus on bus $1 VRM @$2 rail $3...
    echo 0 > $(echo ${i2c_path}/$1-$(printf "%04x" $2)/hwmon/hwmon*/avs$(printf "%d" $3)_enable)
}

# Usage: for_each_rail <command>
# <command> will be invoked with <bus> <i2c_address> <page>
for_each_rail()
{
    for bus in $buses
    do
        for addr_page in $addrs_pages
        do
            $1 $bus `echo $addr_page | tr : " "`
        done
    done
}

if [ "$1" == "enable" ]
then
    for_each_rail vrm_avs_enable
elif [ "$1" == "disable" ]
then
    for_each_rail vrm_avs_disable
else
    echo "\"$0 <enable|disable>\" to control whether VRMs use AVSBus"
    echo "\"$0 <vdn_max>\" to set VDN rails VOUT_MAX to 1.1V"
fi
