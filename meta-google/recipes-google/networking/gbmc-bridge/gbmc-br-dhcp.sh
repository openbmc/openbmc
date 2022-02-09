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

# A list of functions which get executed for each bound DHCP lease.
# These are configured by the files included below.
GBMC_BR_DHCP_HOOKS=()

# Load configurations from a known location in the filesystem to populate
# hooks that are executed after each event.
shopt -s nullglob
for conf in /usr/share/gbmc-br-dhcp/*.sh; do
  # SC doesn't like dynamic source loading
  # shellcheck disable=SC1090
  source "$conf"
done

gbmc_br_dhcp_run_hooks() {
  local hook
  for hook in "${GBMC_BR_DHCP_HOOKS[@]}"; do
    "$hook" || return
  done
}

# SC can't find this path during repotest
# shellcheck disable=SC1091
source /usr/share/network/lib.sh || exit

# Write out the current PID and cleanup when complete
trap 'rm -f /run/gbmc-br-dhcp.pid' EXIT
echo "$$" >/run/gbmc-br-dhcp.pid

if [ "$1" = bound ]; then
  # Variable is from the environment via udhcpc6
  # shellcheck disable=SC2154
  echo "DHCPv6(gbmcbr): $ipv6/128" >&2

  pfx_bytes=()
  ip_to_bytes pfx_bytes "$ipv6"
  # Ensure we are a BMC and have a suffix nibble, the 0th index is reserved
  if (( pfx_bytes[8] != 0xfd || pfx_bytes[9] & 0xf == 0 )); then
    echo "Invalid address" >&2
    exit
  fi
  # Ensure we don't have more than a /80 address
  for (( i = 10; i < 16; ++i )); do
    if (( pfx_bytes[i] != 0 )); then
      echo "Invalid address" >&2
      exit
    fi
  done

  pfx="$(ip_bytes_to_str pfx_bytes)"
  (( pfx_bytes[9] &= 0xf0 ))
  stateless_pfx="$(ip_bytes_to_str pfx_bytes)"
  read -r -d '' contents <<EOF
[Network]
Address=$pfx/128
IPv6PrefixDelegation=yes
[IPv6PrefixDelegation]
RouterLifetimeSec=60
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

  for file in /etc/systemd/network/{00,}-bmc-gbmcbr.network.d/50-public.conf; do
    mkdir -p "$(dirname "$file")"
    printf '%s' "$contents" >"$file"
  done

  # Ensure that systemd-networkd performs a reconfiguration as it doesn't
  # currently check the mtime of drop-in files.
  touch -c /lib/systemd/network/*-bmc-gbmcbr.network

  if [ "$(systemctl is-active systemd-networkd)" != 'inactive' ]; then
    networkctl reload && networkctl reconfigure gbmcbr
  fi

  if [ -n "${fqdn-}" ]; then
    echo "Using hostname $fqdn" >&2
    hostnamectl set-hostname "$fqdn" || true
  fi

  gbmc_br_dhcp_run_hooks || exit

  # Ensure that the installer knows we have completed processing DHCP by
  # running a service that reports completion
  systemctl start dhcp-done --no-block
fi
