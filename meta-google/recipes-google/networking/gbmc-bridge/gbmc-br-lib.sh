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
  local name
  gbmc_br_ra_withdrawn_name name "$section" "$pfx"
  echo "gBMC Bridge withdrawing $section $pfx" >&2
  gbmc_net_networkd_write_dropin gbmcbr "$name" "$contents" || true
}

# Stops withdrawing a prefix we advertise again.
# Arguments:
#   $1: RA section, IPv6Prefix or IPv6RoutePrefix
#   $2: Prefix with its length
gbmc_br_ra_unwithdraw() {
  local name
  gbmc_br_ra_withdrawn_name name "$1" "$2"
  gbmc_net_networkd_write_dropin gbmcbr "$name" '' || true
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
  local file
  for file in "${GBMC_BR_WITHDRAWN_DIRS[0]}"/45-withdrawn-*.conf; do
    [[ -f $file ]] || continue
    local hdr until=0
    read -r hdr <"$file" || true
    [[ $hdr =~ ^'# until '([0-9]+)$ ]] && until="${BASH_REMATCH[1]}"
    if (( until <= now )); then
      gbmc_net_networkd_write_dropin gbmcbr "${file##*/}" '' || true
    elif [[ -z $withdrawn_next_out ]] || (( until < withdrawn_next_out )); then
      withdrawn_next_out="$until"
    fi
  done
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
  gbmc_net_reload_queue_start
  if [ -z "$ip" ]; then
    echo "Removing runtime gbmcbr IP: $name" >&2
    gbmc_net_networkd_write_dropin gbmcbr "50-ip-$name.conf" '' || true
    gbmc_br_ra_withdraw_stale "${old_ra[@]}"
    gbmc_net_reload_queue_end || true
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
Metric=$GBMC_ROUTE_METRIC_ONLINK
[Route]
Destination=$stateless_pfx/$stateless_size
Type=unreachable
Metric=$GBMC_ROUTE_METRIC_CATCHALL
EOF
  echo "Adding runtime gbmcbr IP: $name $pfx stateless($stateless_pfx/$stateless_size)" >&2

  gbmc_net_networkd_write_dropin gbmcbr "50-ip-$name.conf" \
    "$contents"$'\n'"$scontents" || true
  gbmc_br_ra_withdraw_stale "${old_ra[@]}"
  gbmc_net_reload_queue_end || true
}

gbmc_br_reload_ips() {
  echo "Reloading gbmcbr IPs" >&2
  gbmc_net_reload_queue_start
  # Remove legacy network configuration
  local d
  for d in /etc/systemd/network/{00,}-bmc-gbmcbr.network.d; do
    gbmc_net_mask_or_rm "$d"
  done

  local -a old_ra=()
  gbmc_br_ra_conf_entries old_ra '50-ip-static*.conf'
  local -A keep=([50-ip-static.conf]=1)

  local cur_ip
  cur_ip="$(gbmc_br_get_ip)"
  gbmc_br_set_runtime_ip static "$cur_ip" || true
  local ip
  local i=0
  for ip in $(shopt -s nullglob; cat /run/gbmc-br-ips/* 2>/dev/null); do
    keep["50-ip-static$i.conf"]=1
    gbmc_br_set_runtime_ip static$i "$ip"
    (( i += 1 ))
  done
  local file
  for file in "${GBMC_BR_WITHDRAWN_DIRS[0]}"/50-ip-static*.conf; do
    [[ -f $file && -z ${keep["${file##*/}"]-} ]] || continue
    gbmc_net_networkd_write_dropin gbmcbr "${file##*/}" '' || true
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

  local -a old_ra=()
  gbmc_br_ra_conf_entries old_ra '50-ip-alt*.conf'
  local -A keep=()

  gbmc_br_set_runtime_ip static "$ip" || rc=$?
  local alt_ip
  local i=0
  for alt_ip in "${alt_ips[@]}"; do
    keep["50-ip-alt$i.conf"]=1
    gbmc_br_set_runtime_ip alt$i "$alt_ip" || { rc=$?; break; }
    (( i += 1 ))
  done
  local file
  for file in "${GBMC_BR_WITHDRAWN_DIRS[0]}"/50-ip-alt*.conf; do
    [[ -f $file && -z ${keep["${file##*/}"]-} ]] || continue
    gbmc_net_networkd_write_dropin gbmcbr "${file##*/}" '' || true
  done
  gbmc_br_ra_withdraw_stale "${old_ra[@]}"

  if (( rc == 0 )); then
    gbmc_br_run_hooks GBMC_BR_LIB_SET_IP_HOOKS "$ip" || rc=$?
  fi

  gbmc_net_reload_queue_end || rc=1
  return $rc
}

