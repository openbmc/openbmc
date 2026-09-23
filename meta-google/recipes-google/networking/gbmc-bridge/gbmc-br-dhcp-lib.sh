#!/bin/bash
# Copyright 2024 Google LLC
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

[ -n "${gbmc_br_dhcp_lib_init-}" ] && return

# Safely flush writes and remount RWFS read-only before status signaling or reboot/powercycle.
gbmc_br_dhcp_rwfs_sync_ro() {
  sync
  mount -o remount,ro / 2>/dev/null || true
  mount -o remount,ro /run/initramfs/rw 2>/dev/null || true
}

# Global netboot trip ID parsed from installer upgrade metadata.
GBMC_TRIP_ID=""

# Fetch and cache the installer netboot trip ID into GBMC_TRIP_ID if present.
gbmc_fetch_trip_id() {
  [ -n "${GBMC_TRIP_ID-}" ] && return 0
  local trip_id_regex="/Installer/TRIP/ID/(.*)$"
  if declare -F gbmc_upgrade_metadata_first_match &>/dev/null; then
    local matched_line
    matched_line=$(gbmc_upgrade_metadata_first_match "${trip_id_regex}")
    if [[ -n "${matched_line}" && "${matched_line}" =~ ${trip_id_regex} ]]; then
      export GBMC_TRIP_ID="${BASH_REMATCH[1]}"
      return 0
    fi
  fi
  return 0
}

# Returns 0 if RWFS has been purged for the current netboot trip ID, 1 otherwise.
gbmc_rwfs_purge_done() {
  gbmc_fetch_trip_id || true
  if [ -n "${GBMC_TRIP_ID-}" ]; then
    [ -r "/var/google/rwfs-purged" ] || return 1
    [ "$(< /var/google/rwfs-purged)" = "$GBMC_TRIP_ID" ]
    return
  fi

  [ -e "/run/initramfs/rwfs-purged" ]
}

gbmc_br_dhcp_lib_init=1
