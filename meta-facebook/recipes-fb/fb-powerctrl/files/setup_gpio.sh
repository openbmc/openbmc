#!/bin/bash

# Set all output GPIOs as such and drive them with reasonable values.
function set_gpio_active_low() {
  if [ $# -ne 2 ]; then
    echo "set_gpio_active_low: need both GPIO# and initial level";
    return;
  fi

  echo "$1" > /sys/class/gpio/export
  echo "$2" > "/sys/class/gpio/gpio$1/direction"
}

GPIO_BASE=$(cat /sys/class/gpio/gpio*/base)

# FM_BMC_READY_N, GPIO S1, active low
set_gpio_active_low $((GPIO_BASE + 144 +1)) low

# FP_PECI_MUX, active low
set_gpio_active_low $((GPIO_BASE + 212)) high

exit 0;
