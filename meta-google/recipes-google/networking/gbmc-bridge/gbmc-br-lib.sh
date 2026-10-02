#!/bin/bash
# Copyright 2022 Google LLC
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

[ -n "${gbmc_br_lib_init-}" ] && return

# shellcheck source=meta-google/recipes-google/networking/network-sh/lib.sh
source /usr/share/network/lib.sh || exit
# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
source /usr/share/gbmc-net-lib.sh || exit

# A list of functions which get executed for each configured IP.
# These are configured by the files included below.
# Shellcheck does not understand how this gets referenced
# shellcheck disable=SC2034
GBMC_BR_LIB_SET_IP_HOOKS=()

# A dict of netboot status to retain start of each state
declare -A NETBOOT_STATUS_START=()

update_netboot_status() {
  local state="$1"
  local message="$2"
  local code="$3"
  local retries="${4-}"
  local time

  # Remembered so the exit trap can tell whether a failure was already reported
  # shellcheck disable=SC2034
  NETBOOT_STATUS_CODE="$code"

  if [[ "$code" == "START" ]]; then
    NETBOOT_STATUS_START["$state"]=$SECONDS
    time=0
  elif [[ -v NETBOOT_STATUS_START["$state"] ]]; then
    time=$((SECONDS - NETBOOT_STATUS_START["$state"]))
  else
    # easy indicator to flag error, no state should ever report before START is defined.
    time=-1
  fi
  local json_output="{\"Message\":\"$message\",\"State\":\"$state\",\"Code\":\"$code\",\"Time\":\"$time\""

  if [[ -n "$retries" ]]; then
    json_output+=",\"retries\":\"$retries\""
  fi

  json_output+="}"

  systemd-cat -t "gbmc-netboot" <<<"$json_output"
  update-dhcp-status 'ONGOING' "$json_output"
}

