#!/bin/bash
# Usage of this utility
function usage() {
  echo "usage: power-util mb [on|status|cycle|reset|graceful_reset|force_reset|soft_off]";
}

power_status() {
  st=$(busctl get-property xyz.openbmc_project.State.Chassis /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis CurrentPowerState | cut -d"." -f6)
  if [ "$st" == "On\"" ]; then
  echo "on"
  else
  echo "off"
  fi
}

shutdown_ack() {
  if [ -f "/run/openbmc/host@0-softpoweroff" ]; then
    echo "Receive shutdown ACK triggered after softportoff the host."
    touch /run/openbmc/host@0-softpoweroff-shutdown-ack
  else
    echo "Receive shutdown ACK triggered"
<<<<<<< HEAD
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
=======
    sleep 3
    systemctl start obmc-chassis-poweroff@0.target
>>>>>>> 397e033ef... meta-ampere: power control: refactor host power control
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
  if [ -f "/run/openbmc/host@0-softpoweroff" ]; then
    # In graceful host reset, after trigger os shutdown,
    # the phosphor-state-manager will call force-warm-reset
    # in this case the force_reset should wait for shutdown_ack from host
    cnt=30
    while [ $cnt -gt 0 ];
    do
      if [ -f "/run/openbmc/host@0-softpoweroff-shutdown-ack" ]; then
        break
      fi
      echo "Waiting for shutdown-ack count down $cnt"
      sleep 1
      cnt=$((cnt - 1))
    done
    # The host OS is failed to shutdown
    if [ $cnt == 0 ]; then
      echo "Shutdown-ack time out after 30s."
      exit 0
    fi
  fi
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

if [ $2 == "shutdown_ack" ]; then
  shutdown_ack
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
