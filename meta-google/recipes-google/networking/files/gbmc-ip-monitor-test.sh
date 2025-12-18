#!/bin/bash
# shellcheck disable=SC2317
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

main
