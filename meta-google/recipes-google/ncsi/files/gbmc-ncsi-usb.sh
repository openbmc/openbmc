#!/bin/bash
# Copyright 2025 Google LLC
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

[ -n "${gbmc_ncsi_dynamic_lib-}" ] && return

gbmc_ncsi_dynamic_hook() {
  # shellcheck disable=SC2154
  if [[ "$change" = 'link' && "$action" = 'add' ]]; then
    ip link show "$intf" | grep -q '^ *alias ncsi-usb$' || return

read -r -d '' contents <<EOF
table inet filter {
    chain ${intf}_input {
        type filter hook input priority 0; policy drop;
        iifname != $intf accept
        ct state established accept
        udp dport 547 accept
        jump gbmc_br_pub_input
        reject
    }
    chain gbmc_br_pub_input {
        ip6 nexthdr icmpv6 accept
    }
    chain ${intf}_forward {
        type filter hook forward priority 0; policy drop;
        iifname != $intf accept
        oifname != gbmcbr drop
        ip6 daddr fdb5:0481:10ce::/64 drop
        ip6 saddr fdb5:0481:10ce::/64 drop
    }
}
EOF
    local rfile=/run/nftables/50-gbmc-ncsi-$intf.rules
    mkdir -p "$(dirname "$rfile")"
    printf '%s' "$contents" >"$rfile"
    # shellcheck disable=SC2015
    systemctl reset-failed nftables && systemctl --no-block reload-or-restart nftables || true

    systemctl start --no-block gbmc-ncsi-ra@"$intf"
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_ncsi_dynamic_hook)

gbmc_ncsi_dynamic_lib=1
