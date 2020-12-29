#!/bin/bash
#
# PSU hard reset (power cycle) script.
#
# Power cycle the entire tray by setting the PSU hotswap reset (GPIO218) to high
#
# Global variable: PSU_HARDRESET_DELAY specifies the number of seconds to wait
# before pulling the trigger. If not specified or zero, the script power cycles
# immediately.

##################################################
# Stop the phosphor-hwmon daemon
# Return:
#   0 if success, non-zero if error
##################################################
stop_phosphor_hwmon() {
  if (( $# != 0 )); then
    echo 'Usage: stop_phosphor_hwmon' >&2
    return 1
  fi

  echo "Stopping phosphor-hwmon" >&2
  local srv='system-xyz.openbmc_project.Hwmon.slice'
  systemctl stop "${srv}"
}

main() {
  # Sleep PSU_HARDRESET_DELAY seconds
  local psu_delay=$((PSU_HARDRESET_DELAY))
  if ((psu_delay > 0)); then
    echo "Sleeping ${psu_delay} seconds before PSU hard reset!"
    sleep "${psu_delay}"
  fi

  # Stop phosphor-hwmon so that ADM1272 powercycle doesn't happen
  # in the middle of an i2c transaction and stuck the bus low
  stop_phosphor_hwmon

  gpioset gpiochip6 26=1
}

# Exit without running main() if sourced
return 0 2>/dev/null

main "$@"
