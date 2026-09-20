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

# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source /usr/share/gbmc-net-lib.sh || exit

# shellcheck disable=SC2034
declare -A gbmc_br_dhcrelay_linkaddrs

gbmc_ncsi_dynamic_hook() {
  # shellcheck disable=SC2154
  local -n numaddrs=gbmc_br_dhcrelay_linkaddrs["$intf"]
  # shellcheck disable=SC2154
  if [[ "$change" = 'link' && "$action" = 'add' ]]; then
    ip link show "$intf" | grep -q '^ *alias ncsi-usb-side$' || return
    echo "NCSI USB Link Add $intf" >&2

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
    local rfile=/run/nftables/50-gbmc-ncsi-"$intf".rules
    mkdir -p "$(dirname "$rfile")"
    printf '%s' "$contents" >"$rfile"
    gbmc_net_nftables_reload || true

    systemctl start --no-block gbmc-ncsi-ra@"$intf"
    numaddrs=${numaddrs-0}
  elif [[ "$change" = 'link' && "$action" = 'del' ]]; then
    [[ -n "${numaddrs-}" ]] || return 0
    echo "NCSI USB Link Del $intf" >&2
    if rm /run/nftables/50-gbmc-ncsi-"$intf".rules 2>/dev/null; then
      gbmc_net_nftables_reload || true
    fi
    systemctl stop --no-block gbmc-ncsi-ra@"$intf" || true
    rm -f /run/gbmc-br-dhcrelay/uppers/"$intf"
    unset numaddrs
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_ncsi_dynamic_hook)

gbmc_ncsi_dynamic_lib=1
