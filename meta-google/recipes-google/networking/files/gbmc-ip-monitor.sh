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

# A list of functions which get executed for each netlink event received.
# These are configured by the files included below.
GBMC_IP_MONITOR_HOOKS=()

# Load configurations from a known location in the filesystem to populate
# hooks that are executed after each event.
shopt -s nullglob
for conf in /usr/share/gbmc-ip-monitor/*.sh; do
  source "$conf"
done

gbmc_ip_monitor_run_hooks() {
  local hook
  for hook in "${GBMC_IP_MONITOR_HOOKS[@]}"; do
    "$hook" || continue
  done
}

gbmc_ip_monitor_generate_init() {
  ip link | sed 's,^[^ ],[LINK]\0,'
  local intf=
  local line
  while read line; do
    [[ "$line" =~ ^([0-9]+:[[:space:]][^:]+) ]] && intf="${BASH_REMATCH[1]}"
    [[ "$line" =~ ^[[:space:]]*inet ]] && echo "[ADDR]$intf $line"
  done < <(ip addr)
  ip -4 route | sed 's,^,[ROUTE],'
  ip -6 route | sed 's,^,[ROUTE],'
  echo '[INIT]'
}

gbmc_ip_monitor_parse_line() {
  local line="$1"
  if [[ "$line" == '[INIT]'* ]]; then
    change=init
    echo "Initialized" >&2
  elif [[ "$line" == '[ADDR]'* ]]; then
    change=addr
    action=add
    pfx_re='^\[ADDR\](Deleted )?[0-9]+:[[:space:]]*'
    intf_re='([^ ]+)[[:space:]]+'
    fam_re='([^ ]+)[[:space:]]+'
    addr_re='([^/]+)/[0-9]+[[:space:]]+(brd[[:space:]]+[^ ]+[[:space:]]+)?'
    scope_re='scope[[:space:]]+([^ ]+)[[:space:]]*(.*)'
    combined_re="${pfx_re}${intf_re}${fam_re}${addr_re}${scope_re}"
    if ! [[ "$line" =~ ${combined_re} ]]; then
      echo "Failed to parse addr: $line" >&2
      return 1
    fi
    if [ -n "${BASH_REMATCH[1]}" ]; then
      action=del
    fi
    intf="${BASH_REMATCH[2]}"
    fam="${BASH_REMATCH[3]}"
    ip="${BASH_REMATCH[4]}"
    scope="${BASH_REMATCH[6]}"
    flags="${BASH_REMATCH[7]}"
  elif [[ "$line" == '[ROUTE]'* ]]; then
    line="${line#[ROUTE]}"
    change=route
    action=add
    if ! [[ "$line" =~ ^\[ROUTE\](Deleted )?(.*)$ ]]; then
      echo "Failed to parse link: $line" >&2
      return 1
    fi
    if [ -n "${BASH_REMATCH[1]}" ]; then
      action=del
    fi
    route="${BASH_REMATCH[2]}"
  elif [[ "$line" == '[LINK]'* ]]; then
    change=link
    action=add
    pfx_re='^\[LINK\](Deleted )?[0-9]+:[[:space:]]*'
    intf_re='([^:]+):[[:space:]]+'
    if ! [[ "$line" =~ ${pfx_re}${intf_re} ]]; then
      echo "Failed to parse link: $line" >&2
      return 1
    fi
    if [ -n "${BASH_REMATCH[1]}" ]; then
      action=del
    fi
    intf="${BASH_REMATCH[2]}"
    read line || break
    data=($line)
    mac="${data[1]}"
  else
    return 2
  fi
}

cleanup() {
  local st="$?"
  trap - HUP INT QUIT ABRT TERM EXIT
  jobs -l -p | xargs -r kill || true
  exit $st
}
trap cleanup HUP INT QUIT ABRT TERM EXIT

return 0 2>/dev/null

while read line; do
  gbmc_ip_monitor_parse_line "$line" || continue
  gbmc_ip_monitor_run_hooks || continue
  if [ "$change" = 'init' ]; then
    systemd-notify --ready
  fi
done < <(gbmc_ip_monitor_generate_init; exec ip monitor link addr route label)
