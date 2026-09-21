#!/bin/bash
# shellcheck disable=SC2317
# shellcheck disable=SC2329
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

# Substitute template variables and paths for testing
TEST_DIR="$(mktemp -d)"
trap 'rm -rf "$TEST_DIR"' EXIT

NET_LIB="$(readlink -f ../network-sh/lib.sh)"
GBMC_NET_LIB="$(readlink -f ../gbmc-net-common/gbmc-net-lib.sh)"

sed -e 's,@IFS@,eth0,g' \
    -e "s#/usr/share/network/lib.sh#$NET_LIB#g" \
    -e "s#/usr/share/gbmc-net-lib.sh#$GBMC_NET_LIB#g" \
    gbmc-nic-neigh.sh.in >"$TEST_DIR/gbmc-nic-neigh.sh"

reload_neigh_sh() {
  unset gbmc_nic_neigh_lib
  # shellcheck source=meta-google/recipes-google/networking/gbmc-nic-config/gbmc-nic-neigh.sh.in
  source "$TEST_DIR/gbmc-nic-neigh.sh"
}

reload_neigh_sh

test_gbmc_nic_neigh_ips() {
  reload_neigh_sh

  local ips=()
  gbmc_nic_neigh_ips ips "2002:a05:7538:212b::"
  expect_numeq 16 "${#ips[@]}"
  expect_streq "2002:a05:7538:212b::" "${ips[0]}"
  expect_streq "2002:a05:7538:212b:fd01::" "${ips[1]}"
  expect_streq "2002:a05:7538:212b:fd02::" "${ips[2]}"
  expect_streq "2002:a05:7538:212b:fd0f::" "${ips[15]}"
}

test_gbmc_nic_neigh_set_add_del() {
  reload_neigh_sh

  # Mock environment
  local ip_calls=()
  ip() {
    ip_calls+=("$*")
  }
  sysctl() { :; }
  local nft_reloads=0
  gbmc_net_nftables_reload() {
    (( nft_reloads++ ))
  }
  local networkd_reloads=0
  gbmc_net_networkd_reload() {
    (( networkd_reloads++ ))
  }
  gbmc_net_route_table_for_intf() {
    echo "1002"
  }

  local run_dir="$TEST_DIR/run"
  mkdir -p "$run_dir/systemd/network" "$run_dir/nftables"

  # Override run directory paths in gbmc_nic_neigh_set
  eval "$(declare -f gbmc_nic_neigh_set | sed "s#/run#$run_dir#g")"

  # Test add
  gbmc_nic_neigh_set add "2002:a05:7538:212b::"

  expect_numeq 0 "$networkd_reloads"
  expect_numeq 1 "$nft_reloads"

  # Check that 10-nic-neigh-table.conf was created
  local conf_files=("$run_dir"/systemd/network/*-bmc-eth0.network.d/10-nic-neigh-table.conf)
  [ -f "${conf_files[0]}" ] || fail

  # Check that ip neigh replace proxy was called 16 times
  local neigh_adds=0
  local rule_adds=0
  for call in "${ip_calls[@]}"; do
    [[ "$call" =~ "-6 neigh replace proxy" ]] && (( neigh_adds++ ))
    [[ "$call" =~ "-6 rule add" ]] && (( rule_adds++ ))
  done
  expect_numeq 16 "$neigh_adds"
  expect_numeq 2 "$rule_adds" # to main and from 1002

  # Test del
  ip_calls=()
  nft_reloads=0
  networkd_reloads=0
  gbmc_nic_neigh_set del "2002:a05:7538:212b::"

  expect_numeq 0 "$networkd_reloads"
  expect_numeq 1 "$nft_reloads"
  [ ! -f "${conf_files[0]}" ] || fail

  local neigh_dels=0
  local rule_dels=0
  for call in "${ip_calls[@]}"; do
    [[ "$call" =~ "-6 neigh del proxy" ]] && (( neigh_dels++ ))
    [[ "$call" =~ "-6 rule del" ]] && (( rule_dels++ ))
  done
  expect_numeq 16 "$neigh_dels"
  expect_numeq 2 "$rule_dels"
}

test_gbmc_nic_neigh_carrier_up() {
  reload_neigh_sh

  local ip_calls=()
  ip() {
    ip_calls+=("$*")
  }
  sysctl() { :; }

  gbmc_nic_neigh_addr="2002:a05:7538:212b::"
  intf="eth0"
  change="link"
  action="add"
  carrier="UP"

  gbmc_nic_neigh_hook

  local neigh_adds=0
  for call in "${ip_calls[@]}"; do
    [[ "$call" =~ "-6 neigh replace proxy" ]] && (( neigh_adds++ ))
  done
  expect_numeq 16 "$neigh_adds"
}

test_gbmc_nic_neigh_carrier_up_fallback() {
  reload_neigh_sh

  ip() {
    return 1
  }
  sysctl() { :; }
  local networkd_reloads=0
  local reloaded_intfs=()
  gbmc_net_networkd_reload() {
    (( networkd_reloads++ ))
    reloaded_intfs+=("$@")
  }

  gbmc_nic_neigh_addr="2002:a05:7538:212b::"
  intf="eth0"
  change="link"
  action="add"
  carrier="UP"

  gbmc_nic_neigh_hook

  expect_numeq 1 "$networkd_reloads"
  expect_streq "eth0" "${reloaded_intfs[0]}"
}

test_gbmc_nic_neigh_fallback_on_failure() {
  reload_neigh_sh

  ip() {
    return 1
  }
  sysctl() { :; }
  local networkd_reloads=0
  local reloaded_intfs=()
  gbmc_net_networkd_reload() {
    (( networkd_reloads++ ))
    reloaded_intfs+=("$@")
  }
  gbmc_net_route_table_for_intf() {
    echo "1002"
  }

  local run_dir="$TEST_DIR/run_fail"
  mkdir -p "$run_dir/systemd/network" "$run_dir/nftables"
  eval "$(declare -f gbmc_nic_neigh_set | sed "s#/run#$run_dir#g")"

  gbmc_nic_neigh_set add "2002:a05:7538:212b::"
  expect_numeq 1 "$networkd_reloads"
  expect_streq "eth0" "${reloaded_intfs[0]}"
}

expect_err 0 main
