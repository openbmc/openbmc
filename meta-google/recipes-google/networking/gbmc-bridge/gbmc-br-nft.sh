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

[ -z "${gbmc_br_nft_lib-}" ] || return

source /usr/share/network/lib.sh || exit

gbmc_br_nft_init=
gbmc_br_nft_pfx=

gbmc_br_nft_update() {
  printf 'gBMC Bridge input firewall for %s\n' \
    "${gbmc_br_nft_pfx:-(deleted)}" >&2

  local contents=
  contents+='table inet filter {'$'\n'
  contents+='    chain gbmc_br_int_input {'$'\n'
  if [ -n "${gbmc_br_nft_pfx-}" ]; then
    contents+="        ip6 saddr $gbmc_br_nft_pfx"
    contents+=" ip6 daddr $gbmc_br_nft_pfx accept"$'\n'
  fi
  contents+='    }'$'\n'
  contents+='}'$'\n'

  local rfile=/run/nftables/40-gbmc-br-int.rules
  mkdir -p -m 755 "$(dirname "$rfile")"
  printf '%s' "$contents" >"$rfile"

  systemctl reset-failed nftables && systemctl --no-block reload-or-restart nftables || true
}

gbmc_br_nft_hook() {
  if [ "$change" = 'init' ]; then
    gbmc_br_nft_init=1
    gbmc_br_nft_update
  # Match only global IP addresses on the bridge that match the BMC prefix
  # (<mpfx>:fdxx:). So 2002:af4:3480:2248:fd02:6345:3069:9186 would become
  # a 2002:af4:3480:2248:fd00/76 rule.
  elif [ "$change" = 'addr' -a "$intf" = 'gbmcbr' -a "$scope" = 'global' ] &&
       [[ "$fam" == 'inet6' && "$flags" != *tentative* ]]; then
    local ip_bytes=()
    if ! ip_to_bytes ip_bytes "$ip"; then
      echo "gBMC Bridge NFT Invalid IP: $ip" >&2
      return 1
    fi
    if (( ip_bytes[8] != 0xfd )); then
      return 0
    fi
    local i
    for (( i=9; i<16; i++ )); do
      ip_bytes[$i]=0
    done
    pfx="$(ip_bytes_to_str ip_bytes)/76"
    if [ "$action" = "add" -a "$pfx" != "$gbmc_br_nft_pfx" ]; then
      gbmc_br_nft_pfx="$pfx"
      gbmc_br_nft_update
    fi
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_nft_hook)

gbmc_br_nft_lib=1
