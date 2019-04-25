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

function set_gpio_active_high() {
  if [ $# -ne 2 ]; then
    echo "set_gpio_active_high: need both GPIO# and initial level";
    return;
  fi

  echo $1 > /sys/class/gpio/export
  echo 0 > /sys/class/gpio/gpio$1/active_low
  echo $2 > /sys/class/gpio/gpio$1/direction
}

GPIO_BASE=$(cat /sys/class/gpio/gpio*/base)

# SYS_MON_PWR_BTN, GPIO E2
set_gpio_active_high $((${GPIO_BASE} + 32 + 2)) in

# SYS_MON_RST_BTN, GPIO E3
set_gpio_active_high $((${GPIO_BASE} + 32 + 3)) in
# SYS_MON_PWR_GOOD, GPIO E5
set_gpio_active_high $((${GPIO_BASE} + 32 + 5)) in

# SYS_MON_PWROK, GPIO E6
set_gpio_active_high $((${GPIO_BASE} + 32 + 6)) in

# MGMT_ASSERT_PWR_BTN, GPIO M0
set_gpio_active_high $((${GPIO_BASE} + 96 + 0)) out

# MGMT_ASSERT_RST_BTN, GPIO M1
set_gpio_active_high $((${GPIO_BASE} + 96 + 1)) out

# MGMT_ASSERT_BMC_READY, GPIO M7
set_gpio_active_high $((${GPIO_BASE} + 96 + 7)) out

exit 0;
