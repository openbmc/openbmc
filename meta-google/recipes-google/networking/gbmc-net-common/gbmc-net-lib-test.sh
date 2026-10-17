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
if [ -e ../gbmc-net-common.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
fi
# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source gbmc-net-lib.sh

# If running unprivileged without access to /run, redirect /run/gbmc-net-reload to a temporary directory
TEST_RELOAD_DIR="/run/gbmc-net-reload"
if ! mkdir -p "$TEST_RELOAD_DIR" 2>/dev/null; then
  TEST_RELOAD_DIR="$(mktemp -d)"
  eval "$(declare -f _gbmc_net_service_reload | sed "s#/run/gbmc-net-reload#$TEST_RELOAD_DIR#g")"
fi

# Pretend networkd is running unless a test says otherwise
GBMC_NET_NETWORKD_SOCK="$(mktemp)"

testNetworkdReloadActionNotStarted() {
  local saved_sock="$GBMC_NET_NETWORKD_SOCK"
  GBMC_NET_NETWORKD_SOCK=/nonexistent/io.systemd.Network
  local calls=''
  systemctl() { calls+="systemctl "; }
  networkctl() { calls+="networkctl "; }
  local rc=0
  _gbmc_net_networkd_reload_action 1 gbmcbr || rc=$?
  expect_numeq "$rc" 0
  # Without networkd's socket it doesn't even ask systemd
  expect_streq "$calls" ''
  unset -f systemctl networkctl
  GBMC_NET_NETWORKD_SOCK="$saved_sock"
}

testCrossProcessServiceReloadCoalesce() {
  rm -rf "$TEST_RELOAD_DIR"
  local test_dir
  test_dir="$(mktemp -d)"

  local run_count_file="$test_dir/run_count"
  echo 0 > "$run_count_file"

  dummy_action() {
    local gen="$1"
    local count
    count=$(<"$run_count_file")
    echo $(( count + 1 )) > "$run_count_file"
    sleep 0.1
    return 0
  }

  # Launch 3 background reloads concurrently
  _gbmc_net_service_reload "testsvc" dummy_action &
  local pid1=$!
  _gbmc_net_service_reload "testsvc" dummy_action &
  local pid2=$!
  _gbmc_net_service_reload "testsvc" dummy_action &
  local pid3=$!

  wait "$pid1"
  wait "$pid2"
  wait "$pid3"

  local total_runs
  total_runs=$(<"$run_count_file")
  # 3 concurrent requests must coalesce into at most 2 executions (initial + 1 coalesced pending batch)
  if (( total_runs > 2 )); then
    echo "Expected <= 2 executions from 3 concurrent reloads, got $total_runs" >&2
    fail
  fi

  rm -rf "$test_dir" "$TEST_RELOAD_DIR"
}

testReloadAsync() {
  rm -rf "$TEST_RELOAD_DIR"
  local test_dir
  test_dir="$(mktemp -d)"
  local saved
  saved="$(declare -f _gbmc_net_networkd_reload_action)"
  # Records the interfaces of each batch once the caller let go of it
  _gbmc_net_networkd_reload_action() {
    shift
    while [[ ! -e $test_dir/go ]]; do sleep 0.05; done
    echo "${*:-none}" >>"$test_dir/batches"
  }

  GBMC_NET_RELOAD_ASYNC=1
  # Doesn't wait for the reload
  gbmc_net_reload_queue_start
  gbmc_net_networkd_reload eth0
  gbmc_net_reload_queue_end || fail
  [[ ! -e $test_dir/batches ]] || fail
  gbmc_net_networkd_reload eth1 || fail
  touch "$test_dir/go"
  wait
  GBMC_NET_RELOAD_ASYNC=

  # Every request is covered, by at most one batch each
  local -a batches=()
  mapfile -t batches <"$test_dir/batches"
  (( ${#batches[@]} >= 1 && ${#batches[@]} <= 2 )) || fail
  [[ " ${batches[*]} " == *' eth0 '* && " ${batches[*]} " == *' eth1 '* ]] || fail

  eval "$saved"
  rm -rf "$test_dir" "$TEST_RELOAD_DIR"
}

testCrossProcessServiceReloadStatus() {
  rm -rf "$TEST_RELOAD_DIR"

  dummy_fail_action() {
    sleep 0.1
    return 42
  }

  _gbmc_net_service_reload "testsvc" dummy_fail_action &
  local pid1=$!
  _gbmc_net_service_reload "testsvc" dummy_fail_action &
  local pid2=$!

  local rc1=0 rc2=0
  wait "$pid1" || rc1=$?
  wait "$pid2" || rc2=$?

  if (( rc1 != 42 || rc2 != 42 )); then
    echo "Expected both coalesced reloads to fail with 42, got rc1=$rc1 rc2=$rc2" >&2
    fail
  fi

  # A coalesced waiter reports failure if no usable status was recorded
  local state_dir="$TEST_RELOAD_DIR/testsvc"
  echo $(( $(<"$state_dir/req_gen") + 5 )) >"$state_dir/done_gen"
  rm -f "$state_dir/status"
  local miss_rc=0
  _gbmc_net_service_reload "testsvc" dummy_fail_action || miss_rc=$?
  if (( miss_rc != 1 )); then
    echo "Expected failure rc=1 when no status was recorded, got $miss_rc" >&2
    fail
  fi

  rm -rf "$TEST_RELOAD_DIR"
}

testCrossProcessServiceReloadBatch() {
  rm -rf "$TEST_RELOAD_DIR"
  local test_dir
  test_dir="$(mktemp -d)"

  local batch_file="$test_dir/batch"

  record_action() {
    shift
    printf '%s\n' "$*" >>"$batch_file"
  }

  # Everything queued while a batch runs must be merged into the next one
  local hold_fd
  local state_dir="$TEST_RELOAD_DIR/testsvc"
  mkdir -p "$state_dir"
  exec {hold_fd}>"$state_dir/lock"
  flock -x "$hold_fd"

  _gbmc_net_service_reload "testsvc" record_action eth0 &
  local pid1=$!
  _gbmc_net_service_reload "testsvc" record_action eth1 &
  local pid2=$!
  sleep 0.2

  flock -u "$hold_fd"
  exec {hold_fd}>&-
  wait "$pid1"
  wait "$pid2"

  # One execution, carrying both interfaces
  expect_streq "$(wc -l <"$batch_file")" '1'
  local batch
  batch="$(tr ' ' '\n' <"$batch_file" | sort | tr '\n' ' ')"
  expect_streq "$batch" 'eth0 eth1 '

  rm -rf "$test_dir" "$TEST_RELOAD_DIR"
}

testCrossProcessServiceReloadCrashRequeue() {
  rm -rf "$TEST_RELOAD_DIR"
  local test_dir
  test_dir="$(mktemp -d)"

  local state_dir="$TEST_RELOAD_DIR/testsvc"
  local batch_file="$test_dir/batch"

  record_action() {
    shift
    printf '%s\n' "$*" >>"$batch_file"
  }

  # Leave behind a batch as if its owner had been killed mid-execution
  mkdir -p "$state_dir/running"
  touch "$state_dir/running/eth9"

  _gbmc_net_service_reload "testsvc" record_action eth0

  # The orphaned request must be picked up rather than dropped
  local batch
  batch="$(tr ' ' '\n' <"$batch_file" | sort | tr '\n' ' ')"
  expect_streq "$batch" 'eth0 eth9 '
  [ -d "$state_dir/running" ] && fail

  rm -rf "$test_dir" "$TEST_RELOAD_DIR"
}

testNetworkdReloadActionInactive() {
  systemctl() { [ "$1" = 'is-active' ] && echo inactive; }
  networkctl() { fail; }

  expect_err 0 _gbmc_net_networkd_reload_action 1 gbmcbr

  unset -f systemctl networkctl
}

testNetworkdReloadActionActive() {
  local calls=
  systemctl() { [ "$1" = 'is-active' ] && echo active; }
  networkctl() { calls+="$* "; }

  expect_err 0 _gbmc_net_networkd_reload_action 1 gbmcbr eth0
  expect_streq "$calls" 'reload reconfigure gbmcbr reconfigure eth0 '

  unset -f systemctl networkctl
}

testNetworkdReloadActionActivating() {
  # systemctl runs in a command substitution, so count through a file
  local count_file
  count_file="$(mktemp)"
  echo 0 >"$count_file"
  local calls=
  systemctl() {
    local n
    n="$(<"$count_file")"
    echo $(( n + 1 )) >"$count_file"
    (( n < 2 )) && { echo activating; return 0; }
    echo active
  }
  networkctl() { calls+="$* "; }
  sleep() { :; }

  # 'activating' is waited out in place rather than deferred to another process
  expect_err 0 _gbmc_net_networkd_reload_action 1 gbmcbr
  expect_streq "$calls" 'reload reconfigure gbmcbr '
  expect_numeq "$(<"$count_file")" 3

  rm -f "$count_file"
  unset -f systemctl networkctl sleep
}

testNetworkdReloadActionGivesUp() {
  local count_file
  count_file="$(mktemp)"
  echo 0 >"$count_file"
  local sleep_args=''
  systemctl() {
    echo $(( $(<"$count_file") + 1 )) >"$count_file"
    echo activating
  }
  networkctl() { fail; }
  sleep() { sleep_args+="$* "; }

  expect_err 1 _gbmc_net_networkd_reload_action 1 gbmcbr
  expect_numeq "$(<"$count_file")" 10
  expect_streq "$sleep_args" '1 1 1 1 1 1 1 1 1 '

  rm -f "$count_file"
  unset -f systemctl networkctl sleep
}

testNetworkdReloadActionRetriesReconfigure() {
  local calls='' reconfigures=0
  systemctl() { [ "$1" = 'is-active' ] && echo active; }
  networkctl() {
    calls+="$* "
    # Fail the first reconfigure only
    [ "$1" = 'reconfigure' ] && (( reconfigures += 1 )) && (( reconfigures == 1 )) && return 1
    return 0
  }
  sleep() { :; }

  expect_err 0 _gbmc_net_networkd_reload_action 1 gbmcbr
  expect_streq "$calls" 'reload reconfigure gbmcbr reload reconfigure gbmcbr '

  unset -f systemctl networkctl sleep
}

testNftablesReloadAction() {
  local calls=
  systemctl() {
    [ "$1" = 'is-active' ] && { echo active; return 0; }
    calls+="$* "
  }

  expect_err 0 _gbmc_net_nftables_reload_action 1
  expect_streq "$calls" 'reset-failed nftables --no-block reload-or-restart nftables '

  unset -f systemctl
}

testNftablesReloadActionInactive() {
  local calls=
  systemctl() {
    [ "$1" = 'is-active' ] && { echo inactive; return 0; }
    calls+="$* "
  }

  expect_err 0 _gbmc_net_nftables_reload_action 1
  expect_streq "$calls" ''

  unset -f systemctl
}

testReloadQueueUnderflow() {
  GBMC_NET_RELOAD_REFCOUNT=0
  local err
  err="$(gbmc_net_reload_queue_end 2>&1)" && fail
  if [[ "$err" != *"Reload queue refcount underflow"* ]]; then
    echo "Expected underflow error, got: $err" >&2
    fail
  fi
}

testReloadQueuedMessages() {
  GBMC_NET_RELOAD_REFCOUNT=0
  GBMC_NET_NETWORKD_RELOAD_PENDING=0
  GBMC_NET_NETWORKD_RELOAD_INTFS=()
  GBMC_NET_NFTABLES_RELOAD_PENDING=0

  gbmc_net_reload_queue_start

  local err
  err="$(gbmc_net_networkd_reload eth0 2>&1)"
  if [[ "$err" != *"Queuing networkd reload + reconfiguring (eth0) from "* ]]; then
    echo "Expected Queuing networkd reload message, got: $err" >&2
    fail
  fi

  err="$(gbmc_net_networkd_reload 2>&1)"
  if [[ "$err" != *"Queuing networkd reload from "* ]] || [[ "$err" == *"reconfiguring"* ]]; then
    echo "Expected Queuing networkd reload (no reconfigure) message, got: $err" >&2
    fail
  fi

  err="$(gbmc_net_nftables_reload 2>&1)"
  if [[ "$err" != *"Queuing nftables reload from "* ]]; then
    echo "Expected Queuing nftables reload message, got: $err" >&2
    fail
  fi

  _gbmc_net_networkd_reload_exec() { :; }
  _gbmc_net_nftables_reload_exec() { :; }
  gbmc_net_reload_queue_end

  err="$(gbmc_net_networkd_reload eth0 2>&1)"
  if [[ "$err" != *"Requesting networkd reload + reconfiguring (eth0) from "* ]]; then
    echo "Expected Requesting networkd reload message, got: $err" >&2
    fail
  fi

  err="$(gbmc_net_nftables_reload 2>&1)"
  if [[ "$err" != *"Requesting nftables reload from "* ]]; then
    echo "Expected Requesting nftables reload message, got: $err" >&2
    fail
  fi
  unset -f _gbmc_net_networkd_reload_exec _gbmc_net_nftables_reload_exec
}

testUnmaskAndWrite() {
  local test_dir
  test_dir="$(mktemp -d)"

  # Writing with contents creates parent directories and file
  gbmc_net_unmask_and_write "$test_dir/subdir/testfile" "hello world"
  local content
  content=$(<"$test_dir/subdir/testfile")
  if [ "$content" != "hello world" ]; then
    echo "Expected 'hello world', got '$content'" >&2
    fail
  fi

  # Writing multiple arguments joins with space
  gbmc_net_unmask_and_write "$test_dir/subdir/testfile" "foo" "bar" "baz"
  content=$(<"$test_dir/subdir/testfile")
  if [ "$content" != "foo bar baz" ]; then
    echo "Expected 'foo bar baz', got '$content'" >&2
    fail
  fi
  local ino
  ino="$(stat -c %i "$test_dir/subdir/testfile")"
  gbmc_net_unmask_and_write "$test_dir/subdir/testfile" "foo" "bar" "baz"
  expect_numeq "$(stat -c %i "$test_dir/subdir/testfile")" "$ino"

  # Writing without content touches the file
  gbmc_net_unmask_and_write "$test_dir/emptyfile"
  if [ ! -f "$test_dir/emptyfile" ] || [ -s "$test_dir/emptyfile" ]; then
    echo "Expected empty file at $test_dir/emptyfile" >&2
    fail
  fi

  # Mask or rm unmounted path removes file
  gbmc_net_mask_or_rm "$test_dir/emptyfile"
  if [ -e "$test_dir/emptyfile" ]; then
    echo "Expected $test_dir/emptyfile to be removed" >&2
    fail
  fi

  rm -rf "$test_dir"
}

testMaskFileSource() {
  local root
  root="$(mktemp -d)"

  # Non-systemd paths and systemd paths without a lower copy use /dev/null
  expect_streq "$(gbmc_net_mask_file_source /var/google/foo "$root")" /dev/null
  expect_streq "$(gbmc_net_mask_file_source \
    /etc/systemd/network/00-bmc-ncsi.network "$root")" /dev/null

  # Fall back to the vendor copy so the file is not masked by systemd
  mkdir -p "$root"/usr/lib/systemd/network "$root"/run/systemd/network
  touch "$root"/usr/lib/systemd/network/00-bmc-ncsi.network
  expect_streq "$(gbmc_net_mask_file_source \
    /etc/systemd/network/00-bmc-ncsi.network "$root")" \
    "$root"/usr/lib/systemd/network/00-bmc-ncsi.network

  # A runtime copy takes precedence over the vendor copy
  touch "$root"/run/systemd/network/00-bmc-ncsi.network
  expect_streq "$(gbmc_net_mask_file_source \
    /etc/systemd/network/00-bmc-ncsi.network "$root")" \
    "$root"/run/systemd/network/00-bmc-ncsi.network

  # A masked runtime copy keeps the file masked
  rm "$root"/run/systemd/network/00-bmc-ncsi.network
  ln -s /dev/null "$root"/run/systemd/network/00-bmc-ncsi.network
  expect_streq "$(gbmc_net_mask_file_source \
    /etc/systemd/network/00-bmc-ncsi.network "$root")" /dev/null

  rm -rf "$root"
}

testRuleReplace() {
  local calls=()
  ip() { calls+=("$*"); [[ $3 != add ]]; }
  local rc=0
  gbmc_net_rule_replace from 2001:db8::/64 lookup 1002 pref 1002 || rc=$?
  expect_numeq "$rc" 1
  expect_numeq "${#calls[@]}" 2
  expect_streq "${calls[0]}" '-6 rule del from 2001:db8::/64 lookup 1002 pref 1002'
  expect_streq "${calls[1]}" '-6 rule add proto static from 2001:db8::/64 lookup 1002 pref 1002'
  unset -f ip
}

testIntfRules() {
  local conf=
  gbmc_net_intf_rules_conf conf 2001:db8::/64 1003
  local want=$'[RoutingPolicyRule]\nTo=2001:db8::/64\nTable=main\nPriority=1000\n'
  want+=$'[RoutingPolicyRule]\nFrom=2001:db8::/64\nTable=1003\nPriority=1003\n'
  want+=$'[RoutingPolicyRule]\nFrom=2001:db8::/64\nType=unreachable\nPriority=1999\n'
  expect_streq "$conf" "$want"

  local calls=()
  ip() { calls+=("$*"); }
  gbmc_net_intf_rules_set add 2001:db8::/64 1003 || fail
  expect_numeq "${#calls[@]}" 6
  expect_streq "${calls[5]}" '-6 rule add proto static from 2001:db8::/64 unreachable pref 1999'
  calls=()
  gbmc_net_intf_rules_set del 2001:db8::/64 1003 || fail
  expect_numeq "${#calls[@]}" 3
  expect_streq "${calls[1]}" '-6 rule del from 2001:db8::/64 lookup 1003 pref 1003'
  expect_streq "${calls[2]}" '-6 rule del from 2001:db8::/64 unreachable pref 1999'
  unset -f ip
}

testProxyNdpAdd() {
  local calls=()
  ip() { calls+=("$*"); [[ $5 != 2001:db8::2 ]]; }
  sysctl() { calls+=("sysctl $*"); }
  local rc=0
  gbmc_net_proxy_ndp_add eth0 2001:db8::1 2001:db8::2 2001:db8::3 || rc=$?
  expect_numeq "$rc" 1
  expect_numeq "${#calls[@]}" 4
  expect_streq "${calls[0]}" 'sysctl net.ipv6.conf.eth0.proxy_ndp=1'
  expect_streq "${calls[3]}" '-6 neigh replace proxy 2001:db8::3 dev eth0'
  unset -f ip sysctl
}

testWriteIfChanged() {
  local test_dir
  test_dir="$(mktemp -d)"
  local saved_netd saved_nft
  saved_netd="$(declare -f gbmc_net_networkd_reload)"
  saved_nft="$(declare -f gbmc_net_nftables_reload)"
  local netd_reloads=0
  local nft_reloads=0
  gbmc_net_networkd_reload() { (( ++netd_reloads )); }
  gbmc_net_nftables_reload() { (( ++nft_reloads )); }

  local file="$test_dir/systemd/network/file.conf"
  local file2="$test_dir/nftables/file2.rules"
  local rc=0
  gbmc_net_write_if_changed $'a\nb\n' "$file" "$file2" || rc=$?
  expect_numeq "$rc" 0
  expect_numeq "$netd_reloads" 1
  expect_numeq "$nft_reloads" 1
  local cur
  IFS= read -r -d '' cur <"$file" || true
  expect_streq "$cur" $'a\nb\n'
  IFS= read -r -d '' cur <"$file2" || true
  expect_streq "$cur" $'a\nb\n'
  local ino
  ino="$(stat -c %i "$file")"
  # A missing trailing newline is added, so the contents still match
  rc=0
  gbmc_net_write_if_changed $'a\nb' "$file" "$file2" || rc=$?
  expect_numeq "$rc" 1
  expect_numeq "$netd_reloads" 1
  expect_numeq "$nft_reloads" 1
  expect_numeq "$(stat -c %i "$file")" "$ino"
  ! compgen -G "$file.tmp*" >/dev/null || fail
  # Empty contents remove the files
  rc=0
  gbmc_net_write_if_changed '' "$file" "$file2" || rc=$?
  expect_numeq "$rc" 0
  expect_numeq "$netd_reloads" 2
  expect_numeq "$nft_reloads" 2
  [ ! -e "$file" ] || fail
  [ ! -e "$file2" ] || fail
  rc=0
  gbmc_net_write_if_changed '' "$file" "$file2" || rc=$?
  expect_numeq "$rc" 1
  expect_numeq "$netd_reloads" 2
  expect_numeq "$nft_reloads" 2
  # A failure on one file still writes and reloads for the others
  touch "$test_dir/blocker"
  rc=0
  gbmc_net_write_if_changed 'c' "$test_dir/blocker/file" "$file" || rc=$?
  expect_numeq "$rc" 2
  expect_numeq "$netd_reloads" 3
  IFS= read -r -d '' cur <"$file" || true
  expect_streq "$cur" $'c\n'
  ! compgen -G "$test_dir/blocker*.tmp*" >/dev/null || fail
  ! compgen -G "$file.tmp*" >/dev/null || fail

  eval "$saved_netd"
  eval "$saved_nft"
  rm -rf "$test_dir"
}

testRmIfExists() {
  local test_dir
  test_dir="$(mktemp -d)"
  touch "$test_dir/a"
  local rc=0
  gbmc_net_rm_if_exists "$test_dir/a" "$test_dir/b" || rc=$?
  expect_numeq "$rc" 0
  [ ! -e "$test_dir/a" ] || fail
  rc=0
  gbmc_net_rm_if_exists "$test_dir/a" "$test_dir/b" || rc=$?
  expect_numeq "$rc" 1
  rm -rf "$test_dir"
}

testConfRouteSrc() {
  local conf
  conf=$'[Neighbor]\nMACAddress=00:11:22:33:44:55\nAddress=fe80::1\n'
  conf+=$'[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=512\nTable=1002\n'
  conf+=$'[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=1800\n'
  local out

  # Only the main table route with the gateway and metric gets the source
  gbmc_net_conf_route_src out "$conf" fe80::1 1800 2001:db8::1 || fail
  local want="${conf}PreferredSource=2001:db8::1"$'\n'
  expect_streq "$out" "$want"

  # Replacing the source keeps a single one
  gbmc_net_conf_route_src out "$out" fe80::1 1800 2001:db8::2 || fail
  expect_streq "$out" "${conf}PreferredSource=2001:db8::2"$'\n'

  # An empty source removes it
  gbmc_net_conf_route_src out "$out" fe80::1 1800 '' || fail
  expect_streq "$out" "$conf"

  # Interface table routes, other gateways and metrics are left alone
  gbmc_net_conf_route_src out "$conf" fe80::1 512 2001:db8::1 && fail
  gbmc_net_conf_route_src out "$conf" fe80::2 1800 2001:db8::1 && fail
  gbmc_net_conf_route_src out "$conf" fe80::1 800 2001:db8::1 && fail
  expect_streq "$out" "$conf"
  gbmc_net_conf_route_src out '' fe80::1 800 2001:db8::1 && fail
  expect_streq "$out" ''
}

testRouteSrcSet() {
  local test_dir
  test_dir="$(mktemp -d)"
  local GBMC_NET_ROUTE_SRC_DIR="$test_dir/state"
  # Point the networkd paths at the test directory
  local saved_set saved_dropin
  saved_set="$(declare -f gbmc_net_route_src_set)"
  saved_dropin="$(declare -f gbmc_net_networkd_write_dropin)"
  eval "${saved_set//\/run\/systemd\/network/$test_dir/network}"
  eval "${saved_dropin//\/run\/systemd\/network/$test_dir/network}"

  local dyn=$'[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=800\n'
  local lo=$'[Route]\nGateway=fe80::1\nGatewayOnLink=true\nMetric=1800\n'
  gbmc_net_gateway_conf_write front 10-gateway-dyn.conf "$dyn"
  gbmc_net_gateway_conf_write front 10-gateway.conf "$lo"
  local f cur
  for f in "$test_dir"/network/{00,}-bmc-front.network.d/10-gateway-dyn.conf; do
    IFS= read -r -d '' cur <"$f" || true
    expect_streq "$cur" "$dyn"
  done

  # The source lands in the matching drop-in of both file sets
  gbmc_net_route_src_set front fe80::1 800 2001:db8::1 || fail
  for f in "$test_dir"/network/{00,}-bmc-front.network.d/10-gateway-dyn.conf; do
    IFS= read -r -d '' cur <"$f" || true
    expect_streq "$cur" "${dyn}PreferredSource=2001:db8::1"$'\n'
  done
  for f in "$test_dir"/network/{00,}-bmc-front.network.d/10-gateway.conf; do
    IFS= read -r -d '' cur <"$f" || true
    expect_streq "$cur" "$lo"
  done
  local src
  gbmc_net_route_src_get src front fe80::1 800
  expect_streq "$src" 2001:db8::1
  gbmc_net_route_src_get src front fe80::2 800
  expect_streq "$src" ''
  gbmc_net_route_src_get src front fe80::1 1800
  expect_streq "$src" ''

  # gbmc-ra rewriting its drop-in keeps the saved source
  gbmc_net_gateway_conf_write front 10-gateway-dyn.conf "$dyn"
  IFS= read -r -d '' cur <"$test_dir/network/-bmc-front.network.d/10-gateway-dyn.conf" || true
  expect_streq "$cur" "${dyn}PreferredSource=2001:db8::1"$'\n'

  # Unknown routes report failure
  gbmc_net_route_src_set front fe80::1 900 2001:db8::1 && fail
  gbmc_net_route_src_set ncsi fe80::1 800 2001:db8::1 && fail
  [ ! -e "$GBMC_NET_ROUTE_SRC_DIR/front/900" ] || fail
  [ ! -e "$GBMC_NET_ROUTE_SRC_DIR/ncsi/800" ] || fail

  # A stale gateway at the same metric leaves the saved source alone
  gbmc_net_route_src_set front fe80::2 800 2001:db8::2 && fail
  gbmc_net_route_src_set front fe80::2 800 '' && fail
  gbmc_net_route_src_get src front fe80::1 800
  expect_streq "$src" 2001:db8::1

  # Removing the source
  gbmc_net_route_src_set front fe80::1 800 '' || fail
  IFS= read -r -d '' cur <"$test_dir/network/00-bmc-front.network.d/10-gateway-dyn.conf" || true
  expect_streq "$cur" "$dyn"
  gbmc_net_route_src_get src front fe80::1 800
  expect_streq "$src" ''

  # Empty contents remove the drop-in
  gbmc_net_gateway_conf_write front 10-gateway-dyn.conf ''
  [ ! -e "$test_dir/network/-bmc-front.network.d/10-gateway-dyn.conf" ] || fail

  eval "$saved_set"
  eval "$saved_dropin"
  rm -rf "$test_dir"
}

main
