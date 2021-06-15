#!/bin/bash

U2_PRESENT=( 148 149 150 151 152 153 154 155 )
POWER_U2=( 195 196 202 199 198 197 127 126 )
PWRGD_U2=( 161 162 163 164 165 166 167 168 )
RST_BMC_U2=( 72 73 74 75 76 77 78 79 )
PLUGGED=0
I2C_BUS=8
CHIP_ADDR=0x68
CLOCK_GEN_VALUE=$(i2cget -y $I2C_BUS $CHIP_ADDR 0 i 2|cut -f3 -d' ')

function set_gpio_direction()
{
    #$1 gpio pin, $2 'in','high','low'
    echo $2 > /sys/class/gpio/gpio$1/direction
}

function read_gpio_input()
{
    #$1 read input gpio pin
    echo $(cat /sys/class/gpio/gpio$1/value)
}

function enable_nvme_power()
{
    set_gpio_direction "${POWER_U2[$1]}" "high"
    sleep 0.04
    check_powergood $1
}

function check_powergood()
{
    if [ $(read_gpio_input ${PWRGD_U2[$1]}) == 1 ];then
        sleep 0.005
        update_clock_gen_chip_register $1 1
        sleep 0.1
        set_gpio_direction "${RST_BMC_U2[$1]}" "high"
    else
        disable_nvme_power $1
    fi
}

function disable_nvme_power()
{
    set_gpio_direction "${RST_BMC_U2[$1]}" "low"
    sleep 0.1
    update_clock_gen_chip_register $1 0
    sleep 0.005
    set_gpio_direction "${POWER_U2[$1]}" "low"
}

function update_clock_gen_chip_register(){
    #$1 nvme slot number, $2 enable/disable
    update_value=$(printf '%x\n' "$((0x01 <<$1))")
    if [ $2 -eq 1 ];then
        CLOCK_GEN_VALUE=$(printf '0x%x\n' \
        "$(($CLOCK_GEN_VALUE | 0x$update_value))")
    else
        CLOCK_GEN_VALUE=$(printf '0x%x\n' \
        "$(($CLOCK_GEN_VALUE & ~0x$update_value))")
    fi
    i2cset -y $I2C_BUS $CHIP_ADDR 0 $CLOCK_GEN_VALUE s
}
