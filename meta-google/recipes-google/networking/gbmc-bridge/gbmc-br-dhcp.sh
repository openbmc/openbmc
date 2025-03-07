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
# Shellcheck does not understand how this gets referenced
# shellcheck disable=SC2034
GBMC_BR_DHCP_HOOKS=()

# A dict of outstanding items that should prevent DHCP completion
declare -A GBMC_BR_DHCP_OUTSTANDING=()
# A dict of netboot status to retain start of each state
declare -A NETBOOT_STATUS_START=()

# SC can't find this path during repotest
# shellcheck disable=SC1091
source /usr/share/network/lib.sh || exit
# SC can't find this path during repotest
# shellcheck disable=SC1091
source /usr/share/gbmc-br-lib.sh || exit

# Load configurations from a known location in the filesystem to populate
# hooks that are executed after each event.
gbmc_br_source_dir /usr/share/gbmc-br-dhcp || exit
update_netboot_status() {
  local state="$1"
  local message="$2"
  local code="$3"
  local retries="${4-}"
  local time

  if [[ "$code" == "START" ]]; then
    NETBOOT_STATUS_START["$state"]=$SECONDS
    time=0
  elif [[ -v NETBOOT_STATUS_START["$state"] ]]; then
    time=$((SECONDS - NETBOOT_STATUS_START["$state"]))
  else
    # easy indicator to flag error, no state should ever report before START is defined.
    time=-1
  fi
  local json_output="{\"Message\":\"$message\",\"State\":\"$state\",\"Code\":\"$code\",\"Time\":\"$time\""

  if [[ -n "$retries" ]]; then
    json_output+=",\"retries\":\"$retries\""
  fi

  json_output+="}"

  systemd-cat -t "gbmc-netboot" <<<"$json_output"
  update-dhcp-status 'ONGOING' "$json_output"
}

if [ "$1" = bound ]; then
  # We don't want to allow 2 simultaneous sessions. Check for a pidfile
  PID_FILE=/run/gbmc-br-dhcp.pid
  exec {PID_FD}<>$PID_FILE
  # If we can't acquire the lock we already have a successful DHCP process in the works
  flock -xn $PID_FD || exit 0

  # Write out the current PID and cleanup when complete
  trap 'rm -f $PID_FILE' EXIT
  echo "$$" >&$PID_FD

  # Don't let other DHCP processes start by hogging the pidfile indefinitely
  # on successful termination.
  # This intentionally comes after the pidfile hook to replace it, since we
  # won't need to remove the pidfile if we never terminate.
  trap '(( $? == 0 )) && sleep infinity' EXIT

  update_netboot_status "netboot" "BMC netboot started" "START"
  # Variable is from the environment via udhcpc6
  # shellcheck disable=SC2154
  update_netboot_status "dhcp" "Received dhcp response ${ipv6}, ${fqd}, ${bootfile_url}" "START"

  pfx_bytes=()
  ip_to_bytes pfx_bytes "$ipv6"
  # Ensure we are a BMC and have a suffix nibble, the 0th index is reserved
  # Alternatively, we may also have received a /64 for the OOB address
  if (( pfx_bytes[8] != 0xfd || (pfx_bytes[9] & 0xf) == 0 )) &&
     (( pfx_bytes[8] != 0 || pfx_bytes[9] != 0 )); then
    update_netboot_status "dhcp" "Invalid address prefix ${ipv6}" "FAIL"
    exit 1
  fi
  # Ensure we don't have more than a /80 address
  for (( i = 10; i < 16; ++i )); do
    if (( pfx_bytes[i] != 0 )); then
      update_netboot_status "dhcp" "Invalid address ${ipv6}" "FAIL"
      exit 1
    fi
  done

  # we need to set hostname first before IP so logging services report using proper name
  if [ -n "${fqdn-}" ]; then
    update_netboot_status "dhcp_name" "Attempting to set hostname ${fqdn}" "START"
    hostnamectl set-hostname "$fqdn" || true
    update_netboot_status "dhcp_name" "Succesfully set hostname ${fqdn}" "SUCCESS"
  fi

  update_netboot_status "dhcp_ip" "Attempt to set ip to ${ipv6}" "START"
  pfx="$(ip_bytes_to_str pfx_bytes)"
  gbmc_br_set_ip "$pfx" || exit
  update_netboot_status "dhcp_ip" "Successfully set ip to ${ipv6}" "SUCCESS"
  update_netboot_status "dhcp" "DHCP complete" "SUCCESS"

  gbmc_br_run_hooks GBMC_BR_DHCP_HOOKS || exit

  # If any of our hooks had expectations we should fail here
  if [ "${#GBMC_BR_DHCP_OUTSTANDING[@]}" -gt 0 ]; then
    update_netboot_status "netboot" "Outstanding DHCP hooks ${!GBMC_BR_DHCP_OUTSTANDING[*]}" "FAIL"
    exit 1
  fi

  # Ensure that the installer knows we have completed processing DHCP by
  # running a service that reports completion
  echo 'Signaling dhcp done' >&2
  update_netboot_status "netboot" "BMC Netboot Complete" "SUCCESS"
  update-dhcp-status 'DONE' "Netboot finished"
fi
