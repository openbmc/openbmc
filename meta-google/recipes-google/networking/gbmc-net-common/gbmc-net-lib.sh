#!/bin/bash
# shellcheck disable=SC2119
# shellcheck disable=SC2120
# Copyright 2024 Google LLC
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

[ -n "${gbmc_net_lib_init-}" ] && return

# Prints the file to bind-mount over `target` so that it looks removed.
# Files under /etc/systemd shadow same-named files in /run/systemd and
# /usr/lib/systemd, and a /dev/null there is systemd's marker for a masked
# file, which hides the lower copies too. Use the copy systemd would fall back
# to if the file were removed, and /dev/null only when there is none.
# Arguments:
#   $1: Path to the target file
#   $2: Optional root prefix for the lookup (testing)
gbmc_net_mask_file_source() {
  local target="$1"
  local root="${2-}"
  if [[ $target == /etc/systemd/* ]]; then
    local rel="${target#/etc/systemd/}"
    local lower
    for lower in "$root"/run/systemd/"$rel" "$root"/usr/lib/systemd/"$rel"; do
      [ -e "$lower" ] || [ -L "$lower" ] || continue
      # A masked lower copy keeps the file masked
      if [ -f "$lower" ] && [ ! -L "$lower" ]; then
        echo "$lower"
        return 0
      fi
      break
    done
  fi
  echo /dev/null
}

# Safely mask or remove a filesystem target (file or directory).
# If GBMC_AVOID_RWFS is set, avoids writing or unlinking on persistent
# storage (RWFS) by bind-mounting over the target if not already mounted,
# an empty directory for directories and the file from
# gbmc_net_mask_file_source for files.
# If GBMC_AVOID_RWFS is unset, unmounts any active mount and removes the
# target directly via rm -rf.
# Arguments:
#   $1: Path to the target file or directory to mask or remove
gbmc_net_mask_or_rm() {
  local target="$1"
  [ -e "$target" ] || return 0
  if [ -n "${gbmc_net_avoid_rwfs-${GBMC_AVOID_RWFS-}}" ]; then
    if grep -q " $target " /proc/mounts 2>/dev/null; then
      return 0
    fi
    echo "Masking RWFS path $target from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    if [ -d "$target" ]; then
      local empty_dir="/run/gbmc-empty-d"
      mkdir -p "$empty_dir"
      mount --bind "$empty_dir" "$target" || return
    else
      mount --bind "$(gbmc_net_mask_file_source "$target")" "$target" || return
    fi
  else
    echo "Removing path $target from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    if grep -q " $target " /proc/mounts 2>/dev/null; then
      umount "$target" 2>/dev/null || true
    fi
    rm -rf "$target" || return
  fi
}

# Safely unmask any bind-mounts over a persistent target file and write content to it.
# Unmounts any active mount over the target and writes the contents, leaving
# the file alone if it already holds them. Without contents, it only makes
# sure the file exists.
# Arguments:
#   $1: Path to the target file
#   $2+: Contents to write (if provided, written separated by spaces with trailing newline)
gbmc_net_unmask_and_write() {
  local target="$1"
  shift

  while grep -q " $target " /proc/mounts 2>/dev/null; do
    echo "Unmasking RWFS path $target from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    umount "$target" 2>/dev/null || break
  done

  if [ -c "$target" ]; then
    rm -f "$target" || return
  fi

  if (( $# > 0 )); then
    gbmc_net_write_if_changed "$*" "$target"
    (( $? < 2 )) || return 1
  else
    mkdir -p "$(dirname "$target")" || return
    touch "$target" || return
  fi
}

GBMC_NET_RELOAD_REFCOUNT=0
GBMC_NET_NETWORKD_RELOAD_PENDING=0
declare -A GBMC_NET_NETWORKD_RELOAD_INTFS=()
GBMC_NET_NFTABLES_RELOAD_PENDING=0

# Begin an in-process reload queuing section.
# Increments the reload queue refcount. While refcount > 0, calls to
# gbmc_net_networkd_reload and gbmc_net_nftables_reload will queue their
# reload requests and interface arguments rather than executing immediately.
gbmc_net_reload_queue_start() {
  GBMC_NET_RELOAD_REFCOUNT=$(( GBMC_NET_RELOAD_REFCOUNT + 1 ))
}

# End an in-process reload queuing section.
# Decrements the reload queue refcount. When the refcount returns to 0, flushes
# any pending networkd or nftables reloads queued during the section.
# Returns:
#   0 on success, non-zero on refcount underflow or if a flushed reload fails.
gbmc_net_reload_queue_end() {
  if (( GBMC_NET_RELOAD_REFCOUNT > 0 )); then
    GBMC_NET_RELOAD_REFCOUNT=$(( GBMC_NET_RELOAD_REFCOUNT - 1 ))
  else
    echo "Reload queue refcount underflow from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    return 1
  fi
  if (( GBMC_NET_RELOAD_REFCOUNT == 0 )); then
    local rc=0
    if (( GBMC_NET_NETWORKD_RELOAD_PENDING == 1 )); then
      GBMC_NET_NETWORKD_RELOAD_PENDING=0
      local -a intfs=("${!GBMC_NET_NETWORKD_RELOAD_INTFS[@]}")
      GBMC_NET_NETWORKD_RELOAD_INTFS=()
      _gbmc_net_networkd_reload_exec "${intfs[@]}" || rc=$?
    fi
    if (( GBMC_NET_NFTABLES_RELOAD_PENDING == 1 )); then
      GBMC_NET_NFTABLES_RELOAD_PENDING=0
      _gbmc_net_nftables_reload_exec || rc=$?
    fi
    return "$rc"
  fi
  return 0
}

# Request a reload of systemd-networkd, optionally reconfiguring specific
# network interfaces.
# If a reload queue is active (refcount > 0), the reload and interface
# reconfigurations are queued. Otherwise, triggers an immediate cross-process
# coalesced reload of networkd.
# Arguments:
#   $@: Optional interface names to reconfigure (e.g. eth0, gbmcbr)
gbmc_net_networkd_reload() {
  local reconf=""
  (( $# > 0 )) && reconf=" + reconfiguring ($*)"
  if (( GBMC_NET_RELOAD_REFCOUNT > 0 )); then
    echo "Queuing networkd reload$reconf from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    GBMC_NET_NETWORKD_RELOAD_PENDING=1
    local intf
    for intf in "$@"; do
      [[ -n "$intf" ]] && GBMC_NET_NETWORKD_RELOAD_INTFS["$intf"]=1
    done
    return 0
  fi
  echo "Requesting networkd reload$reconf from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
  _gbmc_net_networkd_reload_exec "$@"
}

# Request a reload/restart of the nftables service.
# If a reload queue is active (refcount > 0), the reload is marked pending.
# Otherwise, triggers an immediate cross-process coalesced reload of nftables.
gbmc_net_nftables_reload() {
  if (( GBMC_NET_RELOAD_REFCOUNT > 0 )); then
    echo "Queuing nftables reload from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    GBMC_NET_NFTABLES_RELOAD_PENDING=1
    return 0
  fi
  echo "Requesting nftables reload from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
  _gbmc_net_nftables_reload_exec
}

# Serialize and coalesce reloads of "$svc" across processes.
#
# Coordination uses two locks and state files inside /run/gbmc-net-reload/$svc:
#
# Locks:
#   - $state_dir/mutex:
#       Short-lived mutex protecting metadata and directory operations.
#       Held only briefly during ticket issuing (req_gen increment and
#       populating pending/), batch claiming (atomic move to running/),
#       and completion recording (done_gen/status update). Never held
#       while the service action itself is running.
#   - $state_dir/lock:
#       Long-lived execution lock. Held by the single process actively
#       executing $action_fn (e.g. networkctl reload or nftables reload).
#       Concurrent callers queue on this lock.
#
# State files in $state_dir (/run/gbmc-net-reload/$svc):
#   - req_gen:
#       Monotonically increasing integer request generation. Each caller
#       increments req_gen under the mutex to receive a ticket (my_gen).
#   - pending/:
#       Directory containing touched marker files named after arguments
#       (e.g., interface names like eth0, gbmcbr) to deduplicate and batch
#       incoming arguments across callers before a batch is claimed.
#   - running/:
#       Directory containing the batch currently being processed by the
#       execution lock holder. Atomically promoted from pending/ via rename.
#       If an execution lock holder crashes, the next lock holder recovers
#       orphaned requests in running/ by moving them back into pending/.
#   - done_gen:
#       The highest req_gen completed by the most recent batch execution.
#       Waiting callers that acquire the execution lock compare done_gen
#       against their ticket (my_gen); if done_gen >= my_gen, their request
#       was already serviced by a preceding batch and they can return early.
#   - status:
#       The exit code of the last completed batch, read and returned by
#       callers whose requests were already covered by done_gen.
_gbmc_net_service_reload() {
  local svc="$1"
  local action_fn="$2"
  shift 2

  local state_dir="/run/gbmc-net-reload/$svc"
  local mutex="$state_dir/mutex"
  mkdir -p "$state_dir/pending" || return 1

  local mfd
  # Take a ticket and add our arguments to the pending batch
  local my_gen
  my_gen="$( (
    flock -x "$mfd" || exit
    local gen=0
    [[ -f "$state_dir/req_gen" ]] && gen="$(<"$state_dir/req_gen")"
    gen=$(( gen + 1 ))
    echo "$gen" >"$state_dir/req_gen" || exit
    local arg
    for arg in "$@"; do
      [[ -n "$arg" ]] && touch "$state_dir/pending/$arg"
    done
    echo "$gen"
  ) {mfd}>"$mutex" )"
  if ! [[ "$my_gen" =~ ^[0-9]+$ ]]; then
    echo "Failed to queue $svc reload from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    return 1
  fi

  # Only one process runs the action at a time
  local lfd
  exec {lfd}>"$state_dir/lock"
  if ! flock -x "$lfd"; then
    echo "Failed to acquire $svc reload lock from ${BASH_LINENO[0]} ${FUNCNAME[1]-main} ${BASH_SOURCE[1]-unknown}" >&2
    exec {lfd}>&-
    return 1
  fi

  # Claim every request queued so far, unless ours is already covered
  local claimed claim_rc
  claimed="$( (
    flock -x "$mfd" || exit 2
    local done_gen=0
    [[ -f "$state_dir/done_gen" ]] && done_gen="$(<"$state_dir/done_gen")"
    (( done_gen >= my_gen )) && exit 1
    # We hold the execution lock, so a leftover batch means its owner died.
    # Re-queue it instead of dropping those requests on the floor.
    local f
    for f in "$state_dir"/running/*; do
      [[ -e "$f" ]] && mv "$f" "$state_dir/pending/"
    done
    rm -rf "$state_dir/running"
    mv "$state_dir/pending" "$state_dir/running" || exit 2
    mkdir -p "$state_dir/pending" || exit 2
    cat "$state_dir/req_gen"
  ) {mfd}>"$mutex" )"
  claim_rc=$?

  local rc=0
  if (( claim_rc == 1 )); then
    # An earlier batch already covered us, so report the result it recorded
    rc=1
    [[ -f "$state_dir/status" ]] && rc="$(<"$state_dir/status")"
    [[ "$rc" =~ ^[0-9]+$ ]] || rc=1
  elif (( claim_rc != 0 )) || ! [[ "$claimed" =~ ^[0-9]+$ ]]; then
    echo "Failed to claim $svc reload batch" >&2
    rc=1
  else
    local -a batch=()
    local f
    for f in "$state_dir"/running/*; do
      [[ -e "$f" ]] && batch+=("${f##*/}")
    done

    "$action_fn" "$claimed" "${batch[@]}" || rc=$?

    (
      flock -x "$mfd" || exit
      echo "$rc" >"$state_dir/status"
      echo "$claimed" >"$state_dir/done_gen"
      rm -rf "$state_dir/running"
    ) {mfd}>"$mutex" ||
      echo "Warning: failed to record $svc reload completion" >&2
  fi

  exec {lfd}>&-
  return "$rc"
}

# networkd's varlink socket, created when it starts
GBMC_NET_NETWORKD_SOCK=/run/systemd/netif/io.systemd.Network

_gbmc_net_networkd_reload_action() {
  local gen="$1"
  shift
  local -a intfs=("$@")
  local retry
  for (( retry = 1; retry <= 10; retry++ )); do
    (( retry == 1 )) || sleep 1
    # networkd creates its varlink socket when it starts, before it loads the
    # configs. Without it there is nothing to reload yet. Checking for it
    # first skips a round-trip to systemd, which stalls for as long as a
    # daemon-reload runs (~10s at boot on some machines).
    [[ -e $GBMC_NET_NETWORKD_SOCK ]] || return 0
    local st
    st="$(systemctl is-active systemd-networkd 2>/dev/null || true)"
    if [ "$st" = 'inactive' ]; then
      # Nothing to reload, networkd picks up our configs when it starts
      return 0
    elif [ "$st" != 'active' ]; then
      # 'activating' is the common case here and is normally over in well under
      # a second, so just wait it out rather than deferring the reload.
      echo "systemd-networkd is $st, waiting before reload" >&2
      continue
    fi
    local reconf=""
    (( ${#intfs[@]} > 0 )) && reconf=" + reconfiguring (${intfs[*]})"
    echo "Reloading networkd (gen $gen)$reconf, try $retry" >&2
    networkctl reload || continue
    local intf failed=
    for intf in "${intfs[@]}"; do
      networkctl reconfigure "$intf" || { failed=1; break; }
    done
    [ -n "$failed" ] || return 0
  done
  local reconf=""
  (( ${#intfs[@]} > 0 )) && reconf=" + reconfiguring (${intfs[*]})"
  echo "Failed to reload networkd$reconf after 10 attempts" >&2
  return 1
}

# Set by long running event loops (gbmc-ip-monitor) to run the reloads in
# the background instead of waiting for them, as `networkctl reload` and the
# systemd round-trips can take seconds at boot. _gbmc_net_service_reload()
# still serializes and coalesces them: the child takes its ticket after the
# caller wrote the files it reloads for, so the batch covering it reads them.
GBMC_NET_RELOAD_ASYNC=

# Runs _gbmc_net_service_reload() with the arguments, in the background if
# GBMC_NET_RELOAD_ASYNC is set.
_gbmc_net_service_reload_maybe_async() {
  if [[ -n $GBMC_NET_RELOAD_ASYNC ]]; then
    _gbmc_net_service_reload "$@" &
    return 0
  fi
  _gbmc_net_service_reload "$@"
}

_gbmc_net_networkd_reload_exec() {
  _gbmc_net_service_reload_maybe_async "networkd" _gbmc_net_networkd_reload_action "$@"
}

_gbmc_net_nftables_reload_action() {
  local gen="$1"
  local st
  st="$(systemctl is-active nftables 2>/dev/null || true)"
  # Nothing to reload, nftables loads our rules when it starts
  if [ "${st:-inactive}" = 'inactive' ] || [ "$st" = 'activating' ]; then
    return 0
  fi
  echo "Reloading nftables (gen $gen)" >&2
  systemctl reset-failed nftables 2>/dev/null || true
  # Non-blocking: systemd coalesces and orders the jobs for us, and we don't
  # want a slow nftables restart stalling the netlink event loop.
  systemctl --no-block reload-or-restart nftables
}

_gbmc_net_nftables_reload_exec() {
  _gbmc_net_service_reload_maybe_async "nftables" _gbmc_net_nftables_reload_action "$@"
}

GBMC_INTF_ROUTE_TABLE_BASE=1000
# Per-gateway policy routing tables on gbmcbr (3000..3999), disjoint from the
# per-interface tables (GBMC_INTF_ROUTE_TABLE_BASE + ifindex, 1001..1999).
# shellcheck disable=SC2034
GBMC_BR_GW_ROUTE_TABLE_BASE=3000
# shellcheck disable=SC2034
GBMC_BR_GW_ROUTE_TABLE_MAX=3999
# Routing policy rule priority for From= rules on gbmcbr RA-discovered prefixes.
# Placed after the per-interface From= rules (GBMC_INTF_ROUTE_TABLE_BASE +
# ifindex, 1001..1999) so a BMC that owns the upstream NIC/uplink for a prefix
# always routes its own addresses out that uplink, while peer BMCs on gbmcbr
# route traffic sourced from that prefix into the gateway's gbmcbr routing table.
# shellcheck disable=SC2034
GBMC_BR_RA_RULE_PREF=$(( GBMC_INTF_ROUTE_TABLE_BASE + 1000 ))
# Routing policy rule priority that ends the lookup for traffic sourced from
# an uplink's allocation, after its per-interface table. Each allocation only
# has the single path out of its uplink, so when the uplink is down (and its
# table is empty) the traffic is dropped rather than falling through to the
# main table and out another uplink or back over gbmcbr.
GBMC_INTF_RULE_END_PREF=$(( GBMC_BR_RA_RULE_PREF - 1 ))

# Route metrics. The default route metrics form a preference order for
# reaching the ToR (lower wins); the other metrics mostly keep a route we add
# with `ip route` identical to the copy networkd installs from our drop-ins,
# so the two merge instead of duplicating, and distinct from the kernel's own
# prefix routes (256) and networkd's RA routes (RouteMetric=1056 on gbmcbr).
#
# Default routes in the main table, by uplink:
#   700  Whichever uplink route below is sourced from the primary IP, copied
#        to this metric so the primary address always wins source selection.
#   800  Front panel RJ45, from a live RA (gbmc-nic-ra). Closest to the ToR.
#   900  NCSI / in-band NIC, from a live RA (gbmc-ncsi-ra).
#   +1000 (1800, 1900)  The same uplinks from the last RA we saw, cached in
#        /run and /var so we have a gateway before the first live RA arrives.
#   1536 Highest metric still eligible for the 700 copy, i.e. live uplinks
#        only, never a cached route. Routes through gbmcbr never get copied
#        either, whatever their metric.
#
# Default routes on a tray, through the head BMC over gbmcbr. The head
# advertises each of its uplinks with its metric (see gbmc_br_adv_pfx) and the
# tray bumps it to the next thousand, keeping the uplink digit in the tens
# (800 -> 1080, 1800 -> 2080, see gbmc_br_tray_metric):
#   1070, 1080, 1090  The head's live uplinks. Below the tray's own cached
#        routes (1800+) so a head with a live ToR beats a stale cache, above
#        its own live uplinks (800, 900) so a tray with its own ToR uses it.
#   2080, 2090  The head's cached uplinks.
#   2000 A head too old to encode the metric in its advertisement.
#   2500 l2br RA routes once the head is advertising, well past every route
#        through the head (up to 2099) so a tray never prefers l2br to it.
#
# Prefix routes on gbmcbr:
#   512  Specific on-link prefixes: the /80s downstream BMCs autoconfigure in,
#        RA prefixes we hold an address in, and the per-interface routing
#        table (GBMC_INTF_ROUTE_TABLE_BASE + ifindex) default routes.
#   1024 Catch-all routes for a whole allocation: unreachable for the unused
#        rest of a /64 or /76, and the compute node's half (/65) of its /64.
#
# Source selection pseudo-metrics in gbmc-br-gw-src, not installed anywhere:
#   4095 An NCSI NIC address on its own NIC before its policy rules exist.
#   4096 No route / unknown metric.
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_PRIMARY=700
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_FRONT_PANEL=800
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_NCSI=900
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_FALLBACK_OFFSET=1000
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_PRIMARY_MAX=1536
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_TRAY_LEGACY=2000
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_L2BR=$(( 2 * GBMC_ROUTE_METRIC_FALLBACK_OFFSET + 500 ))
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_ONLINK=512
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_CATCHALL=1024
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_NIC_FALLBACK=4095
# shellcheck disable=SC2034
GBMC_ROUTE_METRIC_UNSET=4096

# Address label (RFC 6724 rule 6) for the /80 prefixes we autoconfigure an
# address in from gbmcbr RAs. Global destinations carry the default label 1,
# as does our primary /128, so a source with a different label only gets
# picked for destinations inside its own /80 or when a route names it.
# shellcheck disable=SC2034
GBMC_SLAAC_ADDRLABEL=99

# Router discovery timers, in seconds:
#   GBMC_RA_TIMEOUT      How long each rdisc6 solicitation waits for RAs.
#   GBMC_RA_MIN_RETRY    Solicitation interval until we have a router.
#   GBMC_RA_RS_INTERVAL  Solicitation interval once we do.
#   GBMC_RA_EXPIRE       Without an RA for this long a router is dropped and
#                        the next one we heard from takes over.
#   GBMC_BR_RA_LIFETIME  Lifetime of the router, prefixes and routes we
#                        advertise on gbmcbr, 4 solicitation intervals so the
#                        BMCs behind us refresh well before it runs out.
#   GBMC_BR_RA_GRACE     How long an RA route may be missing before we act on
#                        it. A networkd reconfigure flushes the RA routes and
#                        strips the source off our uplink routes; it solicits a
#                        new RA right away and we put the source back, so
#                        waiting this long avoids tearing down addresses and
#                        advertisements that are about to come back.
# shellcheck disable=SC2034
GBMC_RA_TIMEOUT=10
# shellcheck disable=SC2034
GBMC_RA_MIN_RETRY=10
# shellcheck disable=SC2034
GBMC_RA_RS_INTERVAL=30
# shellcheck disable=SC2034
GBMC_RA_EXPIRE=180
# shellcheck disable=SC2034
GBMC_BR_RA_LIFETIME=120
# shellcheck disable=SC2034
GBMC_BR_RA_GRACE=5

# Calculate and print the deterministic routing table number for a network
# interface based on its kernel ifindex.
# The table number is calculated as GBMC_INTF_ROUTE_TABLE_BASE (1000) + ifindex.
# Arguments:
#   $1: Interface name (e.g. eth0, gbmcbr)
# Outputs:
#   The computed integer route table ID to stdout
# Returns:
#   0 on success, non-zero if the interface sysfs ifindex cannot be read.
gbmc_net_route_table_for_intf() {
  local intf="$1"
  local idx=
  read -r idx <"/sys/class/net/$intf/ifindex" || return
  echo $((GBMC_INTF_ROUTE_TABLE_BASE + idx))
}

# Replaces an IPv6 policy rule, removing an existing copy first so it never
# gets duplicated.
# Arguments:
#   $@: ip rule selector and action (e.g. from <pfx> lookup <table> pref <n>)
# Returns:
#   The status of adding the rule.
gbmc_net_rule_replace() {
  ip -6 rule del "$@" 2>/dev/null
  ip -6 rule add proto static "$@"
}

# Appends the networkd config for the policy rules of an uplink allocation:
# traffic to it uses the main table, and traffic from it only ever leaves
# through the uplink's own table, see GBMC_INTF_RULE_END_PREF.
# Arguments:
#   $1: Variable to append the networkd config to
#   $2: Allocation prefix with length
#   $3: Per-interface routing table of the uplink
gbmc_net_intf_rules_conf() {
  local -n _gbmc_net_intf_rules_conf="$1"
  local _pfx="$2"
  local _rt="$3"
  _gbmc_net_intf_rules_conf+="[RoutingPolicyRule]"$'\n'"To=$_pfx"$'\n'"Table=main"$'\n'
  _gbmc_net_intf_rules_conf+="Priority=$GBMC_INTF_ROUTE_TABLE_BASE"$'\n'
  _gbmc_net_intf_rules_conf+="[RoutingPolicyRule]"$'\n'"From=$_pfx"$'\n'"Table=$_rt"$'\n'
  _gbmc_net_intf_rules_conf+="Priority=$_rt"$'\n'
  _gbmc_net_intf_rules_conf+="[RoutingPolicyRule]"$'\n'"From=$_pfx"$'\n'"Type=unreachable"$'\n'
  _gbmc_net_intf_rules_conf+="Priority=$GBMC_INTF_RULE_END_PREF"$'\n'
}

# Installs (or removes) the policy rules of gbmc_net_intf_rules_conf() right
# away, without waiting for networkd.
# Arguments:
#   $1: add or del
#   $2: Allocation prefix with length
#   $3: Per-interface routing table of the uplink
gbmc_net_intf_rules_set() {
  local act="$1"
  local pfx="$2"
  local rt="$3"
  local rules=(
    "to $pfx lookup main pref $GBMC_INTF_ROUTE_TABLE_BASE"
    "from $pfx lookup $rt pref $rt"
    "from $pfx unreachable pref $GBMC_INTF_RULE_END_PREF"
  )
  local rule rc=0
  for rule in "${rules[@]}"; do
    # shellcheck disable=SC2086
    if [[ $act == add ]]; then
      gbmc_net_rule_replace $rule || rc=1
    else
      ip -6 rule del $rule 2>/dev/null || true
    fi
  done
  return $rc
}

# Enables proxy NDP on an interface and proxies the addresses on it right
# away, without waiting for networkd to apply the config.
# Arguments:
#   $1: Interface
#   $2+: Addresses to proxy
# Returns:
#   0 on success, non-zero if any of them failed.
gbmc_net_proxy_ndp_add() {
  local intf="$1"
  shift
  local st=0
  sysctl net.ipv6.conf."$intf".proxy_ndp=1 >/dev/null || st=$?
  local ip
  for ip in "$@"; do
    ip -6 neigh replace proxy "$ip" dev "$intf" || st=$?
  done
  return "$st"
}

# Triggers the reload for any networkd or nftables files among the paths.
# Arguments:
#   $@: Paths to the files that changed
_gbmc_net_reload_for_paths() {
  local need_networkd=0
  local need_nftables=0
  local file
  for file in "$@"; do
    case "$file" in
      */systemd/network/*) need_networkd=1 ;;
      */nftables/*) need_nftables=1 ;;
    esac
  done
  (( need_networkd == 0 )) || gbmc_net_networkd_reload || true
  (( need_nftables == 0 )) || gbmc_net_nftables_reload || true
}

# Writes contents to files unless they already hold them, or removes the
# files when the contents are empty, and reloads networkd or nftables when a
# file under systemd/network or nftables changed. Use it for every networkd
# (.network, .netdev, .link and drop-ins) and nftables file we generate, and
# for every persistent file. Unchanged files are left alone, as networkd
# reconfigures the interfaces of every .network file that changed on its next
# reload. The contents go to a temporary file in the same directory first,
# which networkd and nft ignore by its name, and are renamed over the file, so
# readers, and the file left after a power loss, only ever hold the old or the
# new contents. A failure on one file doesn't stop the others from being
# written, nor the reload for those that changed.
# Arguments:
#   $1: Contents, empty to remove the files
#   $2+: Paths to the files
# Returns:
#   0 if any file was written or removed, 1 if none changed, 2 on failure.
gbmc_net_write_if_changed() {
  local contents="$1"
  shift
  [[ -n $contents ]] || { gbmc_net_rm_if_exists "$@"; return; }
  [[ $contents == *$'\n' ]] || contents+=$'\n'
  local changed=0
  local failed=0
  local file
  for file in "$@"; do
    if [[ -f $file ]]; then
      local cur=
      IFS= read -r -d '' cur <"$file" || true
      [[ $cur == "$contents" ]] && continue
    fi
    local tmp="$file.tmp.$BASHPID"
    if { [[ -d ${file%/*} ]] || mkdir -p "${file%/*}"; } &&
        printf '%s' "$contents" >"$tmp" &&
        mv -f "$tmp" "$file"; then
      changed=1
    else
      rm -f "$tmp"
      failed=1
    fi
  done
  (( changed == 0 )) || _gbmc_net_reload_for_paths "$@"
  (( failed == 0 )) || return 2
  (( changed == 1 )) || return 1
}

# Removes files, for the networkd and nftables files we generate, and reloads
# networkd or nftables when a file under systemd/network or nftables was
# removed.
# Arguments:
#   $@: Paths to the files
# Returns:
#   0 if any file was removed, 1 if none existed, 2 on failure.
gbmc_net_rm_if_exists() {
  local changed=0
  local failed=0
  local file
  for file in "$@"; do
    [[ -e $file || -L $file ]] || continue
    if rm -f "$file"; then
      changed=1
    else
      failed=1
    fi
  done
  (( changed == 0 )) || _gbmc_net_reload_for_paths "$@"
  (( failed == 0 )) || return 2
  (( changed == 1 )) || return 1
}

# Writes or removes a networkd drop-in in both {00,}-bmc-<intf>.network.d
# directories, reloading networkd when it changed.
# Arguments:
#   $1: Interface
#   $2: Drop-in file name
#   $3: Contents, empty to remove the drop-in
# Returns:
#   0 if a drop-in was written or removed, 1 if unchanged, 2 on failure.
gbmc_net_networkd_write_dropin() {
  gbmc_net_write_if_changed "$3" \
    /run/systemd/network/{00,}-bmc-"$1".network.d/"$2"
}

# Sources picked by gbmc-br-gw-src for the main table default routes that
# gbmc-ra configures through networkd, one file per interface and metric
# (<dir>/<intf>/<metric>) holding "<gateway> <source>".
#
# networkd only keeps a route it configured across a reconfigure of its
# interface if the kernel route still matches the [Route] exactly, including
# the protocol (static) and the source. Otherwise it removes the route and
# adds it back, here without a source, letting the kernel pick any address
# (e.g. a NIC address out of the front panel) until gbmc-br-gw-src puts the
# source back. So the source goes into the [Route] as PreferredSource= and the
# route is installed with "proto static".
GBMC_NET_ROUTE_SRC_DIR=/run/gbmc-net-route-src

# Sets PreferredSource= in the main table [Route] sections for a gateway and
# metric of networkd .network contents.
# Arguments:
#   $1: Output variable for the new contents
#   $2: Contents
#   $3: Gateway
#   $4: Metric
#   $5: Source address, empty to remove the source
# Returns failure if no [Route] section matched.
# The locals are prefixed so they don't shadow the output variable.
# shellcheck disable=SC2034  # namerefs
gbmc_net_conf_route_src() {
  local -n conf_src_out="$1"
  local _crs_contents="$2"
  local _crs_gw="$3"
  local _crs_metric="$4"
  local _crs_src="$5"
  conf_src_out="$_crs_contents"
  [[ -n $_crs_contents ]] || return 1
  local -a _crs_secs=()
  local _crs_sec=
  local _crs_line
  while IFS= read -r _crs_line; do
    if [[ $_crs_line == '['*']' && -n $_crs_sec ]]; then
      _crs_secs+=("$_crs_sec")
      _crs_sec=
    fi
    _crs_sec+="$_crs_line"$'\n'
  done <<<"${_crs_contents%$'\n'}"
  [[ -n $_crs_sec ]] && _crs_secs+=("$_crs_sec")
  local _crs_out=
  local _crs_matched=0
  for _crs_sec in "${_crs_secs[@]}"; do
    if [[ $_crs_sec == $'[Route]\n'* && $_crs_sec != *$'\nTable='* ]] &&
       [[ $_crs_sec == *$'\nGateway='"$_crs_gw"$'\n'* ]] &&
       [[ $_crs_sec == *$'\nMetric='"$_crs_metric"$'\n'* ]]; then
      local _crs_new=
      while IFS= read -r _crs_line; do
        [[ $_crs_line == PreferredSource=* ]] || _crs_new+="$_crs_line"$'\n'
      done <<<"${_crs_sec%$'\n'}"
      [[ -z $_crs_src ]] || _crs_new+="PreferredSource=$_crs_src"$'\n'
      _crs_sec="$_crs_new"
      _crs_matched=1
    fi
    _crs_out+="$_crs_sec"
  done
  # Keep the contents exactly as they were without a trailing newline
  [[ $_crs_contents == *$'\n' ]] || _crs_out="${_crs_out%$'\n'}"
  conf_src_out="$_crs_out"
  (( _crs_matched == 1 ))
}

# Looks up the saved source for a default route.
# Arguments:
#   $1: Output variable for the source, empty without one
#   $2: Interface
#   $3: Gateway
#   $4: Metric
# shellcheck disable=SC2034  # namerefs
gbmc_net_route_src_get() {
  local -n route_src_out="$1"
  route_src_out=
  local _rsg_file="$GBMC_NET_ROUTE_SRC_DIR/$2/$4"
  [[ -f $_rsg_file ]] || return 0
  local _rsg_gw _rsg_src
  read -r _rsg_gw _rsg_src <"$_rsg_file" || return 0
  [[ $_rsg_gw == "$3" ]] && route_src_out="$_rsg_src"
  return 0
}

# Applies every saved source for an interface to networkd contents.
# Arguments:
#   $1: Output variable for the new contents
#   $2: Interface
#   $3: Contents
# shellcheck disable=SC2034  # namerefs
gbmc_net_route_src_apply() {
  local -n route_apply_out="$1"
  local _rsa_contents="$3"
  local _rsa_file
  for _rsa_file in "$GBMC_NET_ROUTE_SRC_DIR/$2"/*; do
    [[ -f $_rsa_file ]] || continue
    local _rsa_gw _rsa_src
    read -r _rsa_gw _rsa_src <"$_rsa_file" || continue
    gbmc_net_conf_route_src _rsa_contents "$_rsa_contents" "$_rsa_gw" \
      "${_rsa_file##*/}" "$_rsa_src" || true
  done
  route_apply_out="$_rsa_contents"
}

# Takes the lock serializing the gateway drop-in updates for an interface.
# Callers hold a reload queue reference while holding it, so the networkd
# reload only runs once the lock is released, and give up on it after a while
# rather than stalling gbmc-ip-monitor.
# Arguments:
#   $1: Output variable for the lock file descriptor
#   $2: Interface
gbmc_net_route_src_lock() {
  local -n route_lock_fd="$1"
  mkdir -p "$GBMC_NET_ROUTE_SRC_DIR" || return
  exec {route_lock_fd}>"$GBMC_NET_ROUTE_SRC_DIR/$2.lock" || return
  flock -x "$route_lock_fd"
}

# Writes one of the networkd gateway drop-ins (10-gateway*.conf) of an
# interface with the saved sources applied, see GBMC_NET_ROUTE_SRC_DIR.
# Arguments:
#   $1: Interface
#   $2: Drop-in file name
#   $3: Contents, empty to remove the drop-in
gbmc_net_gateway_conf_write() {
  local intf="$1"
  local name="$2"
  local contents="$3"
  local lfd
  gbmc_net_reload_queue_start
  gbmc_net_route_src_lock lfd "$intf" || true
  [[ -z $contents ]] || gbmc_net_route_src_apply contents "$intf" "$contents"
  gbmc_net_networkd_write_dropin "$intf" "$name" "$contents" || true
  [[ -z ${lfd-} ]] || exec {lfd}>&-
  gbmc_net_reload_queue_end || true
}

# Saves the source picked for a default route and sets it in the networkd
# gateway drop-ins of its interface. The saved source is only touched when a
# drop-in configures the route or it was saved for the same gateway, so a
# stale route at the same metric doesn't clobber the current one.
# Arguments:
#   $1: Interface
#   $2: Gateway
#   $3: Metric
#   $4: Source address, empty to remove the source
# Returns failure if the route isn't configured through networkd.
gbmc_net_route_src_set() {
  local intf="$1"
  local gw="$2"
  local metric="$3"
  local src="$4"
  local lfd
  gbmc_net_reload_queue_start
  gbmc_net_route_src_lock lfd "$intf" || true
  local found=0
  local file
  for file in /run/systemd/network/{00,}-bmc-"$intf".network.d/10-gateway*.conf; do
    [[ -f $file ]] || continue
    local contents=
    IFS= read -r -d '' contents <"$file" || true
    local new
    gbmc_net_conf_route_src new "$contents" "$gw" "$metric" "$src" || continue
    found=1
    gbmc_net_write_if_changed "$new" "$file" || true
  done
  local state="$GBMC_NET_ROUTE_SRC_DIR/$intf/$metric"
  local saved_gw=
  [[ ! -f $state ]] || read -r saved_gw _ <"$state" || true
  if (( found == 1 )) || [[ $saved_gw == "$gw" ]]; then
    if [[ -n $src ]]; then
      { [[ -d ${state%/*} ]] || mkdir -p "${state%/*}"; } &&
        printf '%s %s\n' "$gw" "$src" >"$state"
    else
      rm -f "$state"
    fi
  fi
  [[ -z ${lfd-} ]] || exec {lfd}>&-
  gbmc_net_reload_queue_end || true
  (( found == 1 ))
}

gbmc_net_lib_init=1
