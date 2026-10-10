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

    local contents
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
    gbmc_net_write_if_changed "$contents" \
      /run/nftables/50-gbmc-ncsi-"$intf".rules || true

    gbmc_ncsi_usb_network "$intf"

    systemctl start --no-block gbmc-ncsi-ra@"$intf"
    numaddrs=${numaddrs-0}
  elif [[ "$change" = 'link' && "$action" = 'del' ]]; then
    [[ -n "${numaddrs-}" ]] || return 0
    echo "NCSI USB Link Del $intf" >&2
    gbmc_net_rm_if_exists \
      /run/nftables/50-gbmc-ncsi-"$intf".rules \
      /run/systemd/network/-bmc-"$intf".network || true
    systemctl stop --no-block gbmc-ncsi-ra@"$intf" || true
    rm -f /run/gbmc-br-dhcrelay/uppers/"$intf"
    unset numaddrs
  fi
}

# The USB NCSI links all match the shared -bmc-gbmcncsiusb.network, so the
# per-interface drop-ins in -bmc-<intf>.network.d that hold the RA gateway
# routes, proxy NDP and policy rules never apply. Their routes and settings
# then only exist because they were also applied with ip(8), and networkd
# drops them as foreign whenever it reconfigures the link (reload, restart,
# carrier loss), leaving the BMC without any route out of the interface.
# Give each link its own runtime .network so those drop-ins apply, with the
# settings of the shared file. networkd uses the first matching .network in
# lexical order, so this relies on -bmc-<intf>.network sorting before
# -bmc-gbmcncsiusb.network. That holds for the USB path (enu*) and kernel
# (eth*) names these links get.
# Arguments:
#   $1: USB NCSI interface name
gbmc_ncsi_usb_network() {
  local intf="$1"
  local shared=-bmc-gbmcncsiusb.network
  local name=-bmc-"$intf".network
  if [[ $name > "$shared" ]]; then
    echo "NCSI USB $name sorts after $shared, its drop-ins won't apply" >&2
  fi
  local contents
  contents="$(sed -e '/^#/d' -e "s/^Property=.*/Name=$intf/" \
    /usr/lib/systemd/network/"$shared")" || return
  # Link events repeat on every carrier change, only reload when it changed
  gbmc_net_write_if_changed "$contents" /run/systemd/network/"$name" || true
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_ncsi_dynamic_hook)

gbmc_ncsi_dynamic_lib=1
