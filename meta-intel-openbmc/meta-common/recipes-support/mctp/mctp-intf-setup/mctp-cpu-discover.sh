#!/bin/sh
# Resets the net's routes/EIDs, then sends an MCTP Discovery Notify to each
# I3C target found on the given bus, so the real bus owner assigns our
# MCTP-over-I3C endpoint interface an EID.
net="$1"
i3cbus="$2"

if [ -z "$net" ] || [ -z "$i3cbus" ]; then
    logger -t mctp-setup "expected <net> <i3cbus> arguments, got '$1' '$2'"
    exit 1
fi

# shellcheck source=/dev/null # installed on target alongside this script
. /usr/sbin/mctp-i3c-common.sh

# ---- functions ----

# Provisional ID of the single I3C target directly on $busidx, e.g.
# "0-20a0168000f" -> "20a0168000f". Only one target is expected on this bus.
list_i3c_pid() {
    find /sys/bus/i3c/devices -mindepth 1 -maxdepth 1 2>/dev/null |
        sed -n 's|^.*/'"$busidx"'-\([0-9a-f]\+\)$|\1|p' | head -n1
}

# Zero-pads $1 to a 6-byte I3C PID and prints it as a colon-separated
# lladdr, e.g. "20a0168000f" -> "02:0a:01:68:00:0f".
pid_to_lladdr() {
    pid="$1"
    pad=$((12 - ${#pid}))

    while [ "$pad" -gt 0 ]; do
        pid="0${pid}"
        pad=$((pad - 1))
    done
    echo "$pid" | sed 's/\(..\)\(..\)\(..\)\(..\)\(..\)\(..\)/\1:\2:\3:\4:\5:\6/'
}

wait_for_local_addr() {
    timeout=5 

    while [ "$timeout" -gt 0 ]; do
        eid=$(mctp addr show "$ifname" | awk '{print $2}')
        [ -n "$eid" ] && { echo "$eid"; return 0; }
        timeout=$((timeout - 1))
        sleep 1
    done
    return 1
}

# mctpd sends the actual Discovery Notify internally; its own retry only
# covers busy responses, not silence, and the property set always reports
# success regardless, so retrying is done by the caller checking the EID.
set_bus_owner() {
    req_lladdr="$1"
    req_pid="$2"

    # busctl wants "ay <count> 0xNN 0xNN ..."; convert e.g. "02:0a:01:68:00:0f".
    bytes=$(echo "$req_lladdr" | awk -F: '{for (i = 1; i <= NF; i++) printf "0x%s ", $i}')

    # $bytes must stay unquoted: busctl needs each byte as its own argument.
    # shellcheck disable=SC2086
    busctl set-property au.com.codeconstruct.MCTP1 \
        /au/com/codeconstruct/mctp1/interfaces/"$ifname" \
        au.com.codeconstruct.MCTP.Endpoint1 BusOwner ay 6 $bytes
    rc=$?

    if [ "$rc" -eq 0 ]; then
        logger -t mctp-setup "set BusOwner=$req_lladdr on $ifname for $req_pid, mctpd will send Discovery Notify"
    else
        logger -t mctp-setup "failed to set BusOwner=$req_lladdr on $ifname for $req_pid"
    fi
}

# ---- globals ----

# $i3cbus is the config physbus number (systemd instance number), which can
# differ from the kernel's i3c-N enumeration order; resolve the real bus
# index via its mmio address instead of assuming the numbers match.
mmio=$(i3c_physbus_to_mmio "$i3cbus") || {
    logger -t mctp-setup "no mmio mapping for i3c physbus $i3cbus"
    exit 1
}

busidx=$(i3c_busidx_for_mmio "$mmio") || {
    logger -t mctp-setup "no enumerated i3c-N bus found for physbus $i3cbus (mmio $mmio)"
    exit 1
}

ifname="mctpi3c${busidx}"

# ---- main ----

/usr/sbin/mctp-net-reset.sh "$net" \
    || logger -t mctp-setup "mctp-net-reset failed for net $net"

pid=""

# Always rescan (kicking the bus with a reassign) at least once, since a
# stale sysfs entry from before a host reset can otherwise make an already
# non-responsive target look present. Keep retrying until the target shows
# up or we time out (45 retries * 2s = 90s).
retry=0
while [ -z "$pid" ] && [ "$retry" -lt 45 ]; do
    i3c_reassign_addresses "$i3cbus" \
        || logger -t mctp-setup "i3c address reassignment failed for bus $i3cbus"
    sleep 2
    retry=$((retry + 1))
    pid=$(list_i3c_pid)
    [ -z "$pid" ] && logger -t mctp-setup "no I3C device found yet on bus $i3cbus, retrying"
done

if [ -z "$pid" ]; then
    logger -t mctp-setup "no I3C device found on bus $i3cbus, timed out"
    exit 1
fi

lladdr=$(pid_to_lladdr "$pid")

# mctpd's own Discovery Notify attempt doesn't retry on silence, so retry
# the whole set+check cycle here until an EID actually shows up.
attempt=1
max_attempts=10
while [ "$attempt" -le "$max_attempts" ]; do
    set_bus_owner "$lladdr" "$pid"

    eid=$(wait_for_local_addr)
    if [ -n "$eid" ]; then
        logger -t mctp-setup "$ifname assigned local eid $eid after Discovery Notify to $pid (attempt $attempt/$max_attempts)"
        exit 0
    fi

    logger -t mctp-setup "$ifname got no local eid after Discovery Notify to $pid (attempt $attempt/$max_attempts)"
    attempt=$((attempt + 1))
done

logger -t mctp-setup "no bus owner responded on $ifname (i3c bus $i3cbus)"
exit 1
