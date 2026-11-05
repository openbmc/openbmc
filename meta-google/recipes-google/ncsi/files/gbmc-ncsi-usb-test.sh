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
if [ -e ../gbmc-ncsi-config.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
fi

# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source '../../networking/gbmc-net-common/gbmc-net-lib.sh' || exit

TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT

GBMC_NET_LIB="$(readlink -f ../../networking/gbmc-net-common/gbmc-net-lib.sh)"
SYSFS_DIR="$TEST_DIR/sys/class/net"
RUN_DIR="$TEST_DIR/run"
USR_NET_DIR="$TEST_DIR/usr/lib/systemd/network"
mkdir -p "$USR_NET_DIR"
cp -- -bmc-gbmcncsiusb.network "$USR_NET_DIR/"

sed -e "s#/usr/share/gbmc-net-lib.sh#$GBMC_NET_LIB#g" \
    -e "s#/sys/class/net#$SYSFS_DIR#g" \
    -e "s#/usr/lib/systemd/network#$USR_NET_DIR#g" \
    -e "s#/run#$RUN_DIR#g" \
    gbmc-ncsi-usb.sh >"$TEST_DIR/gbmc-ncsi-usb.sh"

reload_usb_sh() {
  unset gbmc_ncsi_dynamic_lib
  gbmc_br_dhcrelay_linkaddrs=()
  gbmc_br_dhcrelay_dirty=
  gbmc_ncsi_usb_ifindex=()
  rm -rf "$SYSFS_DIR" "$RUN_DIR"
  mkdir -p "$SYSFS_DIR" "$RUN_DIR"
  systemctl_calls=()
  networkd_reloads=0
  nft_reloads=0
  defer_calls=0
  systemctl() { systemctl_calls+=("$*"); }
  gbmc_net_networkd_reload() { (( ++networkd_reloads )); }
  gbmc_net_nftables_reload() { (( ++nft_reloads )); }
  gbmc_ip_monitor_defer() { (( ++defer_calls )); }
  # shellcheck source=/dev/null
  source "$TEST_DIR/gbmc-ncsi-usb.sh"
}

set_sysfs_link() {
  local name="$1" alias="$2" idx="$3"
  mkdir -p "$SYSFS_DIR/$name"
  printf '%s\n' "$alias" >"$SYSFS_DIR/$name/ifalias"
  printf '%s\n' "$idx" >"$SYSFS_DIR/$name/ifindex"
}

link_event() {
  change='link'
  action="$1"
  intf="$2"
  gbmc_ncsi_dynamic_hook
}

testUsbNcsiSkipsSameIfindexUpdates() {
  reload_usb_sh

  # Non-NCSI links are ignored
  set_sysfs_link eth0 other 3
  link_event add eth0
  expect_numeq "${#systemctl_calls[@]}" 0

  # Initial USB NCSI enumeration configures files and starts RA
  set_sysfs_link enu1u1u2u1u1 ncsi-usb-side 14
  link_event add enu1u1u2u1u1
  expect_numeq "$nft_reloads" 1
  expect_numeq "$networkd_reloads" 1
  expect_numeq "${#systemctl_calls[@]}" 1
  expect_streq "${systemctl_calls[0]}" "restart --no-block gbmc-ncsi-ra@enu1u1u2u1u1"
  [ -n "${gbmc_br_dhcrelay_linkaddrs[enu1u1u2u1u1]+1}" ] || fail
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[enu1u1u2u1u1]}" ""
  [ -f "$RUN_DIR/nftables/50-gbmc-ncsi-enu1u1u2u1u1.rules" ] || fail
  [ -f "$RUN_DIR/systemd/network/-bmc-enu1u1u2u1u1.network" ] || fail

  # Simulate dhcrelay tracking an active link-local address on ifindex 14
  gbmc_br_dhcrelay_linkaddrs[enu1u1u2u1u1]='fe80::1'
  mkdir -p "$RUN_DIR/gbmc-br-dhcrelay/uppers"
  touch "$RUN_DIR/gbmc-br-dhcrelay/uppers/enu1u1u2u1u1"

  # Repeated RTM_NEWLINK events on the same ifindex (carrier, networkd
  # reconfigure, flag updates) must not restart gbmc-ncsi-ra or reset state
  systemctl_calls=()
  link_event add enu1u1u2u1u1
  link_event add enu1u1u2u1u1
  expect_numeq "${#systemctl_calls[@]}" 0
  expect_numeq "$nft_reloads" 1
  expect_numeq "$networkd_reloads" 1
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[enu1u1u2u1u1]}" "fe80::1"
  [ -e "$RUN_DIR/gbmc-br-dhcrelay/uppers/enu1u1u2u1u1" ] || fail

  # Rapid USB re-enumeration under a new ifindex (even without seeing del)
  # resets dhcrelay state and restarts gbmc-ncsi-ra on the new interface
  set_sysfs_link enu1u1u2u1u1 ncsi-usb-side 19
  link_event add enu1u1u2u1u1
  expect_numeq "${#systemctl_calls[@]}" 1
  expect_streq "${systemctl_calls[0]}" "restart --no-block gbmc-ncsi-ra@enu1u1u2u1u1"
  [ -n "${gbmc_br_dhcrelay_linkaddrs[enu1u1u2u1u1]+1}" ] || fail
  expect_streq "${gbmc_br_dhcrelay_linkaddrs[enu1u1u2u1u1]}" ""
  [ ! -e "$RUN_DIR/gbmc-br-dhcrelay/uppers/enu1u1u2u1u1" ] || fail
  expect_streq "$gbmc_br_dhcrelay_dirty" "1"
  expect_numeq "$defer_calls" 1

  # Link deletion cleans up configs, stops RA, and allows re-add with same ifindex
  gbmc_br_dhcrelay_dirty=
  touch "$RUN_DIR/gbmc-br-dhcrelay/uppers/enu1u1u2u1u1"
  systemctl_calls=()
  link_event del enu1u1u2u1u1
  expect_numeq "${#systemctl_calls[@]}" 1
  expect_streq "${systemctl_calls[0]}" "stop --no-block gbmc-ncsi-ra@enu1u1u2u1u1"
  [ ! -e "$RUN_DIR/nftables/50-gbmc-ncsi-enu1u1u2u1u1.rules" ] || fail
  [ ! -e "$RUN_DIR/systemd/network/-bmc-enu1u1u2u1u1.network" ] || fail
  [ ! -e "$RUN_DIR/gbmc-br-dhcrelay/uppers/enu1u1u2u1u1" ] || fail
  expect_streq "$gbmc_br_dhcrelay_dirty" "1"
  expect_numeq "$defer_calls" 2
  [ -z "${gbmc_br_dhcrelay_linkaddrs[enu1u1u2u1u1]+1}" ] || fail

  systemctl_calls=()
  link_event add enu1u1u2u1u1
  expect_numeq "${#systemctl_calls[@]}" 1
}

main
