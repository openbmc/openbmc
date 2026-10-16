#!/bin/sh
# Cleans up stale routes/EIDs on a network after an endpoint reset, so
# mctpd can rediscover devices with a clean slate.
net="$1"

if [ -z "$net" ]; then
    logger -t mctp-setup "missing network id argument"
    exit 1
fi

# ---- functions ----

# Prints every eid registered under $net (mctpd's busctl tree never lists
# the broadcast eid 255 as an endpoint object, so no need to exclude it).
list_net_eids() {
    busctl tree "$MCTP_BUS" | grep "networks/$net/endpoints/" | sed 's#.*/##'
}

# Prints every interface attached to $net, excluding loopback.
list_net_ifaces() {
    mctp link | grep "net $net " | awk '{print $2}' | grep -v '^lo$'
}

set_iface_state() {
    ifname="$1"
    state="$2"

    mctp link set "$ifname" "$state"
    rc=$?

    if [ "$rc" -eq 0 ]; then
        logger -t mctp-setup "$ifname $state"
    else
        logger -t mctp-setup "failed to bring $ifname $state"
    fi
}

remove_endpoint() {
    eid="$1"

    busctl call "$MCTP_BUS" \
        /au/com/codeconstruct/mctp1/networks/"$net"/endpoints/"$eid" \
        "$MCTP_ENDPOINT_IFACE" Remove >/dev/null
    rc=$?

    if [ "$rc" -eq 0 ]; then
        logger -t mctp-setup "removed net $net eid $eid"
    else
        logger -t mctp-setup "failed to remove net $net eid $eid"
    fi
}

remove_local_addr() {
    ifname="$1"

    eid=$(mctp addr show "$ifname" | awk '{print $2}')
    [ -z "$eid" ] && return 0

    mctp addr del "$eid" dev "$ifname"
    rc=$?

    if [ "$rc" -eq 0 ]; then
        logger -t mctp-setup "removed local eid $eid from $ifname"
    else
        logger -t mctp-setup "failed to remove local eid $eid from $ifname"
    fi
}

# ---- globals ----

MCTP_BUS=au.com.codeconstruct.MCTP1
MCTP_ENDPOINT_IFACE=au.com.codeconstruct.MCTP.Endpoint1

# ---- main ----

# Down first, so the far side can't re-establish state while we're clearing it.
list_net_ifaces | while read -r ifname; do
    set_iface_state "$ifname" down
done

# Local addresses first: deleting them also drops mctpd's own endpoint
# object for that eid, so the eid list below only has remote peers left.
list_net_ifaces | while read -r ifname; do
    remove_local_addr "$ifname"
done

list_net_eids | while read -r eid; do
    remove_endpoint "$eid"
done

list_net_ifaces | while read -r ifname; do
    set_iface_state "$ifname" up
done

logger -t mctp-setup "reset complete for net $net"
