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
if [ -e ../gbmc-bridge.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
  NET_LIB=../network-sh/lib.sh
  GBMC_NET_LIB=../gbmc-net-common/gbmc-net-lib.sh
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
  NET_LIB="$SYSROOT/usr/share/network/lib.sh"
  GBMC_NET_LIB="$SYSROOT/usr/share/gbmc-net-lib.sh"
fi

# Run the scripts against a scratch root instead of /run, /var and /usr/share
TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT
RUN="$TEST_DIR/run"
NETD="$RUN/systemd/network"
mkdir -p "$TEST_DIR/share/network" "$TEST_DIR/share/gbmc-br-lib" || exit
sed_for_test() {
  sed -e 's,@NCSI_IF@,,g' \
    -e "s#/usr/share/#$TEST_DIR/share/#g" \
    -e "s#/run/#$RUN/#g" \
    -e "s#/var/google/#$TEST_DIR/var/#g" \
    "$1" >"$2"
}
sed_for_test "$NET_LIB" "$TEST_DIR/share/network/lib.sh" || exit
sed_for_test "$GBMC_NET_LIB" "$TEST_DIR/share/gbmc-net-lib.sh" || exit
sed_for_test gbmc-br-lib.sh "$TEST_DIR/share/gbmc-br-lib.sh" || exit
sed_for_test gbmc-br-gw-src.sh.in "$TEST_DIR/gbmc-br-gw-src.sh" || exit

# Normally provided by gbmc-ip-monitor
MOCK_NOW=100
# shellcheck disable=SC2034  # nameref
gbmc_ip_monitor_uptime() {
  local -n mock_uptime_out="$1"
  mock_uptime_out="$MOCK_NOW"
}
declare -A mock_timers=()
gbmc_ip_monitor_timer() {
  mock_timers["$1"]="$2"
}
gbmc_ip_monitor_defer() { :; }

# shellcheck source=meta-google/recipes-google/networking/gbmc-bridge/gbmc-br-gw-src.sh.in
source "$TEST_DIR/gbmc-br-gw-src.sh" || exit

mock_reloads=0
gbmc_net_networkd_reload() {
  (( ++mock_reloads ))
}
gbmc_net_nftables_reload() { :; }

# The ip(8) mock: route lookups and default routes come from the variables
# below, everything else is recorded in ip_calls
declare -A mock_route_get=()
mock_defaults=()
ip_calls=()
ip() {
  if [[ $* == '-6 route get 2000:: from '* ]]; then
    [[ -n ${mock_route_get["$6"]-} ]] || return 2
    echo "${mock_route_get["$6"]}"
  elif [[ $* == '-6 route show default' ]]; then
    printf '%s\n' "${mock_defaults[@]}"
  else
    ip_calls+=("$*")
  fi
}

orig_funcs="$(declare -f)"
reset_state() {
  eval "$orig_funcs"
  rm -rf "${RUN:?}" "${TEST_DIR:?}/var"
  mkdir -p "$NETD"
  gbmc_br_gw_src_ips=()
  gbmc_br_gw_src_routes=()
  gbmc_br_gw_src_disabled=()
  gbmc_br_gw_src_conf=()
  gbmc_br_gw_src_adv_cur=()
  gbmc_br_gw_src_adv_gone=()
  gbmc_br_gw_src_br_routes=()
  gbmc_br_gw_src_br_installed=()
  primary_ip_from_br=
  mock_route_get=()
  mock_defaults=()
  mock_timers=()
  ip_calls=()
  MOCK_NOW=100
}

# Writes a gateway drop-in to both networkd directories
# Arguments:
#   $1: Interface
#   $2: Drop-in file name
#   $3: Contents
write_gw_conf() {
  local dir
  for dir in "$NETD"/{00,}-bmc-"$1".network.d; do
    mkdir -p "$dir"
    printf '%s' "$3" >"$dir/$2"
  done
}

expect_keys() {
  local -n expect_keys_arr="$1"
  shift
  local got want
  got="$(printf '%s\n' "${!expect_keys_arr[@]}" | sort)"
  want="$(printf '%s\n' "$@" | sort)"
  [[ $got == "$want" ]] && return
  echo "  Line ${BASH_LINENO[0]} keys '$got' != '$want'" >&2
  test_err=1
}

test_tray_metric() {
  reset_state
  local metric out
  for metric in 700:1070 800:1080 900:1090 1800:2080 1900:2090; do
    gbmc_br_tray_metric out "${metric%:*}" || fail
    expect_numeq "$out" "${metric#*:}"
  done
  for metric in 0 1 70 705 1000 1050 2000 2080 abc ''; do
    expect_err 1 gbmc_br_tray_metric out "$metric"
  done
}

test_adv_pfx_metric() {
  reset_state
  local pair want hex pfx metric
  for pair in 700:2bc0 800:3200 900:3840 1800:7080 1900:76c0; do
    want="${pair%:*}"
    hex="${pair#*:}"
    gbmc_br_adv_pfx pfx 2002:a05:66d0:476a:fd01::1 "$want" || fail
    expect_streq "$pfx" "2002:a05:66d0:476a::$hex"
    gbmc_br_adv_metric metric "$pfx" || fail
    expect_numeq "$metric" "$want"
  done
  # Full 12-bit range (0..4095) in the upper 12 bits of the last hextet
  gbmc_br_adv_pfx pfx 2002:a05:66d0:476a:fd01::1 0 || fail
  expect_streq "$pfx" '2002:a05:66d0:476a::'
  gbmc_br_adv_pfx pfx 2002:a05:66d0:476a:fd01::1 4095 || fail
  expect_streq "$pfx" '2002:a05:66d0:476a::fff0'
  for metric in -1 4096 10000 abc ''; do
    expect_err 1 gbmc_br_adv_pfx pfx 2002:a05:66d0:476a:fd01::1 "$metric"
  done
  # Heads predating the encoding or non-head metrics
  for pfx in 2002:a05:66d0:476a:: 2002:a05:66d0:476a::1 \
      2002:a05:66d0:476a:fd01::3200 2002:a05:66d0:476a::3201 \
      2002:a05:66d0:476a::3e80 2002:a05:66d0:476a::8200; do
    metric=x
    gbmc_br_adv_metric metric "$pfx" || fail
    expect_numeq "$metric" 0
  done
  expect_err 1 gbmc_br_adv_metric metric 'z::'
}

test_stale_key_replace() {
  reset_state
  local update_calls=0
  gbmc_br_gw_src_update() { (( ++update_calls )); }
  gbmc_br_set_router() { :; }
  local primary_calls=()
  gbmc_br_config_primary_ip() { primary_calls+=("$2 $1"); }
  local old='default via fe80::1 dev front0 src 2001:db8::1 metric 800 pref medium'
  local fallback='default via fe80::1 dev front0 src 2001:db8::1 metric 1800 pref medium'
  local disabled='default via fe80::3 dev front0 metric 800 onlink pref medium'
  local other='default via fe80::4 dev front1 src 2001:db8::1 metric 800 pref medium'
  gbmc_br_gw_src_routes=(["$old"]=1 ["$fallback"]=1 ["$disabled"]=1 ["$other"]=1)
  gbmc_br_gw_src_disabled=(["$disabled"]=1)

  # gbmc-ra replaced the router at the same metric, without a delete event
  change=route action=add
  route='default via fe80::2 dev front0 proto static metric 800 pref medium'
  gbmc_br_gw_src_hook
  local new='default via fe80::2 dev front0 metric 800 pref medium'
  expect_keys gbmc_br_gw_src_routes "$new" "$fallback" "$other"
  expect_keys gbmc_br_gw_src_disabled
  expect_numeq "${#primary_calls[@]}" 1
  expect_streq "${primary_calls[0]-}" "del $old"
  expect_numeq "$update_calls" 1

  # Our own source change of it only replaces its key
  route='default via fe80::2 dev front0 proto static src 2001:db8::1 metric 800 pref medium'
  gbmc_br_gw_src_hook
  expect_keys gbmc_br_gw_src_routes \
    'default via fe80::2 dev front0 src 2001:db8::1 metric 800 pref medium' \
    "$fallback" "$other"

  # Only a different router takes the primary route with it, not a source
  # change through the same one
  route='default via fe80::2 dev front0 proto static src 2001:db8::9 metric 800 pref medium'
  gbmc_br_gw_src_hook
  expect_keys gbmc_br_gw_src_routes \
    'default via fe80::2 dev front0 src 2001:db8::9 metric 800 pref medium' \
    "$fallback" "$other"
  expect_numeq "${#primary_calls[@]}" 1
}

test_disabled_gate() {
  local route='default via fe80::1 dev front0 metric 800 onlink pref medium'
  local dyn=$'[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=800\n'

  # gbmc-ra dropped the router, it must not come back with a source
  reset_state
  gbmc_br_gw_src_routes=(["$route"]=1)
  gbmc_br_gw_src_disabled=(["$route"]=1)
  gbmc_br_gw_src_ips=([2001:db8::1]=15)
  gbmc_br_gw_src_update
  expect_keys gbmc_br_gw_src_routes
  expect_keys gbmc_br_gw_src_disabled
  [[ ${ip_calls[*]} != *'route replace'* ]] || fail
  [ ! -e "$RUN/gbmc-net-route-src/front0/800" ] || fail

  # Still configured: it comes back with the address that has no route out
  # of anything else, as it would route out of it
  reset_state
  write_gw_conf front0 10-gateway-dyn.conf "$dyn"
  gbmc_br_gw_src_routes=(["$route"]=1)
  gbmc_br_gw_src_disabled=(["$route"]=1)
  gbmc_br_gw_src_ips=([2001:db8::1]=15)
  gbmc_br_gw_src_update
  expect_keys gbmc_br_gw_src_disabled
  expect_streq "${ip_calls[*]}" \
    "route replace $route src 2001:db8::1 proto static"
  local cur
  read -r cur <"$RUN/gbmc-net-route-src/front0/800" || fail
  expect_streq "$cur" 'fe80::1 2001:db8::1'
  IFS= read -r -d '' cur <"$NETD/-bmc-front0.network.d/10-gateway-dyn.conf"
  expect_streq "$cur" "${dyn}PreferredSource=2001:db8::1"$'\n'

  # Not for an address routing out of a more preferred uplink
  reset_state
  write_gw_conf front0 10-gateway-dyn.conf "$dyn"
  gbmc_br_gw_src_routes=(["$route"]=1)
  gbmc_br_gw_src_disabled=(["$route"]=1)
  gbmc_br_gw_src_ips=([2001:db8::1]=15)
  mock_route_get=([2001:db8::1]='2000:: from 2001:db8::1 via fe80::5 dev eth9 proto static metric 512 pref medium')
  gbmc_br_gw_src_update
  expect_keys gbmc_br_gw_src_disabled "$route"
  expect_streq "${ip_calls[*]}" ''

  # Nor for an address in the gbmcbr prefix of another BMC, even while its
  # policy rule is missing and it routes out of a less preferred uplink
  reset_state
  write_gw_conf front0 10-gateway-dyn.conf "$dyn"
  gbmc_br_gw_src_routes=(["$route"]=1)
  gbmc_br_gw_src_disabled=(["$route"]=1)
  local peer=2001:db8:0:2:fd00:aaaa:bbbb:cccc
  gbmc_br_gw_src_ips=(["$peer"]=15)
  declare -gA gbmc_br_from_ra_pfxs=([2001:db8:0:2:fd00::/80]=1)
  mock_route_get=(["$peer"]="2000:: from $peer via fe80::6 dev ncsi0 proto static metric 900 pref medium")
  gbmc_br_gw_src_update
  expect_keys gbmc_br_gw_src_disabled "$route"
  expect_streq "${ip_calls[*]}" ''
  gbmc_br_from_ra_pfxs=()
}

test_nic_dev() {
  reset_state
  local saved_if="$gbmc_br_gw_src_ncsi_if"
  gbmc_br_gw_src_ncsi_if=ncsi0
  local nic

  # Without any recorded NIC prefix, NIC addresses go to the NCSI interface
  nic=
  gbmc_br_gw_src_nic_dev nic 2001:db8:0:1:fd01::1 || fail
  expect_streq "$nic" ncsi0
  expect_err 1 gbmc_br_gw_src_nic_dev nic 2001:db8:0:1::1

  # The /76 recorded for a NIC interface
  mkdir -p "$RUN/ncsi-ips"
  echo 2001:db8:0:1:fd01:: >"$RUN/ncsi-ips/eth1"
  nic=
  gbmc_br_gw_src_nic_dev nic 2001:db8:0:1:fd0f::5 || fail
  expect_streq "$nic" eth1
  expect_err 1 gbmc_br_gw_src_nic_dev nic 2001:db8:0:1:fd11::5
  expect_err 1 gbmc_br_gw_src_nic_dev nic 2001:db8:0:3:fd01::1

  gbmc_br_gw_src_ncsi_if="$saved_if"
}

test_disabled_load() {
  reset_state
  write_gw_conf front0 10-gateway.conf $'[Neighbor]\nMACAddress=00:11:22:33:44:55\nAddress=fe80::1\n[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=512\nTable=1002\n[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=1800\n'
  write_gw_conf front0 10-gateway-dyn.conf $'[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=800\n'
  write_gw_conf gbmcbr 10-gateway-dyn.conf $'[Route]\nGateway=fe80::7\nGatewayOnLink=true\nMetric=1000\n'
  mock_defaults=('default via fe80::1 dev front0 proto static metric 1800 pref medium')
  gbmc_br_gw_src_disabled_load
  local route='default via fe80::1 dev front0 metric 800 onlink pref medium'
  expect_keys gbmc_br_gw_src_disabled "$route"
  expect_keys gbmc_br_gw_src_routes "$route"
}

test_adv_withdraw() {
  reset_state
  local conf="$NETD/00-bmc-gbmcbr.network.d/70-ip-hybrid-route.conf"
  local wd="$NETD/00-bmc-gbmcbr.network.d/45-withdrawn-IPv6RoutePrefix-2001:db8:1:2::3200_124.conf"
  local adv=$'[IPv6RoutePrefix]\nRoute=2001:db8:1:2::3200/124\nLifetimeSec=120\n'
  local up='default via fe80::1 dev front0 proto static src 2001:db8:1:2::1 metric 800 pref medium'
  gbmc_br_gw_src_ips=([2001:db8:1:2::1]=15)
  mock_defaults=("$up" 'default via fe80::9 dev gbmcbr proto static src 2001:db8:1:2::1 metric 1080 pref medium')
  gbmc_br_gw_src_adv_update
  local cur=
  IFS= read -r -d '' cur <"$conf"
  expect_streq "$cur" "$adv"
  expect_keys gbmc_br_gw_src_adv_cur 2001:db8:1:2::3200

  # A linkdown route is withdrawn right away
  mock_defaults=("${up/ pref medium/ linkdown pref medium}")
  gbmc_br_gw_src_adv_update
  [ ! -e "$conf" ] || fail
  [ -e "$wd" ] || fail
  expect_keys gbmc_br_gw_src_adv_cur

  # And advertised again once it is back
  mock_defaults=("$up")
  gbmc_br_gw_src_adv_update
  [ ! -e "$wd" ] || fail
  [ -e "$conf" ] || fail

  # A route that goes missing is kept for the grace period
  mock_defaults=()
  gbmc_br_gw_src_adv_update
  [ -e "$conf" ] || fail
  [ ! -e "$wd" ] || fail
  expect_numeq "${mock_timers[gbmc-br-gw-src-adv]-}" "$GBMC_BR_RA_GRACE"
  MOCK_NOW=$(( MOCK_NOW + GBMC_BR_RA_GRACE ))
  gbmc_br_gw_src_adv_update
  [ ! -e "$conf" ] || fail
  [ -e "$wd" ] || fail

  # A restart picks up what the previous instance advertised
  gbmc_br_gw_src_adv_cur=()
  write_gw_conf gbmcbr 70-ip-hybrid-route.conf "$adv"
  gbmc_br_gw_src_adv_load
  expect_keys gbmc_br_gw_src_adv_cur 2001:db8:1:2::3200
}

test_br_update() {
  reset_state
  gbmc_br_gw_src_ips=(
    [2001:db8:1:2:fd02::]=10
    [2001:db8:1:3:fd02::]=10)
  gbmc_br_gw_src_br_routes=(
    [2001:db8:1:2::2bc0]=fe80::1
    [2001:db8:1:2::3200]=fe80::1
    [2001:db8:1:3::3840]=fe80::1
    [2001:db8:1:2::7080]=fe80::1
    [2001:db8:1:3::76c0]=fe80::1
    [2001:db8:1:2::1]=fe80::2)
  gbmc_br_gw_src_br_update
  expect_keys gbmc_br_gw_src_br_installed 1070 1080 1090 2080 2090 2001
  expect_streq "${gbmc_br_gw_src_br_installed[1070]}" 'fe80::1 2001:db8:1:2:fd02::'
  expect_streq "${gbmc_br_gw_src_br_installed[1080]}" 'fe80::1 2001:db8:1:2:fd02::'
  expect_streq "${gbmc_br_gw_src_br_installed[1090]}" 'fe80::1 2001:db8:1:3:fd02::'
  expect_streq "${gbmc_br_gw_src_br_installed[2080]}" 'fe80::1 2001:db8:1:2:fd02::'
  expect_streq "${gbmc_br_gw_src_br_installed[2090]}" 'fe80::1 2001:db8:1:3:fd02::'
  expect_streq "${gbmc_br_gw_src_br_installed[2001]}" 'fe80::2 2001:db8:1:2:fd02::'
  expect_streq "$primary_ip_from_br" '2001:db8:1:2::2bc0'
}

main
