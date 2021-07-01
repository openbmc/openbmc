#!/bin/bash
# Usage of this utility
function usage() {
  echo "usage: power-util mb [on|status|cycle|reset|graceful_reset|force_reset|soft_off]";
}

power_off() {
  echo "power_off"
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

timestamp() {
  date +"%s" # current time
}

shutdown_ack() {
  if [ -f "/run/openbmc/host@0-softpoweroff" ]; then
    echo "Receive shutdown ACK triggered after softportoff the host."
    touch /run/openbmc/host@0-softpoweroff-shutdown-ack
  else
    echo "Receive shutdown ACK triggered"
  fi
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

soft_off() {
  # Trigger shutdown_req
  touch /run/openbmc/host@0-softpoweroff
  gpioset -l 0 49=1
  sleep 1s
  gpioset -l 0 49=0

  # Wait for shutdown_ack from the host in 30 seconds
  cnt=30
  while [ $cnt -gt 0 ];
  do
    # Wait for SHUTDOWN_ACK and create the host@0-softpoweroff-shutdown-ack
    if [ -f "/run/openbmc/host@0-softpoweroff-shutdown-ack" ]; then
      break
    fi
    sleep 1
    cnt=$((cnt - 1))
  done
  # Softpoweroff is successed
  sleep 2
  rm -rf /run/openbmc/host@0-softpoweroff
  if [ -f "/run/openbmc/host@0-softpoweroff-shutdown-ack" ]; then
    rm -rf /run/openbmc/host@0-softpoweroff-shutdown-ack
  fi
  echo 0
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
elif [ $2 == "graceful_reset" ]; then
  mkdir -p "/run/openbmc/"
  touch "/run/openbmc/host@0-graceful-reset"
  graceful_shutdown
  sleep 20s
elif [ $2 == "status" ]; then
  power_status
elif [ $2 == "force_reset" ]; then
  force_reset
elif [ $2 == "soft_off" ]; then
  ret=$(soft_off)
  if [ $ret == 0 ]; then
    echo "The host is already softoff"
  else
    echo "Failed to softoff the host"
  fi
  exit $ret;
else
  echo "Invalid parameter2=$2"
  usage;
fi

exit 0;
