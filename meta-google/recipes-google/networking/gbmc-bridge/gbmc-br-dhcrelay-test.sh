#!/bin/bash
# shellcheck disable=SC2034
# shellcheck disable=SC2317
# shellcheck disable=SC2329
# Copyright 2026 Google LLC
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

cd "$(dirname "$0")" || exit
if [ -e ../gbmc-bridge.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
fi

TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT
RUN_DIR="$TEST_DIR/run"
declare -A gbmc_br_dhcrelay_linkaddrs=()

sed "s#/run/#$RUN_DIR/#g" gbmc-br-dhcrelay.sh >"$TEST_DIR/gbmc-br-dhcrelay.sh"

reload_dhcrelay_sh() {
  unset gbmc_br_dhcrelay_lib
  gbmc_br_dhcrelay_linkaddrs=()
  gbmc_br_dhcrelay_dirty=
  GBMC_IP_MONITOR_HOOKS=()
  rm -rf "$RUN_DIR"
  mkdir -p "$RUN_DIR"
  systemctl_calls=()
  defer_pending=0
  systemctl() { systemctl_calls+=("$*"); }
  gbmc_ip_monitor_defer() { defer_pending=1; }
  # shellcheck source=/dev/null
  source "$TEST_DIR/gbmc-br-dhcrelay.sh"
}

flush_defer() {
  (( defer_pending == 1 )) || return 0
  defer_pending=0
  change='defer'
  gbmc_br_dhcrelay_hook
}

addr_event() {
  change='addr'
  action="$1"
  intf="$2"
  ip="$3"
  fam="${4:-inet6}"
  scope="${5:-link}"
  flags="${6-}"
  gbmc_br_dhcrelay_hook
}

testDhcrelayDeduplicatesAddresses() {
  reload_dhcrelay_sh

  # Unregistered interfaces are ignored and remain unset
  addr_event add gbmcbr fe80::1
  expect_numeq "$defer_pending" 0
  expect_numeq "${#systemctl_calls[@]}" 0
  [ -z "${gbmc_br_dhcrelay_linkaddrs[gbmcbr]+1}" ] || fail

  # Register eth0 (empty string), eth1 (legacy 0 initialization), and eth2
  gbmc_br_dhcrelay_linkaddrs[eth0]=''
  gbmc_br_dhcrelay_linkaddrs[eth1]=0
  gbmc_br_dhcrelay_linkaddrs[eth2]=''

  # Tentative link-local address does not add upper or defer dhcrelay restart
  addr_event add eth0 fe80::10 inet6 link tentative
  expect_numeq "$defer_pending" 0
  expect_numeq "${#systemctl_calls[@]}" 0
  [ ! -e "$RUN_DIR/gbmc-br-dhcrelay/uppers/eth0" ] || fail

  # Deleting an unseen/tentative address is a no-op (never underflows)
  addr_event del eth0 fe80::10 inet6 link ''
  expect_numeq "$defer_pending" 0
  expect_numeq "${#systemctl_calls[@]}" 0
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[eth0]}" ""

  # Unrelated defer event when not dirty is a no-op
  change='defer'
  gbmc_br_dhcrelay_hook
  expect_numeq "${#systemctl_calls[@]}" 0

  # First non-tentative link-local address writes upper immediately and defers
  addr_event add eth0 fe80::10 inet6 link permanent
  expect_numeq "$defer_pending" 1
  expect_numeq "${#systemctl_calls[@]}" 0
  expect_streq "$(<"$RUN_DIR/gbmc-br-dhcrelay/uppers/eth0")" "ff02::1:2%eth0"

  # Ensure no leftover non-dot temp files exist alongside upper
  local files=("$RUN_DIR"/gbmc-br-dhcrelay/uppers/*)
  expect_numeq "${#files[@]}" 1

  # Flushing defer runs one non-blocking restart
  flush_defer
  expect_numeq "${#systemctl_calls[@]}" 1
  expect_streq "${systemctl_calls[0]}" "restart --no-block gbmc-br-dhcrelay.service"

  # Subsequent unrelated defer does not restart again
  change='defer'
  gbmc_br_dhcrelay_hook
  expect_numeq "${#systemctl_calls[@]}" 1

  # Repeated RTM_NEWADDR refreshes of the same address are ignored
  systemctl_calls=()
  addr_event add eth0 fe80::10 inet6 link permanent
  addr_event add eth0 fe80::10 inet6 link ''
  expect_numeq "$defer_pending" 0
  expect_numeq "${#systemctl_calls[@]}" 0
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[eth0]}" "fe80::10"

  # Adding a second link-local address updates tracked set without restarting
  addr_event add eth0 fe80::20 inet6 link ''
  expect_numeq "$defer_pending" 0
  expect_numeq "${#systemctl_calls[@]}" 0
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[eth0]}" "fe80::10 fe80::20"

  # Deleting the first address keeps upper active while second remains
  addr_event del eth0 fe80::10
  expect_numeq "$defer_pending" 0
  expect_numeq "${#systemctl_calls[@]}" 0
  [ -f "$RUN_DIR/gbmc-br-dhcrelay/uppers/eth0" ] || fail
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[eth0]}" "fe80::20"

  # Deleting the last address after prior refreshes removes upper and defers
  addr_event del eth0 fe80::20
  expect_numeq "$defer_pending" 1
  expect_numeq "${#systemctl_calls[@]}" 0
  [ ! -e "$RUN_DIR/gbmc-br-dhcrelay/uppers/eth0" ] || fail
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[eth0]}" ""
  flush_defer
  expect_numeq "${#systemctl_calls[@]}" 1
  expect_streq "${systemctl_calls[0]}" "restart --no-block gbmc-br-dhcrelay.service"

  # Multi-interface boot burst coalesces into a single dhcrelay restart
  systemctl_calls=()
  addr_event add eth0 fe80::10
  addr_event add eth1 fe80::99
  addr_event add eth2 fe80::88
  expect_numeq "$defer_pending" 1
  expect_numeq "${#systemctl_calls[@]}" 0
  expect_streq "$(<"$RUN_DIR/gbmc-br-dhcrelay/uppers/eth0")" "ff02::1:2%eth0"
  expect_streq "$(<"$RUN_DIR/gbmc-br-dhcrelay/uppers/eth1")" "ff02::1:2%eth1"
  expect_streq "$(<"$RUN_DIR/gbmc-br-dhcrelay/uppers/eth2")" "ff02::1:2%eth2"
  flush_defer
  expect_numeq "${#systemctl_calls[@]}" 1
  expect_streq "${systemctl_calls[0]}" "restart --no-block gbmc-br-dhcrelay.service"

  # Replaying init addresses when upper file already matches does not restart
  systemctl_calls=()
  gbmc_br_dhcrelay_linkaddrs[eth1]=0
  addr_event add eth1 fe80::99
  expect_numeq "$defer_pending" 0
  expect_numeq "${#systemctl_calls[@]}" 0

  # Removing both remaining uppers in a burst also coalesces into one restart
  addr_event del eth0 fe80::10
  addr_event del eth1 fe80::99
  addr_event del eth2 fe80::88
  expect_numeq "$defer_pending" 1
  expect_numeq "${#systemctl_calls[@]}" 0
  [ ! -e "$RUN_DIR/gbmc-br-dhcrelay/uppers/eth1" ] || fail
  flush_defer
  expect_numeq "${#systemctl_calls[@]}" 1
}

main
