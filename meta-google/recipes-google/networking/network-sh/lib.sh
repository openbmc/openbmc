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

[ -n "${network_init-}" ] && return

mac_to_bytes() {
  local -n bytes="$1"
  local str="$2"

  # Verify that the MAC is Valid
  [[ "$str" =~ ^[[:xdigit:]]{1,2}(:[[:xdigit:]]{1,2}){5}$ ]] || return

  # Split the mac into hex bytes
  local oldifs="$IFS"
  IFS=:
  local byte
  for byte in $str; do
    bytes+=(0x$byte)
  done
  IFS="$oldifs"
}

mac_to_eui48() {
  local mac_bytes=()
  mac_to_bytes mac_bytes "$1" || return

  # Return the EUI-64 bytes in the IPv6 format
  printf '%02x%02x:%02x%02x:%02x%02x\n' "${mac_bytes[@]}"
}

mac_to_eui64() {
  local mac_bytes=()
  mac_to_bytes mac_bytes "$1" || return

  # Using EUI-64 conversion rules, create the suffix bytes from MAC bytes
  # Invert bit-0 of the first byte, and insert 0xfffe in the middle.
  local suffix_bytes=(
    $((mac_bytes[0] ^ 1))
    ${mac_bytes[@]:1:2}
    $((0xff)) $((0xfe))
    ${mac_bytes[@]:3:3}
  )

  # Return the EUI-64 bytes in the IPv6 format
  printf '%02x%02x:%02x%02x:%02x%02x:%02x%02x\n' "${suffix_bytes[@]}"
}

ipv6_pfx_concat() {
  local pfx="$1"
  local sfx="$2"

  # Validate the prefix
  if ! [[ "$pfx" =~ ^(([0-9a-fA-F]{1,4}:)+):/([0-9]+)$ ]]; then
    echo "Invalid IPv6 prefix: $pfx" >&2
    return 1
  fi
  local addr="${BASH_REMATCH[1]}"
  local cidr="${BASH_REMATCH[3]}"
  # Ensure prefix doesn't have too many bytes
  local nos="${addr//:/}"
  if (( ${#addr} - ${#nos} > (cidr+7)/16 )); then
    echo "Too many prefix bytes: $pfx" >&2
    return 1
  fi

  # Validate the suffix
  if ! [[ "$sfx" =~ ^[0-9a-fA-F]{1,4}(:[0-9a-fA-F]{1,4})*$ ]]; then
    echo "Invalid IPv6 suffix: $sfx" >&2
    return 1
  fi
  # Ensure suffix doesn't have too many bytes
  local nos="${sfx//:/}"
  if (( ${#sfx} - ${#nos} >= (128-cidr)/16 )); then
    echo "Too many suffix bytes: $sfx" >&2
    return 1
  fi

  local comb="$addr:$sfx"
  local nos="${comb//:/}"
  if (( ${#comb} - ${#nos} == 8 )); then
    comb="$addr$sfx"
  fi
  echo "$comb/$cidr"
}

ipv6_pfx_to_cidr() {
  [[ "$1" =~ ^[0-9a-fA-F:]+/([0-9]+)$ ]] || return
  echo "${BASH_REMATCH[1]}"
}

network_init=1
return 0 2>/dev/null
echo "network is a library, not executed directly" >&2
exit 1
