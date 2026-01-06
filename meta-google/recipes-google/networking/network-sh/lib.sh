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
    bytes+=("0x$byte")
  done
  IFS="$oldifs"
}

mac_to_eui48() {
  local mac_bytes=(0 0 0 0 0 0 0 0 0 0)
  mac_to_bytes mac_bytes "$1" || return

  # Return the EUI-64 bytes in the IPv6 format
  ip_bytes_to_str mac_bytes
}

mac_to_eui64() {
  local mac_bytes=()
  mac_to_bytes mac_bytes "$1" || return
  mid_byte1="$2"
  mid_byte2="$3"
  [[ -z "$mid_byte1" ]] && mid_byte1="0xff"
  [[ -z "$mid_byte2" ]] && mid_byte2="0xfe"

  # Using EUI-64 conversion rules, create the suffix bytes from MAC bytes
  # Invert bit-1 of the first byte, and insert 0xfffe in the middle.
  # Also support customized mid bytes.
  # shellcheck disable=SC2034
  local suffix_bytes=(
    0 0 0 0 0 0 0 0
    $((mac_bytes[0] ^ 2))
    "${mac_bytes[@]:1:2}"
    $(( mid_byte1 )) $(( mid_byte2 ))
    "${mac_bytes[@]:3:3}"
  )

  # Return the EUI-64 bytes in the IPv6 format
  ip_bytes_to_str suffix_bytes
}

ip_to_bytes() {
  local -n bytes_out="$1"
  local str="$2"

  local bytes=()
  local oldifs="$IFS"
  # Heuristic for V4 / V6, validity will be checked as it is parsed
  if [[ "$str" == *.* ]]; then
    # Ensure we don't start or end with IFS
    [ "${str:0:1}" != '.' ] || return 1
    [ "${str: -1}" != '.' ] || return 1

    local v
    # Split IPv4 address into octets
    IFS=.
    for v in $str; do
      # IPv4 digits are always decimal numbers
      if ! [[ "$v" =~ ^[0-9]+$ ]]; then
        IFS="$oldifs"
        return 1
      fi
      # Each octet is a single byte, make sure the number isn't larger
      if (( v > 0xff )); then
        IFS="$oldifs"
        return 1
      fi
      bytes+=("$v")
    done
    # IPv4 addresses must have all 4 bytes present
    if (( "${#bytes[@]}" != 4 )); then
      IFS="$oldifs"
      return 1
    fi
  else
    # Ensure we bound the padding in an outer byte for
    # IFS splitting to work correctly
    [ "${str:0:2}" = '::' ] && str="0$str"
    [ "${str: -2}" = '::' ] && str="${str}0"

    # Ensure we don't start or end with IFS
    [ "${str:0:1}" != ':' ] || return 1
    [ "${str: -1}" != ':' ] || return 1

    # Stores the bytes that come before ::, if it exists
    local bytesBeforePad=()
    local v
    # Split the Address into hextets
    IFS=:
    for v in $str; do
      # Handle ::, which translates to an empty string
      if [ -z "$v" ]; then
        # Only allow a single :: sequence in an address
        if (( "${#bytesBeforePad[@]}" > 0 )); then
          IFS="$oldifs"
          return 1
        fi
        # Store the already parsed upper bytes separately
        # This allows us to calculate and insert padding
        bytesBeforePad=("${bytes[@]}")
        bytes=()
        continue
      fi
      # IPv6 digits are always hex
      if ! [[ "$v" =~ ^[[:xdigit:]]+$ ]]; then
        IFS="$oldifs"
        return 1
      fi
      # Ensure the number is no larger than a hextet
      v="0x$v"
      if (( v > 0xffff )); then
        IFS="$oldifs"
        return 1
      fi
      # Split the hextet into 2 bytes
      bytes+=($(( v >> 8 )))
      bytes+=($(( v & 0xff )))
    done
    # If we have ::, add padding
    if (( "${#bytesBeforePad[@]}" > 0 )); then
      # Fill the middle bytes with padding and store in `bytes`
      while (( "${#bytes[@]}" + "${#bytesBeforePad[@]}" < 16 )); do
        bytesBeforePad+=(0)
      done
      bytes=("${bytesBeforePad[@]}" "${bytes[@]}")
    fi
    # IPv6 addresses must have all 16 bytes present
    if (( "${#bytes[@]}" != 16 )); then
      IFS="$oldifs"
      return 1
    fi
  fi

  IFS="$oldifs"
  # shellcheck disable=SC2034
  bytes_out=("${bytes[@]}")
}