gbmc_br_source_dir() {
  local dir="$1"

  local file
  while read -r -d $'\0' file; do
    # SC doesn't like dynamic source loading
    # shellcheck disable=SC1090
    source "$file" || return
  done < <(shopt -s nullglob; for f in "$dir"/*.sh; do printf '%s\0' "$f"; done)
}

# Load configurations from a known location in the filesystem to populate
# hooks that are executed after each event.
gbmc_br_source_dir /usr/share/gbmc-br-lib || exit

gbmc_br_run_hooks() {
  local -n hookvar="$1"
  shift
  gbmc_net_reload_queue_start
  local hook
  local rc=0
  for hook in "${hookvar[@]}"; do
    "$hook" "$@" || rc=$?
  done
  gbmc_net_reload_queue_end || rc=1
  return $rc
}

# Lifetime of everything we advertise to the BMCs behind gbmcbr, the same as
# our RouterLifetimeSec. networkd sends unsolicited RAs at least that often
# and the BMCs behind us solicit one every 30s.
GBMC_BR_RA_LIFETIME=120
GBMC_BR_WITHDRAWN_DIRS=(/run/systemd/network/{00,}-bmc-gbmcbr.network.d)

# shellcheck disable=SC2034  # nameref
gbmc_br_ra_withdrawn_name() {
  local -n withdrawn_name_out="$1"
  withdrawn_name_out="45-withdrawn-$2-${3//\//_}.conf"
}

# Keeps advertising a prefix we no longer have with a zero lifetime, so the
# BMCs behind gbmcbr drop it right away instead of when it expires. RAs that
# just leave it out don't remove it. The withdrawal lasts as long as the
# advertisement could have, see gbmc_br_ra_withdrawn_expire().
# Arguments:
#   $1: RA section, IPv6Prefix or IPv6RoutePrefix
#   $2: Prefix with its length
gbmc_br_ra_withdraw() {
  local section="$1"
  local pfx="$2"
  local up _
  read -r up _ </proc/uptime || return
  local contents="# until $(( ${up%.*} + GBMC_BR_RA_LIFETIME ))"$'\n'"[$section]"$'\n'
  if [[ $section == IPv6Prefix ]]; then
    contents+="Prefix=$pfx"$'\n'"PreferredLifetimeSec=0"$'\n'"ValidLifetimeSec=0"$'\n'
  else
    contents+="Route=$pfx"$'\n'"LifetimeSec=0"$'\n'
  fi
  local name dir
  gbmc_br_ra_withdrawn_name name "$section" "$pfx"
  echo "gBMC Bridge withdrawing $section $pfx" >&2
  for dir in "${GBMC_BR_WITHDRAWN_DIRS[@]}"; do
    mkdir -p "$dir"
    printf '%s' "$contents" >"$dir/$name"
  done
}

# Stops withdrawing a prefix we advertise again.
# Arguments:
#   $1: RA section, IPv6Prefix or IPv6RoutePrefix
#   $2: Prefix with its length
gbmc_br_ra_unwithdraw() {
  local name dir
  gbmc_br_ra_withdrawn_name name "$1" "$2"
  for dir in "${GBMC_BR_WITHDRAWN_DIRS[@]}"; do
    rm -f "$dir/$name"
  done
}

# Lists the prefixes advertised by our gbmcbr drop-ins as "<section> <prefix>".
# Arguments:
#   $1: Output array
#   $@: Drop-in file names, globs are expanded
gbmc_br_ra_conf_entries() {
  local -n conf_entries_out="$1"
  shift
  local glob file
  for glob in "$@"; do
    for file in "${GBMC_BR_WITHDRAWN_DIRS[0]}"/$glob; do
      [[ -f $file ]] || continue
      local section="" line
      while read -r line; do
        if [[ $line =~ ^\[(.*)\]$ ]]; then
          section="${BASH_REMATCH[1]}"
        elif [[ $section == IPv6RoutePrefix && $line == Route=* ]]; then
          conf_entries_out+=("$section ${line#Route=}")
        elif [[ $section == IPv6Prefix && $line == Prefix=* ]]; then
          conf_entries_out+=("$section ${line#Prefix=}")
        fi
      done <"$file"
    done
  done
}

# Withdraws the given prefixes we no longer advertise from any of our IP
# drop-ins, and stops withdrawing the ones we do.
# Arguments:
#   $@: "<section> <prefix>" entries we used to advertise
gbmc_br_ra_withdraw_stale() {
  local -a cur=()
  gbmc_br_ra_conf_entries cur '50-ip-*.conf'
  local -A have=()
  local entry
  for entry in "${cur[@]}"; do
    have["$entry"]=1
    # shellcheck disable=SC2086
    gbmc_br_ra_unwithdraw $entry
  done
  for entry in "$@"; do
    [[ -z ${have["$entry"]-} ]] || continue
    have["$entry"]=1
    # shellcheck disable=SC2086
    gbmc_br_ra_withdraw $entry
  done
}

# Removes withdrawals that outlived the advertisements they cancel.
# Arguments:
#   $1: Output variable for the uptime of the next one to expire, if any
gbmc_br_ra_withdrawn_expire() {
  local -n withdrawn_next_out="$1"
  withdrawn_next_out=
  local up _
  read -r up _ </proc/uptime || return
  local now="${up%.*}"
  local changed=
  local file
  for file in "${GBMC_BR_WITHDRAWN_DIRS[0]}"/45-withdrawn-*.conf; do
    [[ -f $file ]] || continue
    local hdr until=0
    read -r hdr <"$file" || true
    [[ $hdr =~ ^'# until '([0-9]+)$ ]] && until="${BASH_REMATCH[1]}"
    if (( until <= now )); then
      local dir
      for dir in "${GBMC_BR_WITHDRAWN_DIRS[@]}"; do
        rm -f "$dir/${file##*/}"
      done
      changed=1
    elif [[ -z $withdrawn_next_out ]] || (( until < withdrawn_next_out )); then
      withdrawn_next_out="$until"
    fi
  done
  if [[ -n $changed ]]; then
    # shellcheck disable=SC2119
    gbmc_net_networkd_reload
  fi
  return 0
}

gbmc_br_get_ip() {
  local ip
  ip="$(cat /run/gbmc-br-ip 2>/dev/null)"
  if [ -n "$ip" ]; then
    echo "$ip"
    return 0
  fi
  cat /var/google/gbmc-br-ip 2>/dev/null || true
}

