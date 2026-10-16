#!/bin/sh
# Runs mctp-cpu-discover once, then re-runs a net reset + rediscovery every
# time the host power state transitions to Running (same signal used by the
# i3c-hub-detector's power state change monitor, rather than eSPI reset).
i3cbus="$1"

if [ -z "$i3cbus" ]; then
    logger -t mctp-setup "missing i3c bus number argument"
    exit 1
fi

# ---- globals ----

# Only the CPU-facing i3c link (physbus 1) is set up; its MCTP network id.
net=2

HOST_STATE_SERVICE=xyz.openbmc_project.State.Host0
HOST_STATE_PATH=/xyz/openbmc_project/state/host0
HOST_STATE_IFACE=xyz.openbmc_project.State.Host
# Passing a SERVICE argument alongside --match makes busctl monitor match
# on either condition instead of both, so sender is folded into the match.
MATCH="type='signal',sender='$HOST_STATE_SERVICE',path='$HOST_STATE_PATH',interface='org.freedesktop.DBus.Properties',member='PropertiesChanged',arg0='$HOST_STATE_IFACE'"

# ---- main ----

/usr/sbin/mctp-cpu-discover.sh "$net" "$i3cbus" \
    || logger -t mctp-setup "mctp-cpu-discover failed for net $net i3c bus $i3cbus"

logger -t mctp-setup "watching for host power state change on net $net i3c bus $i3cbus"

while :; do
    # Blocks for exactly one matching PropertiesChanged signal, then exits;
    # loop back around to wait for the next one.
    signal=$(busctl monitor --match="$MATCH" --limit-messages=1 2>/dev/null)

    echo "$signal" | grep -q 'STRING "CurrentHostState"' || continue
    echo "$signal" | grep -A2 'STRING "CurrentHostState"' | grep -q 'STRING ".*\.Running"' || continue

    logger -t mctp-setup "CurrentHostState discovered as Running for net $net i3c bus $i3cbus"
    sleep 10
    # mctp-cpu-discover.sh runs mctp-net-reset.sh itself, so it isn't
    # called separately here.
    logger -t mctp-setup "redoing discovery for net $net i3c bus $i3cbus"
    /usr/sbin/mctp-cpu-discover.sh "$net" "$i3cbus" \
        || logger -t mctp-setup "mctp-cpu-discover failed for net $net i3c bus $i3cbus"
done
