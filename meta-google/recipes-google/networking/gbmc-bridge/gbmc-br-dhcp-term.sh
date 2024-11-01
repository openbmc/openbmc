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

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit

# Wait until a well known service is network available
echo 'Waiting for network reachability' >&2
while true; do
  before=$SECONDS
  addrs="$(ip addr show gbmcbr | grep '^ *inet6' | awk '{print $2}')"
  for addr in $addrs; do
    # Remove the prefix length
    ip="${addr%/*}"
    ip_to_bytes ip_bytes "$ip" || continue
    # Ignore ULAs and non-gBMC addresses
    (( (ip_bytes[0] & 0xfc) == 0xfc || ip_bytes[8] != 0xfd )) && continue
    # Only allow for the short, well known addresses <pfx>:fd01:: and not
    # <pfx>:fd00:83c1:292d:feef. Otherwise, powercycle may be unavailable.
    (( (ip_bytes[9] & 0x0f) == 0 )) && continue
    for i in {10..15}; do
      (( ip_bytes[i] != 0 )) && continue 2
    done
    echo "Trying reachability from $ip" >&2
    for i in {0..5}; do
      ping -I "$ip" -c 1 -W 1 2001:4860:4860::8888 >/dev/null 2>&1 && break 3
      sleep 1
    done
  done
  # Ensure we only complete the addr lookup loop every 10s
  tosleep=$((before + 10 - SECONDS))
  if (( tosleep > 0 )); then
    sleep "$tosleep"
  fi
done

# We need to guarantee we wait at least 10 minutes from reachable in
# case networking just came up
wait_min=10
echo "Network is reachable, waiting $wait_min min" >&2
sleep $((60 * wait_min))

get_dhcp_unit_json() {
  busctl -j call \
    org.freedesktop.systemd1 \
    /org/freedesktop/systemd1/unit/system_2dgbmc_5cx2dbr_5cx2ddhcp_2eslice \
    org.freedesktop.DBus.Properties \
    GetAll s org.freedesktop.systemd1.Unit
}

# Follow the process and make sure it idles for at least 10 minutes before
# shutting down. This allows for failures and retries to happen.
while true; do
  json="$(get_dhcp_unit_json)" || exit
  last_ms="$(echo "$json" | jq -r '.data[0].StateChangeTimestampMonotonic.data')"
  if pid="$(cat /run/gbmc-br-dhcp.pid 2>/dev/null)" && [ -n "$pid" ]; then
    # If the DHCP configuration process is running, wait for it to finish
    echo "DHCP still running ($pid), waiting" >&2
    while [[ -e /proc/$pid ]]; do
      sleep 1
    done
    # Wait for systemd to detect the process state change
    while true; do
      json="$(get_dhcp_unit_json)" || exit
      ms="$(echo "$json" | jq -r '.data[0].StateChangeTimestampMonotonic.data')"
      (( ms != last_ms )) && break
      sleep 1
    done
  fi

  echo 'Checking DHCP Active State' >&2
  json="$(get_dhcp_unit_json)" || exit
  activestr="$(echo "$json" | jq -r '.data[0].ActiveState.data')"

  # The process is already stopped, we are done
  [[ "$activestr" == 'inactive' ]] && exit

  # If the process is running, give it at least 10 minutes from when it started
  cur_s="$(cut -d' ' -f1 /proc/uptime)"
  # Remove floating point if applied since bash can't perform float arithmetic
  cur_s="${cur_s%.*}"
  if [[ "$activestr" == 'active' ]]; then
    active_ms="$(echo "$json" | jq -r '.data[0].ActiveEnterTimestampMonotonic.data')"
  else
    active_ms=$((cur_s*1000*1000))
  fi
  w=$((active_ms/1000/1000 + (wait_min*60) - cur_s))
  [ "$w" -lt 0 ] && break
  echo "Waiting ${w}s for DHCP process" >&2
  sleep "$w"
done

echo "Stopping DHCP processing" >&2
systemctl stop --no-block gbmc-br-dhcp@'*'
