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

# SC can't find this path during repotest
# shellcheck disable=SC1091
source /usr/share/network/lib.sh || exit

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

gbmc_br_reload() {
  if [ "$(systemctl is-active systemd-networkd)" != 'inactive' ]; then
    networkctl reload && networkctl reconfigure gbmcbr
  fi
}

gbmc_br_no_ip() {
  echo "Runtime removing gbmcbr IP" >&2
  rm -f /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-public.conf
  gbmc_br_reload
}

gbmc_br_reload_ip() {
  local ip="${1-}"

  if [ -z "$ip" ] && ! ip="$(cat /var/google/gbmc-br-ip 2>/dev/null)"; then
    echo "Ignoring unconfigured IP" >&2
    gbmc_br_no_ip
    return 0
  fi

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
PreferredLifetimeSec=60
ValidLifetimeSec=60
[IPv6RoutePrefix]
Route=$pfx/80
LifetimeSec=60
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

  gbmc_br_reload
}

gbmc_br_set_ip() {
  local ip="${1-}"

  if [ -n "$ip" ]; then
    mkdir -p /var/google || return
    echo "$ip" >/var/google/gbmc-br-ip || return
  else
    rm -rf /var/google/gbmc-br-ip
  fi

  # Remove legacy network configuration
  rm -rf /etc/systemd/network/{00,}-bmc-gbmcbr.network.d

  gbmc_br_run_hooks GBMC_BR_LIB_SET_IP_HOOKS "$ip" || return

  gbmc_br_reload_ip "$ip"
}

gbmc_br_lib_init=1
if ! (return 0 2>/dev/null); then
  echo "gbmc-br-lib is a library, not executed directly" >&2
  exit 1
fi