# Every gateway on gbmcbr gets its own policy routing table holding a single
# default route through it, so traffic sourced from a /80 behind a gateway
# (see gbmc_br_from_rule_set) returns through that same gateway even when
# several BMCs on gbmcbr route for different prefixes.
#
# The mapping is only kept in memory and recovered from the kernel: the
# gbmc-ip-monitor init replay covers all tables, so every existing
# "default via <gw> dev gbmcbr table <rt>" route is tracked before anything
# allocates a table. Entries are never dropped, as the tables are shared by
# gbmc-br-from-ra.sh and gbmc-br-gw-src.sh and gateways are link-locals
# derived from stable MACs, so this only grows with the trays seen since boot.
# Gateway link-local address on gbmcbr -> policy routing table ID
declare -A gbmc_br_gw_tables=()
# Policy routing table ID -> gateway link-local address on gbmcbr
declare -A gbmc_br_table_gws=()

# gbmc-ip-monitor hook tracking the gateway <-> table mapping from routes.
# shellcheck disable=SC2154
gbmc_br_gw_table_hook() {
  if [[ $change == init ]]; then
    # Drop the From= rules of a previous instance, nothing would remove the
    # ones for prefixes no owner wants anymore. The owners add theirs back
    # on their next update, the ones already set are put back right away.
    local line
    while read -r line; do
      [[ $line =~ 'from '([^ ]+)' lookup '([0-9]+) ]] || continue
      (( BASH_REMATCH[2] >= GBMC_BR_GW_ROUTE_TABLE_BASE &&
         BASH_REMATCH[2] <= GBMC_BR_GW_ROUTE_TABLE_MAX )) || continue
      ip -6 rule del from "${BASH_REMATCH[1]}" lookup "${BASH_REMATCH[2]}" \
        pref "$GBMC_BR_RA_RULE_PREF" 2>/dev/null || true
    done < <(ip -6 rule show pref "$GBMC_BR_RA_RULE_PREF" 2>/dev/null)
    local pfx
    for pfx in "${!gbmc_br_from_rules[@]}"; do
      ip -6 rule add proto static from "$pfx" lookup "${gbmc_br_from_rules["$pfx"]}" \
        pref "$GBMC_BR_RA_RULE_PREF" 2>/dev/null || true
    done
    return 0
  fi
  [[ $change == route && $action == add ]] || return 0
  [[ $route =~ ^default' via '([^ ]+)' dev gbmcbr '.*'table '([0-9]+)( |$) ]] || return 0
  local gw="${BASH_REMATCH[1]}"
  local rt="${BASH_REMATCH[2]}"
  (( rt >= GBMC_BR_GW_ROUTE_TABLE_BASE && rt <= GBMC_BR_GW_ROUTE_TABLE_MAX )) || return 0
  [[ ${gbmc_br_gw_tables["$gw"]-} == "$rt" ]] && return 0
  local old_gw="${gbmc_br_table_gws["$rt"]-}"
  [[ -z $old_gw ]] || unset 'gbmc_br_gw_tables[$old_gw]'
  local old_rt="${gbmc_br_gw_tables["$gw"]-}"
  [[ -z $old_rt ]] || unset 'gbmc_br_table_gws[$old_rt]'
  gbmc_br_gw_tables["$gw"]="$rt"
  gbmc_br_table_gws["$rt"]="$gw"
  gbmc_ip_monitor_defer
}

