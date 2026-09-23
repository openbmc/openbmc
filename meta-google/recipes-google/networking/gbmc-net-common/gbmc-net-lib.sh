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

# Safely mask or remove a filesystem target (file or directory).
# If GBMC_AVOID_RWFS is set, avoids writing or unlinking on persistent
# storage (RWFS) by bind-mounting /dev/null (for files) or an empty
# directory (for directories) over the target if not already mounted.
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
    echo "Masking RWFS path $target from $(caller 0 2>/dev/null || echo unknown)" >&2
    if [ -d "$target" ]; then
      local empty_dir="/run/gbmc-empty-d"
      mkdir -p "$empty_dir"
      mount --bind "$empty_dir" "$target" || return
    else
      mount --bind /dev/null "$target" || return
    fi
  else
    echo "Removing path $target from $(caller 0 2>/dev/null || echo unknown)" >&2
    if grep -q " $target " /proc/mounts 2>/dev/null; then
      umount "$target" 2>/dev/null || true
    fi
    rm -rf "$target" || return
  fi
}

# Safely unmask any bind-mounts over a persistent target file and write content to it.
# Unmounts any active mount over the target, ensures the parent directory exists,
# touches the file up front to confirm it can be written to RWFS, and writes the contents.
# Arguments:
#   $1: Path to the target file
#   $2+: Contents to write (if provided, written separated by spaces with trailing newline)
gbmc_net_unmask_and_write() {
  local target="$1"
  shift

  while grep -q " $target " /proc/mounts 2>/dev/null; do
    echo "Unmasking RWFS path $target from $(caller 0 2>/dev/null || echo unknown)" >&2
    umount "$target" 2>/dev/null || break
  done

  if [ -c "$target" ]; then
    rm -f "$target" || return
  fi

  mkdir -p "$(dirname "$target")" || return

  touch "$target" || return

  if (( $# > 0 )); then
    printf '%s\n' "$*" >"$target" || return
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
    echo "Reload queue refcount underflow from $(caller 0 2>/dev/null || echo unknown)" >&2
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
    echo "Queuing networkd reload$reconf from $(caller 0 2>/dev/null || echo unknown)" >&2
    GBMC_NET_NETWORKD_RELOAD_PENDING=1
    local intf
    for intf in "$@"; do
      [[ -n "$intf" ]] && GBMC_NET_NETWORKD_RELOAD_INTFS["$intf"]=1
    done
    return 0
  fi
  echo "Requesting networkd reload$reconf from $(caller 0 2>/dev/null || echo unknown)" >&2
  _gbmc_net_networkd_reload_exec "$@"
}

# Request a reload/restart of the nftables service.
# If a reload queue is active (refcount > 0), the reload is marked pending.
# Otherwise, triggers an immediate cross-process coalesced reload of nftables.
gbmc_net_nftables_reload() {
  if (( GBMC_NET_RELOAD_REFCOUNT > 0 )); then
    echo "Queuing nftables reload from $(caller 0 2>/dev/null || echo unknown)" >&2
    GBMC_NET_NFTABLES_RELOAD_PENDING=1
    return 0
  fi
  echo "Requesting nftables reload from $(caller 0 2>/dev/null || echo unknown)" >&2
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
    echo "Failed to queue $svc reload from $(caller 0 2>/dev/null || echo unknown)" >&2
    return 1
  fi

  # Only one process runs the action at a time
  local lfd
  exec {lfd}>"$state_dir/lock"
  if ! flock -x "$lfd"; then
    echo "Failed to acquire $svc reload lock from $(caller 0 2>/dev/null || echo unknown)" >&2
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

_gbmc_net_networkd_reload_action() {
  local gen="$1"
  shift
  local -a intfs=("$@")
  local retry
  for (( retry = 1; retry <= 10; retry++ )); do
    (( retry == 1 )) || sleep 1
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

_gbmc_net_networkd_reload_exec() {
  _gbmc_net_service_reload "networkd" _gbmc_net_networkd_reload_action "$@"
}

_gbmc_net_nftables_reload_action() {
  local gen="$1"
  # Nothing to reload, nftables loads our rules when it starts
  if [ "$(systemctl is-active nftables 2>/dev/null || true)" = 'inactive' ]; then
    return 0
  fi
  echo "Reloading nftables (gen $gen)" >&2
  systemctl reset-failed nftables 2>/dev/null || true
  # Non-blocking: systemd coalesces and orders the jobs for us, and we don't
  # want a slow nftables restart stalling the netlink event loop.
  systemctl --no-block reload-or-restart nftables
}

_gbmc_net_nftables_reload_exec() {
  _gbmc_net_service_reload "nftables" _gbmc_net_nftables_reload_action "$@"
}

GBMC_INTF_ROUTE_TABLE_BASE=1000

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
  idx="$(cat /sys/class/net/"$intf"/ifindex)" || return
  echo $((GBMC_INTF_ROUTE_TABLE_BASE + idx))
}

gbmc_net_lib_init=1
