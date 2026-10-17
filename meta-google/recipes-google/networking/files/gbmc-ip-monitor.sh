#!/bin/bash
# shellcheck disable=SC2034
# shellcheck disable=SC2317
# shellcheck disable=SC2329
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

# shellcheck source=meta-google/recipes-google/networking/gbmc-net-common/gbmc-net-lib.sh
if [ -f /usr/share/gbmc-net-lib.sh ]; then
  source /usr/share/gbmc-net-lib.sh || exit
elif [ -n "${SYSROOT-}" ] && [ -f "$SYSROOT/usr/share/gbmc-net-lib.sh" ]; then
  source "$SYSROOT/usr/share/gbmc-net-lib.sh" || exit
elif [ -f "$(dirname "${BASH_SOURCE[0]}")/../gbmc-net-common/gbmc-net-lib.sh" ]; then
  source "$(dirname "${BASH_SOURCE[0]}")/../gbmc-net-common/gbmc-net-lib.sh" || exit
else
  echo "Failed to find gbmc-net-lib.sh" >&2
  exit 1
fi

# A list of functions which get executed for each netlink event received.
# These are configured by the files included below.
GBMC_IP_MONITOR_HOOKS=()

# Load configurations from a known location in the filesystem to populate
# hooks that are executed after each event.
shopt -s nullglob
for conf in /usr/share/gbmc-ip-monitor/*.sh; do
  # shellcheck source=/dev/null
  source "$conf"
done

gbmc_ip_monitor_run_hooks() {
  gbmc_net_reload_queue_start
  local hook
  for hook in "${GBMC_IP_MONITOR_HOOKS[@]}"; do
    "$hook" || continue
  done
  if [ "$change" != 'defer' ] && [ -n "$gbmc_ip_monitor_init_done" ] && (( GBMC_NET_NETWORKD_RELOAD_PENDING == 1 || GBMC_NET_NFTABLES_RELOAD_PENDING == 1 )) && [ -z "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" ]; then
    gbmc_ip_monitor_defer
  fi
  gbmc_net_reload_queue_end
}

gbmc_ip_monitor_generate_init() {
  ip link | sed 's,^[^ ],[LINK]\0,'
  local intf=
  local line
  while read -r line; do
    [[ "$line" =~ ^([0-9]+:[[:space:]][^:]+) ]] && intf="${BASH_REMATCH[1]}"
    [[ "$line" =~ ^[[:space:]]*inet ]] && echo "[ADDR]$intf $line"
  done < <(ip addr)
  ip -4 route | sed 's,^,[ROUTE],'
  ip -6 route show table all | sed '/ table local /d; s,^,[ROUTE],'
  echo '[INIT]'
}

# Timers, deferrals and idle requests are kept as deadlines in the event loop
# itself rather than as tokens written back into the event stream, so they
# fire on time even with a backlog of netlink events in front of them. All
# deadlines are in centiseconds of uptime, see gbmc_ip_monitor_uptime_cs().

# Uptime deadlines of the pending deferral, see gbmc_ip_monitor_defer()
GBMC_IP_MONITOR_DEFER_OUTSTANDING=
GBMC_IP_MONITOR_DEFER_DUE=
GBMC_IP_MONITOR_DEFER_LATEST=
# Quiet period before a deferral fires, and the most it waits for the
# pending events to drain once that is over
GBMC_IP_MONITOR_DEFER_MIN=1
GBMC_IP_MONITOR_DEFER_MAX=3

# Requests a `change=defer` event, after which the reloads requested until
# then are flushed together. It fires no earlier than GBMC_IP_MONITOR_DEFER_MIN
# seconds from now, and then as soon as no more events are pending, but no
# later than GBMC_IP_MONITOR_DEFER_MAX seconds from now. A deferral requested
# while one is pending is merged into it, and any reloads requested once it
# fired wait for the next one.
gbmc_ip_monitor_defer() {
  [ -z "$GBMC_IP_MONITOR_DEFER_OUTSTANDING" ] || return 0
  local now
  gbmc_ip_monitor_uptime_cs now || return
  # This reload queue reference is held across events: it is released by the
  # main loop when the deferral fires, which is what keeps the reloads
  # requested in between batched together.
  gbmc_net_reload_queue_start
  GBMC_IP_MONITOR_DEFER_DUE=$(( now + GBMC_IP_MONITOR_DEFER_MIN * 100 ))
  GBMC_IP_MONITOR_DEFER_LATEST=$(( now + GBMC_IP_MONITOR_DEFER_MAX * 100 ))
  GBMC_IP_MONITOR_DEFER_OUTSTANDING=1
}

# Stores the centiseconds since boot in the variable named by $1. This is
# monotonic, unlike the wall clock which can jump when time is synced.
gbmc_ip_monitor_uptime_cs() {
  local -n out_uptime_cs="$1"
  local up _
  read -r up _ </proc/uptime || return
  [[ $up =~ ^([0-9]+)\.([0-9]{2}) ]] || return
  out_uptime_cs=$(( BASH_REMATCH[1] * 100 + 10#${BASH_REMATCH[2]} ))
}

# Stores the seconds since boot in the variable named by $1.
gbmc_ip_monitor_uptime() {
  local -n out_uptime="$1"
  local cs
  gbmc_ip_monitor_uptime_cs cs || return
  out_uptime=$(( cs / 100 ))
}

# Uptime deadline of the pending timer for each name
declare -A GBMC_IP_MONITOR_TIMERS=()
# Requests a `change=timer` event with `timer=$1` in no more than $2 seconds.
# An earlier pending timer with the same name is kept instead, so hooks must
# re-arm the timer when it fires if they still need a later one. Unlike
# deferrals, timers don't hold back reloads while they are pending.
gbmc_ip_monitor_timer() {
  local name="$1"
  local secs="$2"
  (( secs < 0 )) && secs=0
  local now
  gbmc_ip_monitor_uptime_cs now || return
  local due=$(( now + secs * 100 ))
  local pending="${GBMC_IP_MONITOR_TIMERS["$name"]-}"
  if [[ -n $pending ]] && (( pending <= due )); then
    return 0
  fi
  GBMC_IP_MONITOR_TIMERS["$name"]=$due
}
gbmc_ip_monitor_timer_cancel() {
  unset 'GBMC_IP_MONITOR_TIMERS[$1]' 'GBMC_IP_MONITOR_IDLE[$1]'
}

# Uptime deadline of the pending idle request for each name
declare -A GBMC_IP_MONITOR_IDLE=()
# Requests a `change=timer` event with `timer=$1` once no more events are
# pending, but in no more than $2 seconds. This batches work that only
# depends on the state the events leave behind, like recomputing something
# derived from all of the routes, into one run per burst of events. Names
# must not be shared with gbmc_ip_monitor_timer().
gbmc_ip_monitor_idle() {
  local name="$1"
  local secs="$2"
  (( secs < 0 )) && secs=0
  local now
  gbmc_ip_monitor_uptime_cs now || return
  local due=$(( now + secs * 100 ))
  local pending="${GBMC_IP_MONITOR_IDLE["$name"]-}"
  if [[ -n $pending ]] && (( pending <= due )); then
    return 0
  fi
  GBMC_IP_MONITOR_IDLE["$name"]=$due
}

# Grace periods for keys (e.g. prefixes) whose routes disappeared, as
# networkd briefly flushes them on every reconfigure. A grace map is an
# associative array of key -> uptime deadline after which the key is acted on,
# if it is still gone.
# Starts the grace period of a key, keeping an already pending deadline.
# Arguments:
#   $1: Name of the grace map
#   $2: Key
#   $3: Grace period in seconds
#   $4: Timer to arm for the end of the grace period
gbmc_ip_monitor_grace_start() {
  local -n _gbmc_ip_monitor_grace_start_map="$1"
  [[ -z ${_gbmc_ip_monitor_grace_start_map["$2"]-} ]] || return 0
  local now
  gbmc_ip_monitor_uptime now || return
  _gbmc_ip_monitor_grace_start_map["$2"]=$(( now + $3 ))
  gbmc_ip_monitor_timer "$4" "$3"
}
# Ends the grace period of a key that came back.
# Arguments:
#   $1: Name of the grace map
#   $2: Key
gbmc_ip_monitor_grace_clear() {
  local -n _gbmc_ip_monitor_grace_clear_map="$1"
  unset '_gbmc_ip_monitor_grace_clear_map[$2]'
}
# Calls `$3 <key>` for every key whose grace period ended, after removing it
# from the map. The callback has to check whether it is still gone. The timer
# is re-armed for the next pending deadline, if any.
# Arguments:
#   $1: Name of the grace map
#   $2: Timer used for the grace map
#   $3: Callback
gbmc_ip_monitor_grace_expire() {
  local -n _gbmc_ip_monitor_grace_expire_map="$1"
  local timer="$2"
  local cb="$3"
  local now
  gbmc_ip_monitor_uptime now || return
  local next='' key
  for key in "${!_gbmc_ip_monitor_grace_expire_map[@]}"; do
    local deadline="${_gbmc_ip_monitor_grace_expire_map["$key"]}"
    if (( deadline <= now )); then
      unset '_gbmc_ip_monitor_grace_expire_map[$key]'
      "$cb" "$key" || true
    elif [[ -z $next ]] || (( deadline < next )); then
      next=$deadline
    fi
  done
  if [[ -n $next ]]; then
    gbmc_ip_monitor_timer "$timer" $(( next - now ))
  else
    gbmc_ip_monitor_timer_cancel "$timer"
  fi
}

gbmc_ip_monitor_parse_line() {
  local line="$1"
  if [[ "$line" == '[INIT]'* ]]; then
    change=init
    echo "Initialized" >&2
  elif [[ "$line" == '[ADDR]'* ]]; then
    change=addr
    action=add
    pfx_re='^\[ADDR\](Deleted )?[0-9]+:[[:space:]]*'
    intf_re='([^ ]+)[[:space:]]+'
    fam_re='([^ ]+)[[:space:]]+'
    addr_re='([^/]+)/[0-9]+[[:space:]]+'
    metric_re='(metric[[:space:]]+[^ ]+[[:space:]]+)?'
    brd_re='(brd[[:space:]]+[^ ]+[[:space:]]+)?'
    scope_re='scope[[:space:]]+([^ ]+)[[:space:]]*(.*)'
    combined_re="${pfx_re}${intf_re}${fam_re}${addr_re}${metric_re}${brd_re}${scope_re}"
    if ! [[ "$line" =~ ${combined_re} ]]; then
      echo "Failed to parse addr: $line" >&2
      return 1
    fi
    if [ -n "${BASH_REMATCH[1]}" ]; then
      action=del
    fi
    intf="${BASH_REMATCH[2]%%@*}"
    fam="${BASH_REMATCH[3]}"
    ip="${BASH_REMATCH[4]}"
    scope="${BASH_REMATCH[7]}"
    flags="${BASH_REMATCH[8]}"
  elif [[ "$line" == '[ROUTE]'* ]]; then
    line="${line#[ROUTE]}"
    change=route
    action=add
    if ! [[ "$line" =~ ^\[ROUTE\](Deleted )?(.*)$ ]]; then
      echo "Failed to parse route: $line" >&2
      return 1
    fi
    if [ -n "${BASH_REMATCH[1]}" ]; then
      action=del
    fi
    route="${BASH_REMATCH[2]}"
  elif [[ "$line" =~ ^\[RULE\](Deleted )?(.*)$ ]]; then
    change=rule
    action=add
    if [ -n "${BASH_REMATCH[1]}" ]; then
      action=del
    fi
    rule="${BASH_REMATCH[2]}"
  elif [[ "$line" == '[LINK]'* ]]; then
    change='link'
    action=add
    pfx_re='^\[LINK\](Deleted )?[0-9]+:[[:space:]]*'
    intf_re='([^:]+):[[:space:]]+'
    carrier_re='state[[:space:]]+([^ ]+)([[:space:]]+|$)'
    combined_re="${pfx_re}${intf_re}.*${carrier_re}"
    if ! [[ "$line" =~ ${combined_re} ]]; then
      echo "Failed to parse link: $line" >&2
      return 1
    fi
    if [ -n "${BASH_REMATCH[1]}" ]; then
      action=del
    fi
    intf="${BASH_REMATCH[2]%%@*}"
    carrier="${BASH_REMATCH[3]}"
    read -ra data || return
    mac="${data[1]}"
  else
    return 2
  fi
}

# Returns success if input is waiting on stdin.
gbmc_ip_monitor_input_pending() {
  read -r -t 0
}

# Fires the timers, idle requests and deferral that are due.
gbmc_ip_monitor_run_due() {
  local now
  gbmc_ip_monitor_uptime_cs now || return
  local busy=
  gbmc_ip_monitor_input_pending && busy=1
  local name
  local -a names=()
  for name in "${!GBMC_IP_MONITOR_TIMERS[@]}"; do
    (( GBMC_IP_MONITOR_TIMERS["$name"] <= now )) && names+=("$name")
  done
  for name in "${names[@]}"; do
    # An earlier hook could have cancelled or re-armed it
    local due="${GBMC_IP_MONITOR_TIMERS["$name"]-}"
    if [[ -z $due ]] || (( due > now )); then
      continue
    fi
    unset 'GBMC_IP_MONITOR_TIMERS[$name]'
    change=timer
    timer="$name"
    gbmc_ip_monitor_run_hooks || true
  done
  names=()
  for name in "${!GBMC_IP_MONITOR_IDLE[@]}"; do
    [[ -z $busy ]] || (( GBMC_IP_MONITOR_IDLE["$name"] <= now )) || continue
    names+=("$name")
  done
  for name in "${names[@]}"; do
    [[ -n ${GBMC_IP_MONITOR_IDLE["$name"]-} ]] || continue
    unset 'GBMC_IP_MONITOR_IDLE[$name]'
    change=timer
    timer="$name"
    gbmc_ip_monitor_run_hooks || true
  done
  # Last, so it also flushes the reloads requested by the above
  if [[ -n $GBMC_IP_MONITOR_DEFER_OUTSTANDING ]] &&
     (( GBMC_IP_MONITOR_DEFER_DUE <= now )) &&
     { [[ -z $busy ]] || (( GBMC_IP_MONITOR_DEFER_LATEST <= now )); }; then
    GBMC_IP_MONITOR_DEFER_OUTSTANDING=
    change=defer
    gbmc_ip_monitor_run_hooks || true
    gbmc_net_reload_queue_end || true
  fi
}

# Stores in the variable named by $1 how long to wait for the next event, in
# seconds as accepted by `read -t`, before something else is due. Empty if
# nothing is pending.
gbmc_ip_monitor_timeout() {
  local -n out_timeout="$1"
  out_timeout=
  local now
  gbmc_ip_monitor_uptime_cs now || return
  local next='' due
  for due in "${GBMC_IP_MONITOR_TIMERS[@]}" "${GBMC_IP_MONITOR_IDLE[@]}"; do
    [[ -z $next ]] || (( due < next )) && next=$due
  done
  if [[ -n $GBMC_IP_MONITOR_DEFER_OUTSTANDING ]]; then
    # Once the quiet period is over it is waiting for the events to drain,
    # which the read below notices
    due=$GBMC_IP_MONITOR_DEFER_DUE
    (( due > now )) || due=$GBMC_IP_MONITOR_DEFER_LATEST
    [[ -z $next ]] || (( due < next )) && next=$due
  fi
  [[ -n $next ]] || return 0
  local wait=$(( next - now ))
  (( wait > 0 )) || wait=1
  printf -v out_timeout '%d.%02d' $(( wait / 100 )) $(( wait % 100 ))
}

# Runs the hooks for one line of the event stream.
gbmc_ip_monitor_handle_line() {
  gbmc_ip_monitor_parse_line "$1" || return 0
  gbmc_ip_monitor_run_hooks || true
  if [ "$change" = 'init' ]; then
    gbmc_ip_monitor_init_done=1
    gbmc_net_reload_queue_end || true
    systemd-notify --ready || true
  fi
}

# Processes the event stream on stdin until it ends.
gbmc_ip_monitor_loop() {
  local line partial='' t rc
  while true; do
    gbmc_ip_monitor_run_due
    gbmc_ip_monitor_timeout t
    if [[ -z $t ]]; then
      IFS= read -r line
    else
      IFS= read -r -t "$t" line
    fi
    rc=$?
    if (( rc == 0 )); then
      gbmc_ip_monitor_handle_line "$partial$line"
      partial=
    elif (( rc > 128 )); then
      # Timed out, keep anything read of an incomplete line
      partial+="$line"
    else
      [[ -z $partial$line ]] || gbmc_ip_monitor_handle_line "$partial$line"
      return 0
    fi
  done
}

return 0 2>/dev/null

# shellcheck disable=SC2329
cleanup() {
  local st="$?"
  trap - HUP INT QUIT ABRT TERM EXIT
  jobs -l -p | xargs -r kill || true
  exit "$st"
}
trap cleanup HUP INT QUIT ABRT TERM EXIT

# Don't stall the event loop on networkctl, see GBMC_NET_RELOAD_ASYNC
GBMC_NET_RELOAD_ASYNC=1

gbmc_net_reload_queue_start
gbmc_ip_monitor_init_done=

gbmc_ip_monitor_loop < <(gbmc_ip_monitor_generate_init; exec ip monitor link addr route rule label)
