#!/bin/bash
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
if [ -e ../gbmc-nic-config.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
fi

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source ../network-sh/lib.sh || exit
# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source ../gbmc-net-common/gbmc-net-lib.sh || exit

TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT

NET_LIB="$(readlink -f ../network-sh/lib.sh)"
GBMC_NET_LIB="$(readlink -f ../gbmc-net-common/gbmc-net-lib.sh)"

sed -e 's,@IFS@,eth0,g' \
    -e "s#/usr/share/network/lib.sh#$NET_LIB#g" \
    -e "s#/usr/share/gbmc-net-lib.sh#$GBMC_NET_LIB#g" \
    gbmc-nic-cn.sh.in >"$TEST_DIR/gbmc-nic-cn.sh"

orig_write_dropin="$(declare -f gbmc_net_networkd_write_dropin)"
RUN_DIR="$TEST_DIR/run"
BR_CONF="$RUN_DIR/systemd/network/-bmc-gbmcbr.network.d/10-nic-cn.conf"
NIC_CONF="$RUN_DIR/systemd/network/-bmc-eth0.network.d/10-nic-cn.conf"

ip_calls=()
networkd_reloads=0

# Reloads the script with ip(8), sysctl and the reloads mocked, and the
# drop-ins written under RUN_DIR
reload_cn_sh() {
  unset gbmc_nic_cn_lib
  eval "$orig_write_dropin"
  # shellcheck source=meta-google/recipes-google/networking/gbmc-nic-config/gbmc-nic-cn.sh.in
  source "$TEST_DIR/gbmc-nic-cn.sh"
  eval "$(declare -f gbmc_net_networkd_write_dropin | sed "s#/run#$RUN_DIR#g")"
  rm -rf "$RUN_DIR"
  ip_calls=()
  networkd_reloads=0
  ip() { ip_calls+=("$*"); }
  sysctl() { :; }
  gbmc_net_networkd_reload() { (( ++networkd_reloads )); }
  gbmc_net_nftables_reload() { :; }
}

# Counts the mocked ip(8) calls matching a pattern
count_calls() {
  local n=0
  local call
  for call in "${ip_calls[@]}"; do
    # shellcheck disable=SC2053  # $1 is a pattern
    [[ $call == $1 ]] && (( ++n ))
  done
  echo "$n"
}

# Feeds the hook a gbmcbr address event
addr_event() {
  change=addr
  intf=gbmcbr
  scope=global
  fam=inet6
  flags=
  action="$1"
  ip="$2"
  gbmc_nic_cn_hook
}

test_gbmc_nic_cn_ips() {
  reload_cn_sh
  local ips=()
  gbmc_nic_cn_ips ips 2002:a05:7538:212b::
  expect_numeq 7 "${#ips[@]}"
  expect_streq 2002:a05:7538:212b:1000:: "${ips[0]}"
  expect_streq 2002:a05:7538:212b:7000:: "${ips[6]}"
}

test_gbmc_nic_cn_add_del() {
  reload_cn_sh

  addr_event add 2002:a05:7538:212b::
  expect_streq 2002:a05:7538:212b:: "$gbmc_nic_cn_addr"
  grep -q '^Destination=2002:a05:7538:212b::/65$' "$BR_CONF" || fail
  grep -q 'ProxyNDP' "$BR_CONF" && fail
  grep -q '^IPv6ProxyNDPAddress=2002:a05:7538:212b:7000::$' "$NIC_CONF" || fail
  expect_numeq 7 "$(count_calls '-6 neigh replace proxy * dev eth0')"
  expect_numeq 1 "$(count_calls '-6 route replace 2002:a05:7538:212b::/65 *')"

  ip_calls=()
  addr_event del 2002:a05:7538:212b::
  expect_streq '' "$gbmc_nic_cn_addr"
  [ ! -e "$BR_CONF" ] || fail
  [ ! -e "$NIC_CONF" ] || fail
  expect_numeq 7 "$(count_calls '-6 neigh del proxy * dev eth0')"
  expect_numeq 1 "$(count_calls '-6 route del 2002:a05:7538:212b::/65 *')"
}

test_gbmc_nic_cn_addr_change() {
  reload_cn_sh

  addr_event add 2002:a05:7538:212b::
  ip_calls=()
  addr_event add 2002:a05:7538:212c::
  expect_streq 2002:a05:7538:212c:: "$gbmc_nic_cn_addr"
  expect_numeq 7 "$(count_calls '-6 neigh del proxy 2002:a05:7538:212b:* dev eth0')"
  expect_numeq 7 "$(count_calls '-6 neigh replace proxy 2002:a05:7538:212c:* dev eth0')"
  grep -q '^Destination=2002:a05:7538:212c::/65$' "$BR_CONF" || fail
  grep -q '212b' "$NIC_CONF" && fail

  # Other addresses are ignored
  ip_calls=()
  addr_event add fd00::
  addr_event add 2002:a05:7538:212b::1
  addr_event del 2002:a05:7538:212b::
  expect_numeq 0 "${#ip_calls[@]}"
  expect_streq 2002:a05:7538:212c:: "$gbmc_nic_cn_addr"
}

test_gbmc_nic_cn_carrier_up() {
  reload_cn_sh

  addr_event add 2002:a05:7538:212b::
  ip_calls=()
  networkd_reloads=0
  change='link'
  action='add'
  carrier='UP'
  intf=eth1
  gbmc_nic_cn_hook
  expect_numeq 0 "${#ip_calls[@]}"
  intf=eth0
  gbmc_nic_cn_hook
  expect_numeq 7 "$(count_calls '-6 neigh replace proxy * dev eth0')"

  # Failures don't fall back to a networkd reload, which wouldn't change
  # anything
  ip() { return 1; }
  gbmc_nic_cn_hook
  expect_numeq 0 "$networkd_reloads"
}

test_gbmc_nic_cn_init_stale() {
  reload_cn_sh

  addr_event add 2002:a05:7538:212b::
  change=init
  gbmc_nic_cn_hook
  [ -f "$BR_CONF" ] || fail
  [ -f "$NIC_CONF" ] || fail

  # A previous instance's config is removed if the address is gone
  gbmc_nic_cn_addr=
  gbmc_nic_cn_hook
  [ ! -e "$BR_CONF" ] || fail
  [ ! -e "$NIC_CONF" ] || fail
}

expect_err 0 main
