#!/bin/bash
# shellcheck disable=SC2034
# shellcheck disable=SC2154
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
if [ -e ../gbmc-net-common.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
  NET_SH_LIB="$(readlink -f ../network-sh/lib.sh)"
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
  NET_SH_LIB="$SYSROOT/usr/share/network/lib.sh"
fi

TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT
GBMC_NET_LIB="$(readlink -f gbmc-net-lib.sh)"

sed -e "s#/usr/share/network/lib.sh#$NET_SH_LIB#g" \
    -e "s#/usr/share/gbmc-net-lib.sh#$GBMC_NET_LIB#g" \
    -e "s#/run/last-ra/#$TEST_DIR/run/last-ra/#g" \
    -e "s#/var/google/last-ra/#$TEST_DIR/var/last-ra/#g" \
    gbmc-ra.sh >"$TEST_DIR/gbmc-ra.sh"

emit_ra() {
  local rtr="$1" mac="$2" pfx="$3" domain="$4"
  printf '\r\n'
  printf 'Router lifetime          :     1800 seconds\r\n'
  [[ -z $pfx ]] || printf ' Prefix                   : %s\r\n' "$pfx"
  printf ' Source link-layer address: %s\r\n' "$mac"
  [[ -z $domain ]] || printf ' DNS search list          : %s \r\n' "$domain"
  printf ' from %s\r\n' "$rtr"
}

reload_ra_sh() {
  rm -rf "${TEST_DIR:?}/run" "${TEST_DIR:?}/var"
  mkdir -p "$TEST_DIR/run" "$TEST_DIR/var"
  RA_IF=eth0
  IP_OFFSET=1
  ROUTE_METRIC=700
  GBMC_RA_NO_LOOP=1
  sleep() { :; }
  ip() { :; }
  gbmc_net_route_table_for_intf() { echo 1005; }
  # shellcheck source=/dev/null
  source "$TEST_DIR/gbmc-ra.sh"
}

testRaRetriesFailedCallbacks() {
  reload_ra_sh

  local rtr_rc=1 pfx_rc=1 fqdn_rc=1
  local rtr_calls=0 pfx_calls=0 fqdn_calls=0
  update_rtr() { (( ++rtr_calls )); return "$rtr_rc"; }
  update_pfx() { (( ++pfx_calls )); return "$pfx_rc"; }
  update_fqdn() { (( ++fqdn_calls )); return "$fqdn_rc"; }
  gbmc_ra_rdisc6() {
    emit_ra fe80::1 aa:bb:cc:dd:ee:01 fd01:2:3:4:fd00::/76 bmc-nfd00.prod.google.com
  }

  # 1. update_rtr fails: old_rtr stays invalid, pfx/fqdn skipped
  gbmc_ra_step
  expect_numeq "$rtr_calls" 1
  expect_numeq "$pfx_calls" 0
  expect_numeq "$fqdn_calls" 0
  expect_streq "$old_rtr" "invalid"

  # 2. update_rtr succeeds, update_pfx and update_fqdn fail: old_rtr advances,
  #    old_pfx and old_fqdn remain invalid so next RA retries them
  rtr_rc=0
  gbmc_ra_step
  expect_numeq "$rtr_calls" 2
  expect_numeq "$pfx_calls" 1
  expect_numeq "$fqdn_calls" 1
  expect_streq "$old_rtr" "fe80::1"
  expect_streq "$old_pfx" "invalid"
  expect_streq "$old_fqdn" "invalid"

  # 3. update_pfx succeeds, update_fqdn still fails: update_rtr is not re-run,
  #    old_pfx advances, old_fqdn still retries next step
  pfx_rc=0
  gbmc_ra_step
  expect_numeq "$rtr_calls" 2
  expect_numeq "$pfx_calls" 2
  expect_numeq "$fqdn_calls" 2
  expect_streq "$old_pfx" "fd01:2:3:4:fd01::"
  expect_streq "$old_fqdn" "invalid"

  # 4. update_fqdn succeeds: only update_fqdn runs, then steady-state is quiet
  fqdn_rc=0
  gbmc_ra_step
  expect_numeq "$rtr_calls" 2
  expect_numeq "$pfx_calls" 2
  expect_numeq "$fqdn_calls" 3
  expect_streq "$old_fqdn" "bmc-nfd01.prod.google.com"

  gbmc_ra_step
  expect_numeq "$rtr_calls" 2
  expect_numeq "$pfx_calls" 2
  expect_numeq "$fqdn_calls" 3

  unset -f update_rtr update_pfx update_fqdn gbmc_ra_rdisc6 sleep ip gbmc_net_route_table_for_intf
}

testDefaultUpdateFqdnPropagatesFailure() {
  reload_ra_sh

  local hostnamectl_rc=1
  local set_calls=0
  hostnamectl() { (( ++set_calls )); return "$hostnamectl_rc"; }

  expect_err 0 default_update_fqdn ""
  expect_numeq "$set_calls" 0

  expect_err 1 default_update_fqdn "bmc-nfd01.prod.google.com"
  expect_numeq "$set_calls" 1

  hostnamectl_rc=0
  expect_err 0 default_update_fqdn "bmc-nfd01.prod.google.com"
  expect_numeq "$set_calls" 2

  unset -f hostnamectl sleep ip gbmc_net_route_table_for_intf
}

main