# Returns the policy routing table for a gateway on gbmcbr, allocating the
# lowest free one if needed.
# Arguments:
#   $1: Output variable for the table ID
#   $2: Gateway IPv6 link-local address
# shellcheck disable=SC2034  # nameref
gbmc_br_gw_table() {
  local -n _gbmc_br_gw_table_out="$1"
  local _gw="$2"
  [[ -n $_gw ]] || return 1
  local _rt="${gbmc_br_gw_tables["$_gw"]-}"
  if [[ -z $_rt ]]; then
    _rt="$GBMC_BR_GW_ROUTE_TABLE_BASE"
    while [[ -n ${gbmc_br_table_gws["$_rt"]-} ]]; do
      (( ++_rt <= GBMC_BR_GW_ROUTE_TABLE_MAX )) || return 1
    done
    gbmc_br_gw_tables["$_gw"]="$_rt"
    gbmc_br_table_gws["$_rt"]="$_gw"
  fi
  _gbmc_br_gw_table_out="$_rt"
}

# Installs the default route of a gateway's policy routing table, and appends
# its networkd config so reconfigures keep it.
# Arguments:
#   $1: Variable to append the networkd [Route] config to
#   $2: Table ID
gbmc_br_gw_table_route() {
  local -n _gbmc_br_gw_table_route_conf="$1"
  local _rt="$2"
  local _gw="${gbmc_br_table_gws["$_rt"]-}"
  [[ -n $_gw ]] || return 1
  _gbmc_br_gw_table_route_conf+="[Route]"$'\n'"Gateway=$_gw"$'\n'"GatewayOnLink=true"$'\n'
  _gbmc_br_gw_table_route_conf+="Metric=$GBMC_ROUTE_METRIC_ONLINK"$'\n'"Table=$_rt"$'\n'
  ip -6 route replace default via "$_gw" onlink dev gbmcbr \
    metric "$GBMC_ROUTE_METRIC_ONLINK" table "$_rt" proto static 2>/dev/null || true
}

# Owner ("<name> <pfx>") -> wanted table for its From= rule
declare -A gbmc_br_from_rule_owners=()
# Prefix -> table of its installed From= rule
declare -A gbmc_br_from_rules=()

# Sets or clears the From= routing policy rule that sends traffic sourced from
# a /80 on gbmcbr into its gateway's policy routing table. Several scripts can
# want a rule for the same prefix, the lowest table wins. Rules are replaced by
# selector rather than by table, so stale ones from a previous instance or an
# old mapping are always removed.
# The owners' networkd drop-ins hold their own tables, so if owners with
# different tables overlap, a networkd reconfigure installs both rules. That
# needs from-ra to learn a route for our own /80, which RouterDenyList prevents.
# Arguments:
#   $1: Owner name (without spaces)
#   $2: Prefix with length
#   $3: Table ID, or empty to drop the owner's rule
gbmc_br_from_rule_set() {
  local owner="$1"
  local pfx="$2"
  local rt="${3-}"
  if [[ -n $rt ]]; then
    gbmc_br_from_rule_owners["$owner $pfx"]="$rt"
  else
    unset 'gbmc_br_from_rule_owners[$owner $pfx]'
  fi
  local want='' key
  for key in "${!gbmc_br_from_rule_owners[@]}"; do
    [[ ${key#* } == "$pfx" ]] || continue
    local krt="${gbmc_br_from_rule_owners["$key"]}"
    if [[ -z $want ]] || (( krt < want )); then
      want="$krt"
    fi
  done
  [[ ${gbmc_br_from_rules["$pfx"]-} == "$want" ]] && return 0
  while ip -6 rule del from "$pfx" pref "$GBMC_BR_RA_RULE_PREF" 2>/dev/null; do :; done
  if [[ -n $want ]]; then
    gbmc_br_from_rules["$pfx"]="$want"
    ip -6 rule add proto static from "$pfx" lookup "$want" pref "$GBMC_BR_RA_RULE_PREF" 2>/dev/null || true
  else
    unset 'gbmc_br_from_rules[$pfx]'
  fi
}

GBMC_IP_MONITOR_HOOKS+=(gbmc_br_gw_table_hook)

gbmc_br_lib_init=1
