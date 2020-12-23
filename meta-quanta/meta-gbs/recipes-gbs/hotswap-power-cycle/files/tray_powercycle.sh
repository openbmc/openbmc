#!/bin/bash
#
# PSU hard reset (power cycle) script.
#
# Power cycle the entire tray by setting the PSU hotswap reset (GPIO218) to high
#
# Global variable: PSU_HARDRESET_DELAY specifies the number of seconds to wait
# before pulling the trigger. If not specified or zero, the script power cycles
# immediately.
main() {
  # Sleep PSU_HARDRESET_DELAY seconds
  local psu_delay=$((PSU_HARDRESET_DELAY))
  if ((psu_delay > 0)); then
    echo "Sleeping ${psu_delay} seconds before PSU hard reset!"
    sleep "${psu_delay}"
  fi

  gpioset gpiochip6 26=1
}

# Exit without running main() if sourced
return 0 2>/dev/null

main "$@"
