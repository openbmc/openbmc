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
wait_min=5
echo "Network is reachable, waiting $wait_min minutes" >&2
# Wait 5 minutes for any potential DHCP that hasn't landed yet
sleep $((60 * wait_min))
# If the DHCP configuration process is running, wait for it to finish
if pid="$(cat /run/gbmc-br-dhcp.pid 2>/dev/null)"; then
  echo "DHCP still running ($pid), waiting" >&2
  wait "$pid"
fi
echo "Stopping DHCP processing" >&2
systemctl stop --no-block gbmc-br-dhcp
