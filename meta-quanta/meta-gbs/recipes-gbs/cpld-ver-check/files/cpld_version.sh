#!/bin/bash
# Copyright 2020 Google LLC
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


#################################################################
# Prints CPLD version and save it in file /run/cpld0.version
#
#    CPLD version format: Major.Minor.Point.Subpoint
#
# Major/Minor: base vendor image version, encoded in register 0x0 of the CPLD
# I2C bus.
#    Major = higher 4 bits of register 0x00
#    Minor = lower 4 bits of register 0x00
#    Point/SubPoint: reserved as 0x0 for now
#
#   e.g. reg[0] = 0x25 -> ver 2.5.0.0
#
#################################################################

CPLD_I2C_BUS_NUM=13
CPLD_I2C_BUS_ADDR='0x21'
CPLD_I2C_BASEVER_REG='0x00'
VER_ENV_FILE='/run/cpld0.version'

#################################################################
# Parse the byte value from i2cget and sanity checks the hex format
# Arguments:
#   $1: i2c_bus_num
#   $2: i2c_bus_addr
#   $3: i2c_reg
# Global:
#   'byte_val' will be written with the numeric value from i2cget
# Returns:
#   0 if success, non-zero if failed to get the value or value is malformed
#################################################################
read_and_check_i2c_get() {
  if ! (( $# == 3 )); then
    echo "Usage: read_and_check_i2c_get i2c_bus_num i2c_bus_addr i2c_reg"
    return 1
  fi

  local i2c_bus_num=$1
  local i2c_bus_addr=$2
  local i2c_reg=$3

  local i2c_val_raw
  i2c_val_raw=$(i2cget -y "${i2c_bus_num}" "${i2c_bus_addr}" "${i2c_reg}") || return

  # Verify that it is of format 0x[hex][hex].
  local HEXBYTE_RE='^0x[0-9A-Fa-f]{2}$'
  if ! [[ ${i2c_val_raw} =~ ${HEXBYTE_RE} ]]; then
    echo "i2cget $* outputs invalid value: ${i2c_val_raw}"
    return 1
  fi

  ((byte_val = i2c_val_raw))
  return 0
}

#################################################################
# Prints CPLD version in Major.Minor.Point.Subpoint format.
# Each dot separated field is decimal number with no leading zeros.
# Arguments:
#    None
# Globals:
#    Write parsed version into the following global variables:
#      cpld_ver_major
#      cpld_ver_minor
#      cpld_point
#      cpld_subpoint
# Returns:
#    0 if success, non-zero otherwise
#################################################################
parse_cpld_ver() {
  # Stores the output of read_and_check_i2c_get
  local byte_val

  # Read a byte, assign higher 4 bits to cpld_ver_major and lower 4 bits to
  # cpld_ver_minor.
  # e.g. cpld_ver_raw = 0x09 => major_hex = 0, minor_hex = 9
  read_and_check_i2c_get ${CPLD_I2C_BUS_NUM} ${CPLD_I2C_BUS_ADDR} ${CPLD_I2C_BASEVER_REG} ||
    return
  local cpld_ver
  ((cpld_ver = byte_val))
  ((cpld_ver_major = cpld_ver >> 4))
  ((cpld_ver_minor = cpld_ver & 0xf))
  ((cpld_point = 0))
  ((cpld_subpoint = 0))

  return 0
}

main() {
  local cpld_ver_major
  local cpld_ver_minor
  local cpld_point
  local cpld_subpoint

  parse_cpld_ver || return

  # Write CPLD version to file.
  cpld_ver="${cpld_ver_major}.${cpld_ver_minor}.${cpld_point}.${cpld_subpoint}"
  echo "CPLD version ${cpld_ver}"
  echo "${cpld_ver}" > "${VER_ENV_FILE}"

  return 0
}

# Exit without running main() if sourced
return 0 2>/dev/null

main "$@"
