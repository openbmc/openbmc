#!/bin/bash

# Set all output GPIOs as such and drive them with reasonable values.
function set_gpio_active_low() {
  if [ $# -ne 2 ]; then
    echo "set_gpio_active_low: need both GPIO# and initial level";
    return;
  fi

  echo $1 > /sys/class/gpio/export
  echo 1 > /sys/class/gpio/gpio$1/active_low
  echo $2 > /sys/class/gpio/gpio$1/direction
}

GPIO_BASE=$(cat /sys/devices/platform/ahb/ahb:apb/1e780000.gpio/gpio/*/base)

# FM_BMC_READY_N, GPIO Q4, active low
set_gpio_active_low $((${GPIO_BASE} + 128 + 4)) high

# FM_BMC_SSB_SMI_LPC_N, GPIO Q6, active low
set_gpio_active_low $((${GPIO_BASE} + 128 + 6)) high

# FM_BMC_SYS_THROTTLE_N, GPIO A3, active low
set_gpio_active_low $((${GPIO_BASE} + 0 + 3)) high

# FM_BMC_SSB_SCI_LPC_N, GPIO E4, active low
set_gpio_active_low $((${GPIO_BASE} + 32 + 4)) high

# FP_PWR_BTN_PASS_R_N, GPIO D3, active low
set_gpio_active_low $((${GPIO_BASE} + 24 + 3)) high

exit 0;