ip_bytes_to_str() {
  # shellcheck disable=SC2178
  local -n bytes="$1"

  if (( "${#bytes[@]}" == 4 )); then
    printf '%d.%d.%d.%d\n' "${bytes[@]}"
  elif (( "${#bytes[@]}" == 16 )); then
    # Track the starting position of the longest run of 0 hextets (2 bytes)
    local longest_i=0
    # Track the size of the longest run of 0 hextets
    local longest_s=0
    # The index of the first 0 byte in the current run of zeros
    local first_zero=0
    local i
    # Find the location of the longest run of zero hextets, preferring same
    # size runs later in the address.
    for (( i=0; i<=16; i+=2 )); do
      # Terminate the run of zeros if we are at the end of the array or
      # have a non-zero hextet
      if (( i == 16 || bytes[i] != 0 || bytes[$((i+1))] != 0 )); then
        local s=$((i - first_zero))
        if (( s >= longest_s )); then
          longest_i=$first_zero
          longest_s=$s
        fi
        first_zero=$((i+2))
      fi
    done
    # Build the address string by each hextet
    for (( i=0; i<16; i+=2 )); do
      # If we encountered a run of zeros, add the necessary :: at the end
      # of the string. If not at the end, a single : is added since : is
      # printed to subsequent hextets already.
      if (( i == longest_i )); then
        (( i += longest_s-2 ))
        printf ':'
        # End of string needs to be ::
        if (( i == 14 )); then
          printf ':'
        fi
      else
        # Prepend : to all hextets except the first for separation
        if (( i != 0 )); then
          printf ':'
        fi
        printf '%x' $(( (bytes[i]<<8) | bytes[$((i+1))]))
      fi
    done
    printf '\n'
  else
    echo "Invalid IP Bytes: ${bytes[*]}" >&2
    return 1
  fi
}

ip_pfx_concat() {
  local pfx="$1"
  local sfx="$2"

  # Parse the prefix
  if ! [[ "$pfx" =~ ^([0-9a-fA-F:.]+)/([0-9]+)$ ]]; then
    echo "Invalid IP prefix: $pfx" >&2
    return 1
  fi
  local addr="${BASH_REMATCH[1]}"
  local cidr="${BASH_REMATCH[2]}"

  # Ensure prefix doesn't have too many bytes
  local pfx_bytes=()
  if ! ip_to_bytes pfx_bytes "$addr"; then
    echo "Invalid IP prefix: $pfx" >&2
    return 1
  fi
  if (( ${#pfx_bytes[@]}*8 < cidr )); then
    echo "Prefix CIDR too large" >&2
    return 1
  fi
  # CIDR values might partially divide a byte so we need to mask out
  # only the part of the byte we want to check for emptiness
  if (( (pfx_bytes[cidr/8] & ~(~0 << (8-cidr%8))) != 0 )); then
    echo "Invalid byte $((cidr/8)): $pfx" >&2
    return 1
  fi
  local i
  # Check the rest of the whole bytes to make sure they are empty
  for (( i=cidr/8+1; i<${#pfx_bytes[@]}; i++ )); do
    if (( pfx_bytes[i] != 0 )); then
      echo "Byte $i not 0: $pfx" >&2
      return 1
    fi
  done

  # Validate the suffix
  local sfx_bytes=()
  if ! ip_to_bytes sfx_bytes "$sfx"; then
    echo "Invalid IPv6 suffix: $sfx" >&2
    return 1
  fi
  if (( "${#sfx_bytes[@]}" != "${#pfx_bytes[@]}" )); then
    echo "Suffix not the same family as prefix: $pfx $sfx" >&2
    return 1
  fi
  # Check potential partially divided bytes for emptiness in the upper part
  # based on the division specified in CIDR.
  if (( (sfx_bytes[cidr/8] & (~0 << (8-cidr%8))) != 0 )); then
    echo "Invalid byte $((cidr/8)): $sfx" >&2
    return 1
  fi
  local i
  # Check the bytes before the CIDR for emptiness to ensure they don't overlap
  for (( i=0; i<cidr/8; i++ )); do
    if (( sfx_bytes[i] != 0 )); then
      echo "Byte $i not 0: $sfx" >&2
      return 1
    fi
  done

  out_bytes=()
  for (( i=0; i<${#pfx_bytes[@]}; i++ )); do
    out_bytes+=($(( pfx_bytes[i] | sfx_bytes[i] )))
  done
  echo "$(ip_bytes_to_str out_bytes)/$cidr"
}

ip_pfx_to_cidr() {
  [[ "$1" =~ ^[0-9a-fA-F:.]+/([0-9]+)$ ]] || return
  echo "${BASH_REMATCH[1]}"
}

normalize_ip() {
  # shellcheck disable=SC2034
  local ip_bytes=()
  ip_to_bytes ip_bytes "$1" || return
  ip_bytes_to_str ip_bytes
}

network_init=1
