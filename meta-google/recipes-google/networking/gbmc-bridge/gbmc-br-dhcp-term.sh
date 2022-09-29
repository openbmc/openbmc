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

# Wait until a well known service is network available
echo "Waiting for network reachability" >&2
while ! ping -c 1 -W 1 2001:4860:4860::8888 >/dev/null 2>&1; do
  sleep 1
done

# We need to guarantee we wait at least 5 minutes from reachable in
# case networking just came up
wait_min=5
echo "Network is reachable, waiting $wait_min min" >&2
sleep $((60 * wait_min))

get_dhcp_unit_json() {
  busctl -j call \
    org.freedesktop.systemd1 \
    /org/freedesktop/systemd1/unit/gbmc_2dbr_2ddhcp_2eservice \
    org.freedesktop.DBus.Properties \
    GetAll s org.freedesktop.systemd1.Unit
}

# Follow the process and make sure it idles for at least 5 minutes before
# shutting down. This allows for failures and retries to happen.
while true; do
  json="$(get_dhcp_unit_json)" || exit
  last_ms="$(echo "$json" | jq -r '.data[0].StateChangeTimestampMonotonic.data')"
  if pid="$(cat /run/gbmc-br-dhcp.pid 2>/dev/null)"; then
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

  # If the process is running, give it at least 5 minutes from when it started
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
  sleep $w
done

echo "Stopping DHCP processing" >&2
systemctl stop --no-block gbmc-br-dhcp
