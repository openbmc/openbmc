#!/bin/bash

# Purpose:
#     The purpose of the script is to change the CB/CL ratio setting of the hotswap controller from 1.9x to 3.9x

I2C_BUS=11
CHIP_ADDR=0x15
GPIO_ID=91   #Revision ID
GPIO_BasePath=/sys/class/gpio

function set_hotswap_reg()
{
    #set reg "0xd9" bit 3 to 1
    i2cset -f -y $I2C_BUS $CHIP_ADDR 0xd9 0x08
}

function get_hotswap_value()
{
    #get the value of reg "0xd9", return value should be "0x08"
    echo "$(i2cget -f -y $I2C_BUS $CHIP_ADDR 0xd9)"
}

function export_gpio()
{
    if [ -d "$GPIO_BasePath/gpio$GPIO_ID" ]; then
        echo "gpio$GPIO_ID folder exist, skip export."
    else
        echo "Export gpio$GPIO_ID..."
        echo $GPIO_ID > $GPIO_BasePath/export
    fi
}

function get_gpio_value()
{
    echo "$(cat $GPIO_BasePath/gpio$GPIO_ID/value)"
}

function setting_hotswap()
{
    echo "setting hotswap controller..."
    set_hotswap_reg

    for i in {0..3};
    do
        if [ "$i" == "3" ];then
            echo "change hotswap controller setting failed after retry 3 times."
        else
            hotswap_value=$(get_hotswap_value)
            echo "get hotswap controller return value : $hotswap_value"
            if [ "$hotswap_value" == "0x08" ];then
                echo "change hotswap controller setting success."
                break;
            else
                echo "hotswap controller setting failed, retry $i times..."
            fi
        fi
    done
}

export_gpio
gpio_value=$(get_gpio_value)
if [ "$gpio_value" == "1" ];then
    echo "gpio$GPIO_ID value is: $gpio_value, setting hotswap."
    setting_hotswap
else
    echo "gpio$GPIO_ID value is: $gpio_value, no need to set hotswap."
fi