gbmc_br_set_runtime_ip() {
  local name="$1"
  local ip="$2"

  local pfx_bytes=()
  local rc=0
  if ! ip_to_bytes pfx_bytes "$ip"; then
    echo "Invalid IPv6 for $name: $ip" >&2
    ip=
    rc=1
  fi
  local -a old_ra=()
  gbmc_br_ra_conf_entries old_ra "50-ip-$name.conf"
  if [ -z "$ip" ]; then
    echo "Removing runtime gbmcbr IP: $name" >&2
    rm -f /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-"$name".conf
    gbmc_br_ra_withdraw_stale "${old_ra[@]}"
    # shellcheck disable=SC2119
    gbmc_net_networkd_reload
    return $rc
  fi

  local pfx
  pfx="$(ip_bytes_to_str pfx_bytes)"
  # We either have an inband IP address that has a /76 allocation for
  # our BMCs, or we have an OOB address that is a /64. We can only tell the
  # difference by the 9th byte being 00 vs fd.
  local stateless_size=
  if (( pfx_bytes[8] == 0 )); then
    stateless_size=64
  else
    stateless_size=76
  fi
  (( pfx_bytes[8] |= 0xfd ))
  (( pfx_bytes[9] &= 0xf0 ))
  local stateless_pfx
  stateless_pfx="$(ip_bytes_to_str pfx_bytes)"
  local contents
  read -r -d '' contents <<EOF
[Address]
Address=$pfx/128
DuplicateAddressDetection=none
[IPv6RoutePrefix]
Route=$pfx/80
LifetimeSec=$GBMC_BR_RA_LIFETIME
EOF
  local scontents
  if [[ -n $stateless_size ]]; then
    # Downstream BMCs autoconfigure addresses in the advertised /80, so it
    # needs an on-link route over gbmcbr. Otherwise the unreachable route
    # covering the rest of the allocation swallows traffic to them, and they
    # are only reachable while they also hold a statically assigned address.
    read -r -d '' scontents <<EOF
[IPv6Prefix]
Prefix=$stateless_pfx/80
PreferredLifetimeSec=$GBMC_BR_RA_LIFETIME
ValidLifetimeSec=$GBMC_BR_RA_LIFETIME
[Route]
Destination=$stateless_pfx/80
Metric=512
[Route]
Destination=$stateless_pfx/$stateless_size
Type=unreachable
Metric=1024
EOF
  fi
  echo "Adding runtime gbmcbr IP: $name $pfx stateless($stateless_pfx/$stateless_size)" >&2

  local file
  for file in /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-$name.conf; do
    mkdir -p "$(dirname "$file")"
    printf '%s\n%s' "$contents" "$scontents" >"$file"
  done
  gbmc_br_ra_withdraw_stale "${old_ra[@]}"

  # A plain reload is enough, networkd reconfigures gbmcbr when its drop-ins
  # change. Forcing a reconfigure would flush every address and route
  # networkd owns on gbmcbr and drop any added outside of networkd.
  # shellcheck disable=SC2119
  gbmc_net_networkd_reload
}

gbmc_br_reload_ips() {
  echo "Reloading gbmcbr IPs" >&2
  gbmc_net_reload_queue_start
  # Remove legacy network configuration
  local d
  for d in /etc/systemd/network/{00,}-bmc-gbmcbr.network.d; do
    gbmc_net_mask_or_rm "$d"
  done

  # Remove existing loaded configurations
  local -a old_ra=()
  gbmc_br_ra_conf_entries old_ra '50-ip-static*.conf'
  (shopt -s nullglob; rm -rf /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-static*.conf)

  local cur_ip
  cur_ip="$(gbmc_br_get_ip)"
  gbmc_br_set_runtime_ip static "$cur_ip" || true
  local ip
  local i=0
  for ip in $(shopt -s nullglob; cat /run/gbmc-br-ips/* 2>/dev/null); do
    gbmc_br_set_runtime_ip static$i "$ip"
    (( i += 1 ))
  done
  gbmc_br_ra_withdraw_stale "${old_ra[@]}"
  gbmc_net_reload_queue_end || true
}

gbmc_br_set_ip() {
  local ip="${1-}"
  shift
  local alt_ips=("$@")

  if [ -n "$ip" ]; then
    local pfx_bytes=()
    if ! ip_to_bytes pfx_bytes "$ip"; then
      echo "Not setting invalid IPv6: $ip" >&2
      return 1
    fi
    echo "Setting gbmcbr IP: $ip alt(${alt_ips[*]})" >&2
    if [ -z "${GBMC_AVOID_RWFS-}" ]; then
      gbmc_net_unmask_and_write /var/google/gbmc-br-ip "$ip" || return
    fi
    echo "$ip" >/run/gbmc-br-ip || return
  else
    local cur_ip
    cur_ip="$(gbmc_br_get_ip)"
    [ -z "$cur_ip" ] && return
    echo "Clearing gbmcbr IP (was $cur_ip)" >&2
    gbmc_net_mask_or_rm /var/google/gbmc-br-ip
    rm -f /run/gbmc-br-ip
  fi

  gbmc_net_reload_queue_start
  local rc=0

  # Remove existing loaded configurations
  local -a old_ra=()
  gbmc_br_ra_conf_entries old_ra '50-ip-alt*.conf'
  (shopt -s nullglob; rm -rf /run/systemd/network/{00,}-bmc-gbmcbr.network.d/50-ip-alt*.conf)

  gbmc_br_set_runtime_ip static "$ip" || rc=$?
  local alt_ip
  local i=0
  for alt_ip in "${alt_ips[@]}"; do
    gbmc_br_set_runtime_ip alt$i "$alt_ip" || { rc=$?; break; }
    (( i += 1 ))
  done
  gbmc_br_ra_withdraw_stale "${old_ra[@]}"

  if (( rc == 0 )); then
    gbmc_br_run_hooks GBMC_BR_LIB_SET_IP_HOOKS "$ip" || rc=$?
  fi

  gbmc_net_reload_queue_end || rc=1
  return $rc
}

gbmc_br_lib_init=1
