#!/bin/bash
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

cd "$(dirname "$0")"
if [ -e ../network-sh.bb ]; then
  source '../../test/test-sh/lib.sh'
else
  source "$SYSROOT/usr/share/test/lib.sh"
fi
source lib.sh

test_mac_to_bytes() {
  out=()
  expect_err 1 mac_to_bytes out ''
  expect_err 1 mac_to_bytes out '00'
  expect_err 1 mac_to_bytes out '12:34:56:78:90:'
  expect_err 1 mac_to_bytes out ':12:34:56:78:90'
  expect_err 1 mac_to_bytes out '12:34:56:78:90:0:'
  expect_err 1 mac_to_bytes out '12:34:56:78:90:0:2'

  expect_err 0 mac_to_bytes out 'a2:0:f:de:0:29'
  expected=(0xa2 0 0xf 0xde 0 0x29)
  for (( i=0; i < ${#expected[@]}; ++i )); do
    expect_numeq "${out[$i]}" "${expected[$i]}"
  done
}

test_mac_to_eui_48() {
  str="$(mac_to_eui48 '12:34:56:78:90:af')" || fail
  expect_streq "$str" '1234:5678:90af'
}

test_eui_64() {
  str="$(mac_to_eui64 '12:34:56:78:90:af')" || fail
  expect_streq "$str" '1334:56ff:fe78:90af'
}

test_ipv6_pfx_concat() {
  # Invalid inputs
  expect_err 1 ipv6_pfx_concat 'fd/64' '1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01::' '1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01:' '1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01::/a0' '1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01::/64' ':1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01::/64' '::'

  # Too many address bits
  expect_err 1 ipv6_pfx_concat 'fd01:1:1:1:1::/64' '1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01::/64' '1:0:1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01::/65' '1:1234:5678:90af'
  expect_err 1 ipv6_pfx_concat 'fd01::/72' '1:1234:5678:90af'

  str="$(ipv6_pfx_concat 'fd01::/64' '1')" || fail
  expect_streq "$str" 'fd01::1/64'
  str="$(ipv6_pfx_concat 'fd01::/72' '1234:5678:90af')" || fail
  expect_streq "$str" 'fd01::1234:5678:90af/72'
  str="$(ipv6_pfx_concat 'fd01:eeee:aaaa:cccc::/64' 'a:1234:5678:90af')" || fail
  expect_streq "$str" 'fd01:eeee:aaaa:cccc:a:1234:5678:90af/64'
}

test_ipv6_pfx_to_cidr() {
  expect_err 1 ipv6_pfx_to_cidr 'z/64'
  expect_err 1 ipv6_pfx_to_cidr '64'

  cidr="$(ipv6_pfx_to_cidr 'fd01::/64')" || fail
  expect_numeq "$cidr" 64
  cidr="$(ipv6_pfx_to_cidr 'fd01:eeee:aaaa:cccc:a:1234:5678:90af/128')" || fail
  expect_numeq "$cidr" 128
}

return 0 2>/dev/null
main
