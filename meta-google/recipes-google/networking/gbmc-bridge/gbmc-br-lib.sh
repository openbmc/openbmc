#!/bin/bash
# Copyright 2022 Google LLC
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

[ -n "${gbmc_br_lib_init-}" ] && return

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit
# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source /usr/share/gbmc-net-lib.sh || exit

# A list of functions which get executed for each configured IP.
# These are configured by the files included below.
# Shellcheck does not understand how this gets referenced
# shellcheck disable=SC2034
GBMC_BR_LIB_SET_IP_HOOKS=()

gbmc_br_source_dir() {
  local dir="$1"

  local file
  while read -r -d $'\0' file; do
    # SC doesn't like dynamic source loading
    # shellcheck disable=SC1090
    source "$file" || return
  done < <(shopt -s nullglob; for f in "$dir"/*.sh; do printf '%s\0' "$f"; done)
}

# Load configurations from a known location in the filesystem to populate
# hooks that are executed after each event.
gbmc_br_source_dir /usr/share/gbmc-br-lib || exit

gbmc_br_run_hooks() {
  local -n hookvar="$1"
  shift
  local hook
  for hook in "${hookvar[@]}"; do
    "$hook" "$@" || return
  done
}

gbmc_br_set_runtime_ip() {
  local name="$1"
  local ip="$2"

  local pfx_bytes=()
  local rc=0
  if ! ip_to_bytes pfx_bytes "$ip"; then
    echo "Invalid IPv6 for $name: $ip" >&2
    ip=
    rc=1
  fi
  if [ -z "$ip" ]; then
    echo "Removing runtime gbmcbr IP: $name" >&2
    rm -f /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-"$name".conf
    gbmc_net_networkd_reload gbmcbr
    return $rc
  fi

  local pfx
  pfx="$(ip_bytes_to_str pfx_bytes)"
  # We either have an inband IP address that has a /76 allocation for
  # our BMCs, or we have an OOB address that is a /64. We can only tell the
  # difference by the 9th byte being 00 vs fd.
  local stateless_size=
  if (( pfx_bytes[8] == 0 )); then
    stateless_size=64
  else
    stateless_size=76
  fi
  (( pfx_bytes[8] |= 0xfd ))
  (( pfx_bytes[9] &= 0xf0 ))
  local stateless_pfx
  stateless_pfx="$(ip_bytes_to_str pfx_bytes)"
  local contents
  read -r -d '' contents <<EOF
[Address]
Address=$pfx/128
DuplicateAddressDetection=none
[IPv6RoutePrefix]
Route=$pfx/80
LifetimeSec=120
EOF
  local scontents
  if [[ -n $stateless_size ]]; then
    read -r -d '' scontents <<EOF
[IPv6Prefix]
Prefix=$stateless_pfx/80
PreferredLifetimeSec=120
ValidLifetimeSec=120
[Route]
Destination=$stateless_pfx/$stateless_size
Type=unreachable
Metric=1024
EOF
  fi
  echo "Adding runtime gbmcbr IP: $name $pfx stateless($stateless_pfx/$stateless_size)" >&2

  local file
  for file in /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-$name.conf; do
    mkdir -p "$(dirname "$file")"
    printf '%s\n%s' "$contents" "$scontents" >"$file"
  done

  gbmc_net_networkd_reload gbmcbr
}

gbmc_br_reload_ips() {
  # Remove legacy network configuration
  rm -rf /etc/systemd/network/{00,}-bmc-gbmcbr.network.d

  # Remove existing loaded configurations
  (shopt -s nullglob; rm -rf /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-static*.conf)

  gbmc_br_set_runtime_ip static "$(cat /var/google/gbmc-br-ip 2>/dev/null)" || true
  local ip
  local i=0
  for ip in $(shopt -s nullglob; cat /run/gbmc-br-ips/* 2>/dev/null); do
    gbmc_br_set_runtime_ip static$i "$ip"
    (( i += 1 ))
  done
}

gbmc_br_set_ip() {
  local ip="${1-}"
  shift
  local alt_ips=("$@")

  if [ -n "$ip" ]; then
    local pfx_bytes=()
    if ! ip_to_bytes pfx_bytes "$ip"; then
      echo "Not setting invalid IPv6: $ip" >&2
      return 1
    fi
    mkdir -p /var/google || return
    echo "$ip" >/var/google/gbmc-br-ip || return
  else
    [ ! -f "/var/google/gbmc-br-ip" ] && return
    rm -rf /var/google/gbmc-br-ip
  fi

  # Remove existing loaded configurations
  (shopt -s nullglob; rm -rf /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-alt*.conf)

  gbmc_br_set_runtime_ip static "$ip" || return
  local alt_ip
  local i=0
  for alt_ip in "${alt_ips[@]}"; do
    gbmc_br_set_runtime_ip alt$i "$alt_ip" || return
    (( i += 1 ))
  done

  gbmc_br_run_hooks GBMC_BR_LIB_SET_IP_HOOKS "$ip" || return
}

gbmc_br_lib_init=1
