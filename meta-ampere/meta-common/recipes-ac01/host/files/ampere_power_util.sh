#!/bin/bash
# Usage of this utility
function usage() {
  echo "usage: power-util mb [on|off|status|cycle|reset|graceful_shutdown|graceful_reset|force_reset]";
}

power_off() {
  echo "Shutting down Server $2"
  busctl set-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis RequestedPowerTransition s xyz.openbmc_project.State.Chassis.Transition.Off
}

power_on() {
  echo "Powering on Server $2"
  busctl set-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis RequestedPowerTransition s xyz.openbmc_project.State.Chassis.Transition.On
}

power_status() {
  st=$(busctl get-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis CurrentPowerState | cut -d"." -f6)
  if [ "$st" == "On\"" ]; then
  echo "on"
  else
  echo "off"
  fi
}

power_reset() {
  echo "Reset on server $2"
  busctl set-property xyz.openbmc_project.State.Host /xyz/openbmc_project/state/host0 xyz.openbmc_project.State.Host RequestedHostTransition s xyz.openbmc_project.State.Host.Transition.Reboot
}

graceful_shutdown() {
  if [ -f "/run/openbmc/host@0-request" ]; then
    echo "shutdown host immediately"
    power_off
  else
    echo "Triggering graceful shutdown"
    gpioset -l 0 49=1
    sleep 1
    gpioset -l 0 49=0
    sleep 30s
  fi
}

force_reset() {
  echo "Triggering sysreset pin"
  gpioset -l 0 91=1
  sleep 1
  gpioset -l 0 91=0
}

if [ $# -lt 2 ]; then
  echo "Total number of parameter=$#"
  echo "Insufficient parameter"
  usage;
  exit 0;
fi

if [ $1 != "mb" ]; then
  echo "Invalid parameter1=$1"
  usage;
  exit 0;
fi

if [ $2 = "on" ]; then
  if [ $(power_status) == "off" ]; then
    power_on
  fi
elif [ $2 = "off" ]; then
  if [ $(power_status) == "on" ]; then
    power_off
  fi
  # If any request of graceful reset, need to power on
  if [ -f "/run/openbmc/host@0-graceful-reset" ]; then
    sleep 20s
    power_on
    rm -f "/run/openbmc/host@0-graceful-reset"
  fi
elif [ $2 == "cycle" ]; then
  if [ $(power_status) == "on" ]; then
    echo "Powering off server"
    power_off
    sleep 20s
    power_on
  else
    echo "Host is already off, do nothing"
  fi
elif [ $2 == "reset" ]; then
  if [ $(power_status) == "on" ]; then
    power_reset
  else
    echo "ERROR: Server not powered on"
  fi
elif [[ $2 == "graceful_shutdown" ]]; then
  graceful_shutdown
elif [ $2 == "graceful_reset" ]; then
  mkdir -p "/run/openbmc/"
  touch "/run/openbmc/host@0-graceful-reset"
  graceful_shutdown
  sleep 20s
elif [ $2 == "status" ]; then
  power_status
elif [ $2 == "force_reset" ]; then
  force_reset
else
  echo "Invalid parameter2=$2"
  usage;
fi

exit 0;
