#!/bin/bash
# Usage of this utility
function set_gpio_active_low() {
  if [ $# -ne 2 ]; then
    echo "set_gpio_active_low: need both GPIO# and initial level";
    return;
  fi

  if [ ! -d /sys/class/gpio/gpio$1 ]; then
    echo $1 > /sys/class/gpio/export
  fi
  echo $2 > /sys/class/gpio/gpio$1/direction

  if [ -d /sys/class/gpio/gpio$1 ]; then
    echo $1 > /sys/class/gpio/unexport
  fi
}

GPIO_OCP_AUX_PWREN=139
GPIO_OCP_MAIN_PWREN=140

GPIO_BASE=$(cat /sys/class/gpio/gpio*/base)

function usage() {
  echo "usage: power-util mb [on|off|status|cycle|reset|graceful_shutdown|graceful_reset|force_reset]";
}

power_off() {
  echo "Shutting down Server $2"
  set_gpio_active_low $((${GPIO_BASE} + ${GPIO_OCP_AUX_PWREN})) high
  set_gpio_active_low $((${GPIO_BASE} + ${GPIO_OCP_MAIN_PWREN})) low
  busctl set-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis RequestedPowerTransition s xyz.openbmc_project.State.Chassis.Transition.Off
}

power_on() {
  echo "Powering on Server $2"
  set_gpio_active_low $((${GPIO_BASE} + ${GPIO_OCP_AUX_PWREN})) high
  set_gpio_active_low $((${GPIO_BASE} + ${GPIO_OCP_MAIN_PWREN})) high
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

timestamp() {
  date +"%s" # current time
}

shutdown_ack() {
  echo "Receive shutdown ACK triggered"
  power_off

  current_time="$(timestamp)"
  if [ -f "/run/openbmc/host@0-shutdown-req-time" ]; then
    request_time="$(cat /run/openbmc/host@0-shutdown-req-time)"
    # shutdow request must be done in 30s
    if [[ "$current_time" -lt "$request_time + 30" ]]; then
      power_off
      sleep 5s
      if [ -f "/run/openbmc/host@0-graceful-reset-time" ]; then
          req_reset="$(cat /run/openbmc/host@0-graceful-reset-time)"
          if [[ "$current_time" -lt "$req_reset + 45" ]]; then
            echo "powering on because of reset request"
            power_on
            rm -rf "/run/openbmc/host@0-graceful-reset-time"
          fi
      fi
    fi
    rm -rf "/run/openbmc/host@0-shutdown-req-time"
  fi
}

graceful_shutdown() {
  # the host@0-request created by ipmi, to indicate shutdown immediately
  if [ -f "/run/openbmc/host@0-request" ]; then
    echo "shutdown host immediately"
    power_off
  else
    echo "Triggering graceful shutdown"
    echo "$(timestamp)" > "/run/openbmc/host@0-shutdown-req-time"
    gpioset -l 0 49=1
    sleep 1s
    gpioset -l 0 49=0
    sleep 30s
  fi
}

graceful_reset() {
  echo "$(timestamp)" > "/run/openbmc/host@0-graceful-reset-time"
  graceful_shutdown
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

# check if power guard enabled
dir="/run/systemd/system/"
file="reboot-guard.conf"
units=("reboot" "poweroff" "halt")
for unit in "${units[@]}"; do
  if [ -f ${dir}${unit}.target.d/${file} ]; then
    echo "PowerGuard enabled, cannot do power control, exit!!!"
    exit -1
  fi
done

if [ ! -d "/run/openbmc/" ]; then
  mkdir -p "/run/openbmc/"
fi

if [ $2 = "on" ]; then
  rm -rf /run/openbmc/*
  if [ $(power_status) == "off" ]; then
    power_on
  fi
elif [ $2 = "off" ]; then
  if [ $(power_status) == "on" ]; then
    power_off
  else
    rm -rf /run/openbmc/host@0-request
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
  if [ $(power_status) == "on" ]; then
    graceful_shutdown
  fi
elif [ $2 == "graceful_reset" ]; then
  if [ $(power_status) == "on" ]; then
      graceful_reset
  fi
elif [ $2 == "shutdown_ack" ]; then
  shutdown_ack
elif [ $2 == "status" ]; then
  power_status
elif [ $2 == "force_reset" ]; then
  force_reset
else
  echo "Invalid parameter2=$2"
  usage;
fi

exit 0;
