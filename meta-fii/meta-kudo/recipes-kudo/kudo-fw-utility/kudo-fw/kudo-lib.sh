#!/bin/bash

# set_gpio_ctrl
# pin #, direction, high(1)/low(0)
function set_gpio_ctrl() {
  echo "$1" > /sys/class/gpio/export
  echo "$2" > /sys/class//gpio/gpio"$1"/direction
  echo "$3" > /sys/class/gpio/gpio"$1"/value
  echo "$1" > /sys/class/gpio/unexport
  sleep 1
}

# get_gpio_ctrl
# pin #
function get_gpio_ctrl() {
  echo "$1" > /sys/class/gpio/export
  cat /sys/class/gpio/gpio"$1"/value
  echo "$1" > /sys/class/gpio/unexport
}
