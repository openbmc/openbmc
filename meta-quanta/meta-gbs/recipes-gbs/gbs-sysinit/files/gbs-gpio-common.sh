#!/bin/bash
# Copyright 2020 Google LLC
# Copyright 2020 Quanta Computer Inc.
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

#
# Common GPIO functions.

# Map names of GPIOs to GPIO number
declare -A GPIO_NAMES_TO_NUMBER=(
  ['RST_BMC_PHY_N']=15
  ['BMC_BRD_REV_ID6']=37
  ['BMC_BRD_REV_ID5']=38
  ['BMC_BRD_SKU_ID3']=39
  ['BMC_BRD_SKU_ID2']=40
  ['FM_BMC_CPU_UART_EN']=76
  ['RST_BMC_RSMRST_N']=87
  ['RST_KBRST_BMC_CPLD_N']=94
  ['FAN_BRD_REV_ID0']=122
  ['FAN_BRD_REV_ID1']=123
  ['HSBP_BRD_REV_ID3']=124
  ['HSBP_BRD_REV_ID2']=125
  ['HSBP_BRD_REV_ID1']=126
  ['BMC_BRD_REV_ID0']=136
  ['BMC_BRD_REV_ID1']=137
  ['BMC_BRD_REV_ID2']=138
  ['BMC_BRD_REV_ID3']=139
  ['BMC_BRD_REV_ID4']=140
  ['BMC_BRD_SKU_ID0']=141
  ['BMC_BRD_SKU_ID1']=142
  ['HDD_PRSNT_N']=160
  ['SPI_SW_SELECT']=169
  ['BMC_BRD_REV_ID7']=194
  ['HSBP_BRD_REV_ID0']=196
)

# 1 is active_low 0 is active_high
declare -A GPIO_NAMES_TO_ACTIVE_LOW=(
  ['RST_BMC_PHY_N']=1
  ['BMC_BRD_REV_ID6']=0
  ['BMC_BRD_REV_ID5']=0
  ['BMC_BRD_SKU_ID3']=0
  ['BMC_BRD_SKU_ID2']=0
  ['FM_BMC_CPU_UART_EN']=0
  ['RST_BMC_RSMRST_N']=1
  ['RST_KBRST_BMC_CPLD_N']=1
  ['FAN_BRD_REV_ID0']=0
  ['FAN_BRD_REV_ID1']=0
  ['HSBP_BRD_REV_ID3']=0
  ['HSBP_BRD_REV_ID2']=0
  ['HSBP_BRD_REV_ID1']=0
  ['BMC_BRD_REV_ID0']=0
  ['BMC_BRD_REV_ID1']=0
  ['BMC_BRD_REV_ID2']=0
  ['BMC_BRD_REV_ID3']=0
  ['BMC_BRD_REV_ID4']=0
  ['BMC_BRD_SKU_ID0']=0
  ['BMC_BRD_SKU_ID1']=0
  ['HDD_PRSNT_N']=1
  ['SPI_SW_SELECT']=0
  ['BMC_BRD_REV_ID7']=0
  ['HSBP_BRD_REV_ID0']=0
)

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
init_gpio() {
  if (( $# != 1 )); then
    echo "Usage: init_gpio name" >&2
    return 1
  fi

  local name=$1

  local number=${GPIO_NAMES_TO_NUMBER["${name}"]}
  if [[ -z ${number} ]]; then
    echo "Missing number info for: ${name}" >&2
    return 2
  fi

  local active_low=${GPIO_NAMES_TO_ACTIVE_LOW["${name}"]}
  if [[ -z ${active_low} ]]; then
    echo "Missing active_low info for: ${name}" >&2
    return 2
  fi

  if [[ ! -e "/sys/class/gpio/gpio${number}" ]]; then
    echo "${number}" >'/sys/class/gpio/export'
  fi
  echo "${active_low}" >"/sys/class/gpio/gpio${number}/active_low"
}

##################################################
# Set output GPIO direction.
# Arguments:
#   $1: GPIO name
#   $2: GPIO direction, "high" or "low"
# Return:
#   0 if success, non-zero if error
##################################################
set_gpio_direction() {
  if (( $# != 2 )); then
    echo 'Usage: set_gpio_direction name direction' >&2
    return 1
  fi

  local name=$1
  local direction=$2

  local number=${GPIO_NAMES_TO_NUMBER["${name}"]}
  if [[ -z ${number} ]]; then
    echo "Missing number info for: ${name}" >&2
    return 2
  fi

  init_gpio "${name}" || return
  echo "${direction}" >"/sys/class/gpio/gpio${number}/direction"
  echo "Set gpio ${name} #${number} to direction ${direction}" >&2
}

##################################################
# Get GPIO value
# Arguments:
#   $1: GPIO name
# Return:
#   0 if success, non-zero if error
#   stdout: The value of the gpio
##################################################
get_gpio_value() {
  if (( $# != 1 )); then
    echo 'Usage: get_gpio_value name' >&2
    return 1
  fi

  local name=$1

  local number=${GPIO_NAMES_TO_NUMBER["${name}"]}
  if [[ -z ${number} ]]; then
    echo "Missing number info for: ${name}" >&2
    return 2
  fi

  init_gpio "${name}" || return
  cat "/sys/class/gpio/gpio${number}/value"
}
