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

# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source /usr/share/gbmc-net-lib.sh || exit

oldname=
while read -r _; do
  # Don't bother parsing the output, just read the final hostname
  name="$(</etc/hostname)" || continue
  [[ "$oldname" == "$name" ]] && continue
  oldname="$name"
  echo "Updating BMC RA Hostname $name" >&2

  contents='[IPv6SendRA]'$'\n'"Domains=$name"
  for netfile in /run/systemd/network/{00,}-bmc-gbmcbr.network.d/60-domains.conf; do
    mkdir -p "$(dirname "$netfile")"
    printf '%s' "$contents" >"$netfile"
   done

  # shellcheck disable=SC2119
  gbmc_net_networkd_reload
done < <(dbus-monitor --system "type='signal',interface='org.freedesktop.DBus.Properties',member='PropertiesChanged',arg0='org.freedesktop.hostname1'")
