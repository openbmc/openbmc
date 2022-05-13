#!/bin/bash

# Set all output GPIOs as such and drive them with reasonable values.
function set_gpio_out()
{
    echo $1 > /sys/class/gpio/export
    echo $2 > /sys/class/gpio/gpio$1/direction
}

# Set Reset ping GPIO 504 and Power pin GPIO 505 as output high with active_low
set_gpio_out 451 high
set_gpio_out 452 high

exit 0;
