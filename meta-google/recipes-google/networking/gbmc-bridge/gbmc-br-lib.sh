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

gbmc_br_no_ip() {
  echo "Runtime removing gbmcbr IP" >&2
  rm -f /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-public.conf
  gbmc_net_networkd_reload gbmcbr
}

gbmc_br_reload_ip() {
  local ip="${1-}"

  if [ -z "$ip" ] && ! ip="$(cat /var/google/gbmc-br-ip 2>/dev/null)"; then
    echo "Ignoring unconfigured IP" >&2
    gbmc_br_no_ip
    return 0
  fi

  # Remove legacy network configuration
  rm -rf /etc/systemd/network/{00,}-bmc-gbmcbr.network.d

  local pfx_bytes=()
  if ! ip_to_bytes pfx_bytes "$ip"; then
    echo "Ignoring Invalid IPv6: $ip" >&2
    gbmc_br_no_ip
    return 0
  fi

  local pfx
  pfx="$(ip_bytes_to_str pfx_bytes)"
  (( pfx_bytes[9] &= 0xf0 ))
  local stateless_pfx
  stateless_pfx="$(ip_bytes_to_str pfx_bytes)"
  local contents
  read -r -d '' contents <<EOF
[Network]
Address=$pfx/128
[IPv6Prefix]
Prefix=$stateless_pfx/80
PreferredLifetimeSec=120
ValidLifetimeSec=120
[IPv6RoutePrefix]
Route=$pfx/80
LifetimeSec=120
[Route]
Destination=$stateless_pfx/76
Type=unreachable
Metric=1024
EOF
  echo "Runtime setting gbmcbr IP: $pfx" >&2

  local file
  for file in /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-public.conf; do
    mkdir -p "$(dirname "$file")"
    printf '%s' "$contents" >"$file"
  done

  gbmc_net_networkd_reload gbmcbr
}

gbmc_br_set_ip() {
  local ip="${1-}"
  local old_ip=
  if [ -n "$ip" ]; then
    old_ip="$(cat /var/google/gbmc-br-ip 2>/dev/null)"
    [ "$old_ip" == "$ip" ] && return
    mkdir -p /var/google || return
    echo "$ip" >/var/google/gbmc-br-ip || return
  else
    [ ! -f "/var/google/gbmc-br-ip" ] && return
    rm -rf /var/google/gbmc-br-ip
  fi

  gbmc_br_run_hooks GBMC_BR_LIB_SET_IP_HOOKS "$ip" || return

  gbmc_br_reload_ip "$ip"
}

gbmc_br_lib_init=1
