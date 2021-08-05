#!/bin/bash
# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This is intended to be used as a library for managing gpio line values.
# Executing this directly will do nothing.
[ -n "${gpio_ctrl_init-}" ] && return

# Map names of GPIOs to GPIO number
# This maps a schematic name to a gpiochip and line at a specific offset
declare -A GPIO_NAMES_TO_SCM=(
  # Examples
  #['SYS_RESET_N']='label=/pinctrl@f0800000/gpio@f0012000 21'
  #['PWRBTN_N']='label=/pinctrl@f0800000/gpio@f0012000 23'
  #['PCH_SLP_S5_R_N']='of_node=/ahb/apb/i2c@8e000/cpu_seq@6b 9'
  #['PCH_PWRGOOD_R']='of_node=/ahb/apb/i2c@8e000/cpu_seq@6b 6'
)

# Load configurations from a known location in the filesystem to populate
# named GPIOs
shopt -s nullglob
for conf in /usr/share/gpio-ctrl/conf.d/*.sh; do
  source "$conf"
done

declare -A gpio_sysfs_lookup_cache=()
declare -A gpio_lookup_cache=()

declare -A gpio_init=()

##################################################
# Looks up the sysfs GPIO number
# Arguments:
#   $1: GPIO name
# Return:
#   0 if success, non-zero if error
#   stdout: The GPIO number
##################################################
gpio_name_to_num() {
  local name="$1"

  if [ -n "${gpio_lookup_cache["$name"]+1}" ]; then
    echo "${gpio_lookup_cache["$name"]}"
    return 0
  fi

  local scm="${GPIO_NAMES_TO_SCM["$name"]-}"
  if [ -z "$scm" ]; then
    echo "Missing gpio definition: $name" >&2
    return 1
  fi
  local id="${scm% *}"
  local type="${id%=*}"
  local val="${id#*=}"
  local offset="${scm#* }"

  local sysfs
  if [ -n "${gpio_sysfs_lookup_cache["$id"]+1}" ]; then
    sysfs="${gpio_sysfs_lookup_cache["$id"]}"
  else
    case "$type" in
    label)
      if ! sysfs="$(grep -xl "$val" /sys/class/gpio/gpiochip*/label)"; then
        echo "Failed to find gpiochip: $val" >&2
        return 1
      fi
      sysfs="${sysfs%/label}"
      ;;
    of_node)
      for sysfs in $(echo /sys/class/gpio/gpiochip*); do
        local link
        # Ignore errors because not all devices have of_nodes
        link="$(readlink -f "$sysfs/device/of_node" 2>/dev/null)" || continue
        [ "${link#/sys/firmware/devicetree/base}" = "$val" ] && break
        sysfs=
      done
      if [ -z "$sysfs" ]; then
        echo "Failed to find gpiochip: $val" >&2
        return 1
      fi
      ;;
    *)
      echo "Invalid GPIO type $type" >&2
      return 1
      ;;
    esac
    gpio_sysfs_lookup_cache["$id"]="$sysfs"
  fi

  local ngpio=$(cat "$sysfs"/ngpio)
  if (( ngpio <= offset )); then
    echo "$name with gpiochip $sysfs only has $ngpio but wants $offset" >&2
    return 1
  fi

  gpio_lookup_cache["$name"]=$(( $(cat "$sysfs"/base) + offset ))
  echo "${gpio_lookup_cache["$name"]}"
}


##################################################
# Populates the GPIO lookup cache
# Most calls to gpio_name_to_num that would
# normally cache the sysfs lookups for gpios run
# inside subshells. This prevents them from
# populating a global cache and greatly speeding
# up future lookups. This call allows scripts to
# populate the cache prior to looking up gpios.
##################################################
gpio_build_cache() {
  local timeout="${1-0}"
  shift
  local gpios=("$@")

  if (( ${#gpios[@]} == 0 )); then
    gpios="${!GPIO_NAMES_TO_SCM[@]}"
  fi

  local deadline=$(( timeout + SECONDS ))
  local name
  for name in "${gpios[@]}"; do
    while true; do
      gpio_name_to_num "$name" >/dev/null && break
      if (( deadline <= SECONDS )); then
        echo "Timed out building gpio cache" >&2
        return 1
      fi
    done
  done
}

##################################################
# Initializes the gpio state
# This operation is idempotent and can be applied
# repeatedly to the same gpio. It will make sure the
# gpio ends up in the initialized state even if it
# was.
# Arguments:
#   $1: GPIO name
# Return:
#   0 if success, non-zero if error
##################################################
gpio_init_() {
  local name="$1"
  local num="$2"

  if [ -n "${gpio_init["$name"]+1}" ]; then
    return 0
  fi

  if [ ! -e "/sys/class/gpio/gpio$num" ]; then
    if ! echo "$num" >'/sys/class/gpio/export'; then
      echo "Failed to export $name gpio$num" >&2
      return 1
    fi
  fi
  local active_low=0
  if [[ "${name%_N}" != "$name" ]]; then
    active_low=1
  fi
  if ! echo "$active_low" >"/sys/class/gpio/gpio$num/active_low"; then
    echo "Failed to set active_low for $name gpio$num" >&2
    return 1
  fi
  gpio_init["$name"]=1
}
gpio_init() {
  local name="$1"

  # Ensure the cache is updated by not running in a subshell
  gpio_name_to_num "$name" >/dev/null || return

  gpio_init_ "$name" "$(gpio_name_to_num "$name")"
}

##################################################
# Sets the output GPIO value.
# Arguments:
#   $1: GPIO name
#   $2: GPIO value, "1" or "0"
# Return:
#   0 if success, non-zero if error
##################################################
gpio_set_value_() {
  local name="$1"
  local num="$2"
  local val="$3"

  gpio_init_ "$name" "$num" || return
  if ! echo out >"/sys/class/gpio/gpio$num/direction"; then
    echo "Failed to set output for $name gpio$num" >&2
    return 1
  fi
  if ! echo "$val" >"/sys/class/gpio/gpio$num/value"; then
    echo "Failed to set $name gpio$num = $val" >&2
    return 1
  fi
}
gpio_set_value() {
  local name="$1"
  local val="$2"

  # Ensure the cache is updated by not running in a subshell
  gpio_name_to_num "$name" >/dev/null || return

  gpio_set_value_ "$name" "$(gpio_name_to_num "$name")" "$val"
}

##################################################
# Get GPIO value
# Arguments:
#   $1: GPIO name
# Return:
#   0 if success, non-zero if error
#   stdout: The value of the gpio
##################################################
gpio_get_value_() {
  local name="$1"
  local num="$2"

  gpio_init_ "$name" "$num" || return
  if ! cat "/sys/class/gpio/gpio$num/value"; then
    echo "Failed to get $name gpio$num value" >&2
    return 1
  fi
}
gpio_get_value() {
  local name="$1"

  # Ensure the cache is updated by not running in a subshell
  gpio_name_to_num "$name" >/dev/null || return

  gpio_get_value_ "$name" "$(gpio_name_to_num "$name")"
}

gpio_ctrl_init=1
return 0 2>/dev/null
echo "gpio-ctrl is a library, not executed directly" >&2
exit 1
