#!/bin/bash

# Set all output GPIOs as such and drive them with reasonable values.
function set_gpio_active_low() {
  if [ $# -ne 2 ]; then
    echo "set_gpio_active_low: need both GPIO# and initial level";
    return;
  fi

  echo $1 > /sys/class/gpio/export
  echo $2 > /sys/class/gpio/gpio$1/direction
}

GPIO_BASE=$(cat /sys/class/gpio/gpio*/base)

# FM_BMC_READY_N, GPIO S1, active low
set_gpio_active_low $((${GPIO_BASE} + 144 +1)) high

# FM_BMC_SSB_SMI_LPC_N, GPIO Q6, active low
set_gpio_active_low $((${GPIO_BASE} + 128 + 6)) high

# FP_PWR_BTN_PASS_R_N, GPIO E3, active low
set_gpio_active_low $((${GPIO_BASE} + 32 + 3)) high

# FP_PWR_GOOD, GPIO B6, active low
set_gpio_active_low $((${GPIO_BASE} + 8 + 6)) high

exit 0;
