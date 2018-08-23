#!/bin/sh -e
# Read and control VCS rails by sending the UCD power sequencer I2C commands.
# This script assumes that the UCD is controlling VCS rails as GPIOs 5 and 6.
# Also assumes that those GPIOs are already enabled.

ucd_bus="0"
ucd_addr="0x64"
ucd_path="/sys/bus/i2c/drivers/ucd9000"
ucd_driver="${ucd_bus}-00${ucd_addr#0x}"
ucd_retries="5"

retry()
{
    local i=0
    until [ $i -ge $ucd_retries ]; do
        i=$((i+1))
        retry_output=`$@` && break
    done
    local ret=$?
    if [ $i -eq $ucd_retries ]; then exit $ret; fi
}

# Usage: ucd_get address
# Result stored in $ucd_reg
ucd_get()
{
    retry i2cget -f -y $ucd_bus $ucd_addr $1 b
    ucd_reg=$retry_output
}

# Usage: ucd_get address value
ucd_set()
{
    retry i2cset -f -y $ucd_bus $ucd_addr $1 $2 b
}

vcs_set_gpios()
{
    echo -e "\tSetting UCD GPIO 5 to $1"
    ucd_set 0xFA 5
    ucd_set 0xFB $1
    ucd_set 0xFB $1
    echo -e "\tSetting UCD GPIO 6 to $1"
    ucd_set 0xFA 6
    ucd_set 0xFB $1
    ucd_set 0xFB $1
}

vcs_get()
{
    echo Reading VCS settings
    ucd_set 0xFA 5
    ucd_get 0xFB
    local val=`echo $ucd_reg | grep -i -c 0x0f`
    echo -e "\tUCD GPIO 5 state=$val"
    ucd_set 0xFA 6
    ucd_get 0xFB
    local val=`echo $ucd_reg | grep -i -c 0x0f`
    echo -e "\tUCD GPIO 6 state=$val"
}


if [ "$1" == "on" ]; then
    echo Turning on VCS
    vcs_set_gpios 0x7
elif [ "$1" == "off" ]; then
    echo Turning off VCS
    vcs_set_gpios 0x3
else
    vcs_get
    echo "$0 <on|off>" to set state
fi
