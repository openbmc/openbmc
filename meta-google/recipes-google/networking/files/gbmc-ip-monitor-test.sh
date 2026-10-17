#!/bin/bash
# shellcheck disable=SC2317
# shellcheck disable=SC2329
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

cd "$(dirname "$0")" || exit
if [ -e ../gbmc-ip-monitor.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
fi
# shellcheck source=meta-google/recipes-google/networking/files/gbmc-ip-monitor.sh
source gbmc-ip-monitor.sh

# shellcheck disable=SC2329
test_init_empty() {
  ip() {
    return 0
  }
  str="$(gbmc_ip_monitor_generate_init)" || fail
  expect_streq "$str" '[INIT]'
}

# shellcheck disable=SC2329
test_init_link_populated() {
  ip() {
    if [ "$1" = 'link' ]; then
      cat <<EOF
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN mode DEFAULT group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
2: eno2: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP mode DEFAULT group default qlen 1000
    link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff
    altname enp0s31f6
EOF
    fi
    return 0
  }
  str="$(gbmc_ip_monitor_generate_init)" || fail
  expect_streq "$str" <<EOF
[LINK]1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN mode DEFAULT group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
[LINK]2: eno2: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP mode DEFAULT group default qlen 1000
    link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff
    altname enp0s31f6
[INIT]
EOF
}

# shellcheck disable=SC2329
test_init_addr_populated() {
  ip() {
    if [ "$1" = 'addr' ]; then
      cat <<EOF
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host
       valid_lft forever preferred_lft forever
2: eno2: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP group default qlen 1000
    link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff
    altname enp0s31f6
    inet 192.168.242.57/23 brd 192.168.243.255 scope global dynamic noprefixroute eno2
       valid_lft 83967sec preferred_lft 83967sec
    inet6 fd01:ff2:5687:4:cf03:45f3:983a:96eb/64 scope global temporary dynamic
       valid_lft 518788sec preferred_lft 183sec
EOF
    fi
    return 0
  }
  str="$(gbmc_ip_monitor_generate_init)" || fail
  expect_streq "$str" <<EOF
[ADDR]1: lo inet 127.0.0.1/8 scope host lo
[ADDR]1: lo inet6 ::1/128 scope host
[ADDR]2: eno2 inet 192.168.242.57/23 brd 192.168.243.255 scope global dynamic noprefixroute eno2
[ADDR]2: eno2 inet6 fd01:ff2:5687:4:cf03:45f3:983a:96eb/64 scope global temporary dynamic
[INIT]
EOF
}

# shellcheck disable=SC2329
test_init_route_populated() {
  ip() {
    if [[ "$1" == "-4" && "${2-}" == 'route' ]]; then
      cat <<EOF
default via 192.168.243.254 dev eno2 proto dhcp metric 100
192.168.242.0/23 dev eno2 proto kernel scope link src 192.168.242.57 metric 100
EOF
    elif [[ "$1" == "-6" && "${2-}" == 'route' ]]; then
      cat <<EOF
::1 dev lo proto kernel metric 256 pref medium
fd01:ff2:5687:4::/64 dev eno2 proto ra metric 100 pref medium
fe80::/64 dev eno2 proto kernel metric 100 pref medium
default via fe80::1234 dev gbmcbr table 1100 proto static metric 512 onlink pref medium
local ::1 dev lo table local proto kernel metric 0 pref medium
EOF
    fi
    return 0
  }
  str="$(gbmc_ip_monitor_generate_init)" || fail
  expect_streq "$str" <<EOF
[ROUTE]default via 192.168.243.254 dev eno2 proto dhcp metric 100
[ROUTE]192.168.242.0/23 dev eno2 proto kernel scope link src 192.168.242.57 metric 100
[ROUTE]::1 dev lo proto kernel metric 256 pref medium
[ROUTE]fd01:ff2:5687:4::/64 dev eno2 proto ra metric 100 pref medium
[ROUTE]fe80::/64 dev eno2 proto kernel metric 100 pref medium
[ROUTE]default via fe80::1234 dev gbmcbr table 1100 proto static metric 512 onlink pref medium
[INIT]
EOF
}

# shellcheck disable=SC2329
testParseNonTag() {
  expect_err 2 gbmc_ip_monitor_parse_line ''
  expect_err 2 gbmc_ip_monitor_parse_line '  '
  expect_err 2 gbmc_ip_monitor_parse_line '  Data'
  expect_err 2 gbmc_ip_monitor_parse_line '  [LINK]'
  expect_err 2 gbmc_ip_monitor_parse_line '  [ROUTE]'
}

# shellcheck disable=SC2329
testParseInit() {
  expect_err 0 gbmc_ip_monitor_parse_line '[INIT]'
  expect_streq "$change" 'init'
}

# shellcheck disable=SC2329
testParseAddrAdd() {
  expect_err 0 gbmc_ip_monitor_parse_line '[ADDR]2: eno2@extra inet6 fd01:ff2:5687:4:cf03:45f3:983a:96eb/128 metric 1024 scope global temporary dynamic'
  expect_streq "$change" 'addr'
  expect_streq "$action" 'add'
  expect_streq "$intf" 'eno2'
  expect_streq "$fam" 'inet6'
  expect_streq "$ip" 'fd01:ff2:5687:4:cf03:45f3:983a:96eb'
  expect_streq "$scope" 'global'
  expect_streq "$flags" 'temporary dynamic'
}

# shellcheck disable=SC2329
testParseAddrDel() {
  expect_err 0 gbmc_ip_monitor_parse_line '[ADDR]Deleted 2: eno2 inet6 fe80::aaaa:aaff:feaa:aaaa/64 scope link'
  expect_streq "$change" 'addr'
  expect_streq "$action" 'del'
  expect_streq "$intf" 'eno2'
  expect_streq "$fam" 'inet6'
  expect_streq "$ip" 'fe80::aaaa:aaff:feaa:aaaa'
  expect_streq "$scope" 'link'
  expect_streq "$flags" ''
}

# shellcheck disable=SC2329
testParseRouteAdd() {
  expect_err 0 gbmc_ip_monitor_parse_line "[ROUTE]fd01:ff2:5687:4::/64 dev eno2 proto ra metric 100 pref medium"
  expect_streq "$change" 'route'
  expect_streq "$action" 'add'
  expect_streq "$route" 'fd01:ff2:5687:4::/64 dev eno2 proto ra metric 100 pref medium'
}

# shellcheck disable=SC2329
testParseRouteDel() {
  expect_err 0 gbmc_ip_monitor_parse_line "[ROUTE]Deleted fd01:ff2:5687:4::/64 dev eno2 proto ra metric 100 pref medium"
  expect_streq "$change" 'route'
  expect_streq "$action" 'del'
  expect_streq "$route" 'fd01:ff2:5687:4::/64 dev eno2 proto ra metric 100 pref medium'
}

# shellcheck disable=SC2329
testParseRuleAdd() {
  expect_err 0 gbmc_ip_monitor_parse_line $'[RULE]1003:\tfrom 2002:a05:6469:4004:fd01::/76 lookup 1003 proto static'
  expect_streq "$change" 'rule'
  expect_streq "$action" 'add'
  expect_streq "$rule" $'1003:\tfrom 2002:a05:6469:4004:fd01::/76 lookup 1003 proto static'
}

# shellcheck disable=SC2329
testParseRuleDel() {
  expect_err 0 gbmc_ip_monitor_parse_line $'[RULE]Deleted 1003:\tfrom 2002:a05:6469:4004:fd01::/76 lookup 1003 proto static'
  expect_streq "$change" 'rule'
  expect_streq "$action" 'del'
  expect_streq "$rule" $'1003:\tfrom 2002:a05:6469:4004:fd01::/76 lookup 1003 proto static'
}

# shellcheck disable=SC2329
testParseLinkAdd() {
  expect_err 0 gbmc_ip_monitor_parse_line "[LINK]2: eno2@extra: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP mode DEFAULT group default qlen 1000" \
    < <(echo 'link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff')
  expect_streq "$change" 'link'
  expect_streq "$action" 'add'
  expect_streq "$intf" 'eno2'
  expect_streq "$mac" 'aa:aa:aa:aa:aa:aa'
  expect_streq "$carrier" 'UP'
}

# shellcheck disable=SC2329
testParseLinkDel() {
  expect_err 0 gbmc_ip_monitor_parse_line "[LINK]Deleted 2: eno2: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc pfifo_fast state UP mode DEFAULT group default qlen 1000" \
    < <(echo 'link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff')
  expect_streq "$change" 'link'
  expect_streq "$action" 'del'
  expect_streq "$intf" 'eno2'
  expect_streq "$mac" 'aa:aa:aa:aa:aa:aa'
  expect_streq "$carrier" 'UP'
}

# shellcheck disable=SC2329
testParseLinkUsb() {
  expect_err 0 gbmc_ip_monitor_parse_line "[LINK]20: gusbem0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master gbmcbr state UP" \
    < <(echo 'link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff')
  expect_streq "$change" 'link'
  expect_streq "$action" 'add'
  expect_streq "$intf" 'gusbem0'
  expect_streq "$mac" 'aa:aa:aa:aa:aa:aa'
  expect_streq "$carrier" 'UP'
}

# Mocks the clock and whether events are pending, see mock_restore
mock_setup() {
  mock_saved="$(declare -f gbmc_ip_monitor_uptime_cs gbmc_ip_monitor_input_pending)"
  mock_cs=10000
  mock_busy=
  gbmc_ip_monitor_uptime_cs() {
    local -n out="$1"
    # shellcheck disable=SC2034
    out=$mock_cs
  }
  gbmc_ip_monitor_input_pending() {
    [[ -n $mock_busy ]]
  }
  GBMC_IP_MONITOR_TIMERS=()
  GBMC_IP_MONITOR_IDLE=()
  GBMC_IP_MONITOR_DEFER_OUTSTANDING=
  GBMC_IP_MONITOR_HOOKS=()
  hook_calls=()
  hook_record() {
    hook_calls+=("$change${timer:+:$timer}")
  }
}
mock_restore() {
  GBMC_IP_MONITOR_TIMERS=()
  GBMC_IP_MONITOR_IDLE=()
  GBMC_IP_MONITOR_DEFER_OUTSTANDING=
  GBMC_IP_MONITOR_HOOKS=()
  eval "$mock_saved"
}

testParseNoTokens() {
  change=
  expect_err 2 gbmc_ip_monitor_parse_line '[DEFER]'
  expect_err 2 gbmc_ip_monitor_parse_line '[TIMER]foo 10'
  expect_streq "$change" ''
}

testUptime() {
  local cs s
  gbmc_ip_monitor_uptime_cs cs || fail
  gbmc_ip_monitor_uptime s || fail
  expect_numeq "$s" $(( cs / 100 ))
}

testTimerArm() {
  mock_setup
  GBMC_IP_MONITOR_HOOKS=(hook_record)
  gbmc_ip_monitor_timer foo 30
  expect_streq "${GBMC_IP_MONITOR_TIMERS[foo]}" '13000'
  # A later deadline keeps the pending timer
  gbmc_ip_monitor_timer foo 60
  expect_streq "${GBMC_IP_MONITOR_TIMERS[foo]}" '13000'
  # An earlier deadline replaces it
  gbmc_ip_monitor_timer foo 10
  expect_streq "${GBMC_IP_MONITOR_TIMERS[foo]}" '11000'
  # Not due yet
  mock_cs=10999
  gbmc_ip_monitor_run_due
  expect_streq "${hook_calls[*]}" ''
  # Fires on time even with events pending
  mock_cs=11000
  mock_busy=1
  timer=
  gbmc_ip_monitor_run_due
  expect_streq "${hook_calls[*]}" 'timer:foo'
  expect_streq "${GBMC_IP_MONITOR_TIMERS[foo]-unset}" 'unset'
  # Cancelled timers don't fire
  gbmc_ip_monitor_timer foo 0
  gbmc_ip_monitor_timer_cancel foo
  gbmc_ip_monitor_run_due
  expect_streq "${hook_calls[*]}" 'timer:foo'
  mock_restore
}

testTimerRearm() {
  mock_setup
  # Hooks can re-arm the timer that fired, and cancel ones not fired yet
  hook_rearm() {
    [[ $change == timer ]] || return 0
    hook_calls+=("$timer")
    if [[ $timer == a ]]; then
      gbmc_ip_monitor_timer a 5
      gbmc_ip_monitor_timer_cancel b
      gbmc_ip_monitor_timer_cancel c
    fi
  }
  GBMC_IP_MONITOR_HOOKS=(hook_rearm)
  GBMC_IP_MONITOR_TIMERS=([a]=9000 [b]=9000 [c]=9000)
  gbmc_ip_monitor_run_due
  # Whichever fires first, a always fires and is re-armed
  [[ " ${hook_calls[*]} " == *' a '* ]] || fail
  expect_streq "${GBMC_IP_MONITOR_TIMERS[a]}" '10500'
  mock_restore
}

testIdle() {
  mock_setup
  GBMC_IP_MONITOR_HOOKS=(hook_record)
  gbmc_ip_monitor_idle foo 1
  expect_streq "${GBMC_IP_MONITOR_IDLE[foo]}" '10100'
  gbmc_ip_monitor_idle foo 5
  expect_streq "${GBMC_IP_MONITOR_IDLE[foo]}" '10100'
  # Waits for the pending events
  mock_busy=1
  timer=
  gbmc_ip_monitor_run_due
  expect_streq "${hook_calls[*]}" ''
  # Fires once they drained
  mock_busy=
  gbmc_ip_monitor_run_due
  expect_streq "${hook_calls[*]}" 'timer:foo'
  expect_streq "${GBMC_IP_MONITOR_IDLE[foo]-unset}" 'unset'
  # But no later than its deadline
  gbmc_ip_monitor_idle bar 1
  mock_busy=1
  mock_cs=10100
  gbmc_ip_monitor_run_due
  expect_streq "${hook_calls[*]}" 'timer:foo timer:bar'
  mock_restore
}

testTimeout() {
  mock_setup
  local t
  gbmc_ip_monitor_timeout t
  expect_streq "$t" ''
  GBMC_IP_MONITOR_TIMERS=([a]=10500)
  gbmc_ip_monitor_timeout t
  expect_streq "$t" '5.00'
  GBMC_IP_MONITOR_IDLE=([b]=10250)
  gbmc_ip_monitor_timeout t
  expect_streq "$t" '2.50'
  GBMC_IP_MONITOR_DEFER_OUTSTANDING=1
  GBMC_IP_MONITOR_DEFER_DUE=10100
  GBMC_IP_MONITOR_DEFER_LATEST=10300
  gbmc_ip_monitor_timeout t
  expect_streq "$t" '1.00'
  # Past the quiet period it waits for the latest deadline
  mock_cs=10150
  gbmc_ip_monitor_timeout t
  expect_streq "$t" '1.00'
  GBMC_IP_MONITOR_IDLE=()
  gbmc_ip_monitor_timeout t
  expect_streq "$t" '1.50'
  # Never 0, which `read -t` takes as a poll
  mock_cs=20000
  gbmc_ip_monitor_timeout t
  expect_streq "$t" '0.01'
  mock_restore
}

testGrace() {
  mock_setup
  declare -gA test_grace=()
  test_expired=()
  test_grace_cb() {
    test_expired+=("$1")
    return 1
  }

  gbmc_ip_monitor_grace_start test_grace a 5 test-grace
  expect_streq "${test_grace[a]}" '105'
  expect_streq "${GBMC_IP_MONITOR_TIMERS[test-grace]}" '10500'
  # A pending deadline is kept
  mock_cs=10200
  gbmc_ip_monitor_grace_start test_grace a 5 test-grace
  gbmc_ip_monitor_grace_start test_grace b 5 test-grace
  expect_streq "${test_grace[a]}" '105'
  expect_streq "${test_grace[b]}" '107'
  # Cleared keys don't expire
  gbmc_ip_monitor_grace_start test_grace c 1 test-grace
  gbmc_ip_monitor_grace_clear test_grace c
  expect_streq "${test_grace[c]-unset}" 'unset'

  # Nothing expired yet
  gbmc_ip_monitor_grace_expire test_grace test-grace test_grace_cb
  expect_streq "${#test_expired[@]}" '0'
  expect_streq "${GBMC_IP_MONITOR_TIMERS[test-grace]}" '10300'

  # Only a expires, the timer is re-armed for b once it fired
  mock_cs=10500
  gbmc_ip_monitor_timer_cancel test-grace
  gbmc_ip_monitor_grace_expire test_grace test-grace test_grace_cb
  expect_streq "${test_expired[*]}" 'a'
  expect_streq "${test_grace[a]-unset}" 'unset'
  expect_streq "${test_grace[b]}" '107'
  expect_streq "${GBMC_IP_MONITOR_TIMERS[test-grace]}" '10700'

  # A timer firing in the middle of a second never ends the grace period
  # early
  mock_cs=10699
  gbmc_ip_monitor_grace_expire test_grace test-grace test_grace_cb
  expect_streq "${test_expired[*]}" 'a'

  # Callback failures are ignored, the timer is cancelled once empty
  mock_cs=20000
  gbmc_ip_monitor_grace_expire test_grace test-grace test_grace_cb
  expect_streq "${test_expired[*]}" 'a b'
  expect_streq "${#test_grace[@]}" '0'
  expect_streq "${GBMC_IP_MONITOR_TIMERS[test-grace]-unset}" 'unset'

  unset test_grace test_expired
  mock_restore
}

testReloadCoalesceLive() {
  mock_setup
  local reload_exec_count=0
  _gbmc_net_networkd_reload_exec() {
    (( reload_exec_count += 1 ))
  }
  local refs="$GBMC_NET_RELOAD_REFCOUNT"

  gbmc_ip_monitor_init_done=1
  hook_reload() {
    [[ $change == link || $change == addr ]] || return 0
    gbmc_net_networkd_reload "$intf"
  }
  GBMC_IP_MONITOR_HOOKS=(hook_reload)

  change='link'
  intf=eth0
  gbmc_ip_monitor_run_hooks
  expect_streq "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" '1'
  expect_streq "$reload_exec_count" '0'

  # Burst: another event arrives during deferral window
  mock_cs=10050
  change=addr
  intf=gbmcbr
  gbmc_ip_monitor_run_hooks
  expect_streq "$reload_exec_count" '0'
  # Merged into the pending deferral, which keeps its deadlines
  expect_streq "$GBMC_IP_MONITOR_DEFER_DUE" '10100'

  # The quiet period isn't over
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '0'
  # Over, but more events are pending
  mock_cs=10100
  mock_busy=1
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '0'
  expect_streq "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" '1'
  # Drained
  mock_busy=
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '1'
  expect_streq "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" ''
  expect_numeq "$GBMC_NET_RELOAD_REFCOUNT" "$refs"

  # The next batch gets its own deferral, which a backlog only holds back
  # until its latest deadline
  change='link'
  intf=eth1
  gbmc_ip_monitor_run_hooks
  expect_streq "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" '1'
  mock_busy=1
  mock_cs=10399
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '1'
  mock_cs=10400
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '2'
  expect_numeq "$GBMC_NET_RELOAD_REFCOUNT" "$refs"
  mock_restore
}

testReloadDeferAfterIdle() {
  mock_setup
  local reload_exec_count=0
  _gbmc_net_networkd_reload_exec() {
    (( reload_exec_count += 1 ))
  }
  local refs="$GBMC_NET_RELOAD_REFCOUNT"
  gbmc_ip_monitor_init_done=1
  hook_idle_reload() {
    if [[ $change == timer && $timer == recompute ]]; then
      gbmc_net_networkd_reload gbmcbr
    elif [[ $change == link ]]; then
      gbmc_net_networkd_reload eth0
      gbmc_ip_monitor_idle recompute 5
    fi
  }
  GBMC_IP_MONITOR_HOOKS=(hook_idle_reload)
  change='link'
  gbmc_ip_monitor_run_hooks
  mock_cs=10100
  # The deferral and the idle request are both due, the reload requested by
  # the latter is flushed along with the first
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '1'
  expect_streq "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" ''
  expect_numeq "$GBMC_NET_RELOAD_REFCOUNT" "$refs"

  # An idle request on its own starts a deferral for its reloads
  timer=
  gbmc_ip_monitor_idle recompute 5
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '1'
  expect_streq "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" '1'
  mock_cs=10200
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '2'
  expect_numeq "$GBMC_NET_RELOAD_REFCOUNT" "$refs"
  mock_restore
}

testReloadCoalesceInit() {
  mock_setup
  local reload_exec_count=0
  _gbmc_net_networkd_reload_exec() {
    (( reload_exec_count += 1 ))
  }
  systemd-notify() { :; }

  gbmc_net_reload_queue_start
  gbmc_ip_monitor_init_done=

  hook_init_reload() {
    if [ "$change" = 'link' ]; then
      gbmc_net_networkd_reload eth0
    elif [ "$change" = 'init' ]; then
      gbmc_ip_monitor_defer
    fi
  }
  GBMC_IP_MONITOR_HOOKS=(hook_init_reload)

  gbmc_ip_monitor_handle_line '[LINK]2: eth0: <UP> mtu 1500 state UP' \
    < <(echo 'link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff')
  expect_streq "$reload_exec_count" '0'

  gbmc_ip_monitor_handle_line '[INIT]'
  expect_streq "$gbmc_ip_monitor_init_done" '1'
  expect_streq "$reload_exec_count" '0'
  expect_streq "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" '1'

  mock_cs=10100
  gbmc_ip_monitor_run_due
  expect_streq "$reload_exec_count" '1'
  unset -f systemd-notify
  mock_restore
}

testLoop() {
  mock_setup
  # The real check, on the stream below
  gbmc_ip_monitor_input_pending() { read -r -t 0; }
  systemd-notify() { hook_calls+=(ready); }
  hook_loop() {
    hook_record
    [[ $change == init ]] && gbmc_ip_monitor_timer after-init 0
    [[ $change == link ]] && gbmc_ip_monitor_idle drained 5
    return 0
  }
  GBMC_IP_MONITOR_HOOKS=(hook_loop)
  gbmc_ip_monitor_init_done=
  gbmc_net_reload_queue_start
  timer=
  gbmc_ip_monitor_loop < <(printf '%s\n' \
    '[LINK]2: eth0: <UP> mtu 1500 state UP' \
    '    link/ether aa:aa:aa:aa:aa:aa brd ff:ff:ff:ff:ff:ff' \
    '[LINK]3: eth1: <UP> mtu 1500 state UP' \
    '    link/ether aa:aa:aa:aa:aa:ab brd ff:ff:ff:ff:ff:ff' \
    '[INIT]'; sleep 0.5)
  # The idle request waits for the whole burst
  expect_streq "${hook_calls[*]}" 'link link init ready timer:after-init timer:drained'

  # A read timeout mid-line at a space boundary must not strip the space
  local seen_routes=()
  hook_partial() {
    [[ $change == route ]] && seen_routes+=("$route")
  }
  GBMC_IP_MONITOR_HOOKS=(hook_partial)
  gbmc_ip_monitor_timer partial-test 0
  gbmc_ip_monitor_loop < <(
    printf '[ROUTE]2002:a05:6469:4046:fd02::/80 via '
    sleep 0.1
    printf 'fe80::1 dev gbmcbr proto ra metric 1056\n'
  )
  expect_streq "${seen_routes[*]}" \
    '2002:a05:6469:4046:fd02::/80 via fe80::1 dev gbmcbr proto ra metric 1056'

  unset -f systemd-notify
  mock_restore
}

testNftablesReloadCoalesceInit() {
  local nft_exec_count=0
  local net_exec_count=0
  _gbmc_net_nftables_reload_exec() {
    (( nft_exec_count += 1 ))
  }
  _gbmc_net_networkd_reload_exec() {
    (( net_exec_count += 1 ))
  }

  gbmc_net_reload_queue_start
  gbmc_ip_monitor_init_done=
  GBMC_IP_MONITOR_HOOKS=()

  hook_nft_init() {
    if [ "$change" = 'link' ]; then
      gbmc_net_nftables_reload
      gbmc_net_networkd_reload eth0
    elif [ "$change" = 'addr' ]; then
      gbmc_net_nftables_reload
    fi
  }
  GBMC_IP_MONITOR_HOOKS+=(hook_nft_init)

  change='link'
  gbmc_ip_monitor_run_hooks
  expect_streq "$nft_exec_count" '0'
  expect_streq "$net_exec_count" '0'

  change='addr'
  gbmc_ip_monitor_run_hooks
  expect_streq "$nft_exec_count" '0'
  expect_streq "$net_exec_count" '0'

  change=init
  gbmc_ip_monitor_run_hooks
  gbmc_ip_monitor_init_done=1
  gbmc_net_reload_queue_end

  expect_streq "$nft_exec_count" '1'
  expect_streq "$net_exec_count" '1'
}


main
