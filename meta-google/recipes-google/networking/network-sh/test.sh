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

cd "$(dirname "$0")" || exit
if [ -e ../network-sh.bb ]; then
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source '../../test/test-sh/lib.sh'
else
  # shellcheck source=meta-google/recipes-google/test/test-sh/lib.sh
  source "$SYSROOT/usr/share/test/lib.sh"
fi
# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source lib.sh

expect_array_numeq() {
  local -n a1="$1"
  local -n a2="$2"

  if (( "${#a1[@]}" != "${#a2[@]}" )); then
    echo "  Line ${BASH_LINENO[0]} Array Size ${#a1[@]} != ${#a2[@]}" >&2
    test_err=1
  else
    local i
    for (( i=0; i < ${#a1[@]}; ++i )); do
      expect_numeq "${a1[$i]}" "${a2[$i]}"
    done
  fi
}

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
  expect_array_numeq out expected
}

test_mac_to_eui48() {
  str="$(mac_to_eui48 '12:34:56:78:90:af')" || fail
  expect_streq "$str" '::1234:5678:90af'
}

test_mac_to_eui64() {
  str="$(mac_to_eui64 '12:34:56:78:90:af')" || fail
  expect_streq "$str" '::1034:56ff:fe78:90af'
}

test_mac_to_eui64_tweak() {
  str="$(mac_to_eui64 '12:34:56:78:90:af' '0x11' '0x22')" || fail
  expect_streq "$str" '::1034:5611:2278:90af'
}

test_ip4_to_bytes() {
  out=()
  expect_err 1 ip_to_bytes out ''
  expect_err 1 ip_to_bytes out '10.0.0.'
  expect_err 1 ip_to_bytes out '.0.1.1'
  expect_err 1 ip_to_bytes out '10.0.0'
  expect_err 1 ip_to_bytes out '10.0..0'
  expect_err 1 ip_to_bytes out '.10.0.0.0'
  expect_err 1 ip_to_bytes out '10.0.0.0.'
  expect_err 1 ip_to_bytes out '10.0.0.256'
  expect_err 1 ip_to_bytes out '10.0.0.0.256'
  expect_err 1 ip_to_bytes out '10.0.0.0.1'

  expect_err 0 ip_to_bytes out '10.0.0.1'
  expected=(10 0 0 1)
  expect_array_numeq out expected
}

test_ip6_to_bytes() {
  out=()
  expect_err 1 ip_to_bytes out ''
  expect_err 1 ip_to_bytes out ':::'
  expect_err 1 ip_to_bytes out '::z'
  expect_err 1 ip_to_bytes out '1::1::1'
  expect_err 1 ip_to_bytes out '1:1:1'
  expect_err 1 ip_to_bytes out ':1::1'
  expect_err 1 ip_to_bytes out '1::1:'

  expect_err 0 ip_to_bytes out '::'
  expected=(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
  expect_array_numeq out expected
  out=()

  expect_err 0 ip_to_bytes out '::1'
  expected=(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1)
  expect_array_numeq out expected
  out=()

  expect_err 0 ip_to_bytes out 'fd00::'
  expected=(0xfd 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
  expect_array_numeq out expected
  out=()

  expect_err 0 ip_to_bytes out 'fd00:ffee::ddff:22'
  expected=(0xfd 0 0xff 0xee 0 0 0 0 0 0 0 0 0xdd 0xff 0 0x22)
  expect_array_numeq out expected
  out=()

  expect_err 0 ip_to_bytes out '1:2:3:4:5:6:7:8'
  expected=(0 1 0 2 0 3 0 4 0 5 0 6 0 7 0 8)
  expect_array_numeq out expected
  # shellcheck disable=SC2034
  out=()
}

test_ip4_bytes_str() {
  in=(10 0 255 1)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" '10.0.255.1'
}

test_ip6_bytes_str() {
  in=(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" '::'
  in=(0xfd 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" 'fd00::'
  in=(0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0xfd)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" '::fd'
  in=(0xfd 1 0 0 0 0 0 0 0 0 0 0 0 0 0 1)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" 'fd01::1'
  in=(0xfd 1 0 0 0 0 0 0 0 1 0 0 0 0 0 1)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" 'fd01::1:0:0:1'
  in=(0xfd 1 0 0 0 0 0 1 0 1 0 0 0 0 0 1)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" 'fd01:0:0:1:1::1'
  # shellcheck disable=SC2034
  in=(0 1 0 1 0xdd 0xdd 0 1 0 1 0 1 0 1 0 1)
  str="$(ip_bytes_to_str in)" || fail
  expect_streq "$str" '1:1:dddd:1:1:1:1:1'
}

test_ip_pfx_concat() {
  # Invalid inputs
  expect_err 1 ip_pfx_concat 'fd/64' '::1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01::' '::1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01:' '::1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01::/a0' '::1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01::/64' ':1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01::/64' ''
  expect_err 1 ip_pfx_concat 'fd01::/129' '::1'

  # Too many address bits
  expect_err 1 ip_pfx_concat 'fd01:1:1:1:1::/64' '::1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01::/64' '::1:0:1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01::/79' '::3:1234:5678:90af'
  expect_err 1 ip_pfx_concat 'fd01::/15' '::3:1234:5678:90af'
  expect_err 1 ip_pfx_concat '10.0.0.1/31' '0.0.0.0'

  str="$(ip_pfx_concat '::1/128' '::0')" || fail
  expect_streq "$str" '::1/128'
  str="$(ip_pfx_concat 'fd01::/64' '::1')" || fail
  expect_streq "$str" 'fd01::1/64'
  str="$(ip_pfx_concat 'fd01::/127' '::1')" || fail
  expect_streq "$str" 'fd01::1/127'
  str="$(ip_pfx_concat 'fd02::/15' '::1')" || fail
  expect_streq "$str" 'fd02::1/15'
  str="$(ip_pfx_concat 'fd01::/72' '::1234:5678:90af')" || fail
  expect_streq "$str" 'fd01::1234:5678:90af/72'
  str="$(ip_pfx_concat 'fd01:eeee:aaaa:cccc::/64' '::a:1234:5678:90af')" || fail
  expect_streq "$str" 'fd01:eeee:aaaa:cccc:a:1234:5678:90af/64'
  str="$(ip_pfx_concat 'fd01::fd00:0:0:0/80' '::1')" || fail
  expect_streq "$str" 'fd01::fd00:0:0:1/80'

  str="$(ip_pfx_concat '10.0.0.0/24' '0.0.0.1')" || fail
  expect_streq "$str" '10.0.0.1/24'
}

test_ip_pfx_to_cidr() {
  expect_err 1 ip_pfx_to_cidr 'z/64'
  expect_err 1 ip_pfx_to_cidr '64'

  cidr="$(ip_pfx_to_cidr 'fd01::/64')" || fail
  expect_numeq "$cidr" 64
  cidr="$(ip_pfx_to_cidr 'fd01:eeee:aaaa:cccc:a:1234:5678:90af/128')" || fail
  expect_numeq "$cidr" 128
  cidr="$(ip_pfx_to_cidr '10.0.0.1/24')" || fail
  expect_numeq "$cidr" 24
}

test_normalize_ip() {
  ip="$(normalize_ip 'fd01:1::0:0:1')" || fail
  expect_streq "$ip" 'fd01:1::1'
}

main
