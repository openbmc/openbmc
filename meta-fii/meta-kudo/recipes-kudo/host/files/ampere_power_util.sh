#!/bin/bash

# Provide source directive to shellcheck.
# shellcheck source=meta-fii/meta-kudo/recipes-kudo/kudo-fw-utility/kudo-fw/kudo-lib.sh
source /usr/libexec/kudo-fw/kudo-lib.sh

# Usage of this utility
function usage() {
  echo "usage: power-util mb [on|off|graceful_shutdown|host_reset|host_cycle|shutdown_ack|hotswap|power_button]";
}

hotswap() {
  kudo.sh rst hotswap
}

force_off() {
  echo "Powering down Server"

  set_gpio_ctrl 203 out 1
  sleep 6
  set_gpio_ctrl 203 out 0
}

power_off() {
  busctl set-property xyz.openbmc_project.Watchdog /xyz/openbmc_project/watchdog/host0 xyz.openbmc_project.State.Watchdog ExpireAction s xyz.openbmc_project.State.Watchdog.Action.None
  busctl set-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis RequestedPowerTransition s xyz.openbmc_project.State.Chassis.Transition.Off
}

power_on() {
  echo "Powering on Server"

  set_gpio_ctrl 203 out 1
  sleep 1
  set_gpio_ctrl 203 out 0
  busctl set-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis RequestedPowerTransition s xyz.openbmc_project.State.Chassis.Transition.On
}

power_status() {
  st=$(busctl get-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis CurrentPowerState | cut -d "." -f6)
  if [ "${st}" == "On\"" ]; then
  echo "on"
  else
  echo "off"
  fi
}

host_status() {
  BOOT_OK=$(get_gpio_ctrl 194)
  S5_N=$(get_gpio_ctrl 204)
  if [ "$S5_N" == 1 ] || [ "$BOOT_OK" == 1 ]; then
    echo "on"
  else
    echo "off"
  fi
}

timestamp() {
  date +"%s" # current time
}

graceful_shutdown() {
  if [ -f "/run/openbmc/host@0-request" ]; then
    echo "Shutdown host immediately"
    power_off
  else
    echo "Triggering graceful shutdown"
    mkdir /run/openbmc
    timestamp > "/run/openbmc/host@0-shutdown-req-time"
    set_gpio_ctrl 70 out 0
    sleep 3
    set_gpio_ctrl 70 out 1
  fi
}

host_reset() {
  if [ "$(host_status)" == "on" ]; then
    echo "Triggering sysreset pin"
    busctl set-property xyz.openbmc_project.Watchdog /xyz/openbmc_project/watchdog/host0 xyz.openbmc_project.State.Watchdog ExpireAction s xyz.openbmc_project.State.Watchdog.Action.None
    set_gpio_ctrl 65 out 0
    sleep 1
    set_gpio_ctrl 65 out 1
  else
    echo "Host is off, cannot reset."
  fi
}

host_cycle() {
  echo "DC cycling host"
  force_off
  sleep 2
  power_on
}

shutdown_ack() {
  echo "Receive shutdown ACK triggered"
  power_off

  if [ -f "/run/openbmc/host@0-shutdown-req-time" ]; then
    rm -rf "/run/openbmc/host@0-shutdown-req-time"
  fi
}

power_button() {
  echo "Power button trigger event."
  current_time="$(timestamp)"
  if [ -f "/run/openbmc/power-button" ]; then
    echo "Power button released"
    press_time="$(cat /run/openbmc/power-button)"
    if [[ "$current_time" -le "(($press_time + 1))" ]]; then
      power_on
    elif [[ "$current_time" -ge "(($press_time + 5))" ]]; then
      power_off
    else
      echo "Button press did not match interval."
    fi
    rm "/run/openbmc/power-button"
  else
    echo "Power button pressed"
    timestamp > "/run/openbmc/power-button"
  fi
}

if [ $# -lt 2 ]; then
  echo "Total number of parameter=$#"
  echo "Insufficient parameter"
  usage;
  exit 0;
fi

if [ "$1" != "mb" ]; then
  echo "Invalid parameter1=$1"
  usage;
  exit 0;
fi

if [ "$2" = "on" ]; then
  sleep 3
  if [ "$(power_status)" == "off" ]; then
    power_on
  fi
elif [ "$2" = "off" ]; then
  if [ "$(power_status)" == "on" ]; then
    power_off
    sleep 6
    if [ "$(host_status)" == "on" ]; then
      force_off
    fi
  fi
elif [ "$2" == "hotswap" ]; then
  hotswap
elif [ "$2" == "graceful_shutdown" ]; then
  graceful_shutdown
elif [ "$2" == "host_reset" ]; then
  host_reset
elif [ "$2" == "host_cycle" ]; then
  host_cycle
elif [ "$2" == "shutdown_ack" ]; then
  shutdown_ack
elif [ "$2" == "power_button" ]; then
  power_button
else
  echo "Invalid parameter2=$2"
  usage;
fi

exit 0;
