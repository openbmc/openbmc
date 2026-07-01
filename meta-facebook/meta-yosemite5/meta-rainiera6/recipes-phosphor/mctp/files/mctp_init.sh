#!/bin/bash

LOCAL_EID="${LOCAL_EID:-8}"
MCTP_MTU="${MCTP_MTU:-68}"

is_interface_up() {
    ip link show "$1" 2>/dev/null | grep -q ".*UP.*"
}

is_eid_assigned() {
    local iface="$1"
    local eid="$2"

    mctp addr show "$iface" 2>/dev/null | grep -q "eid $eid"
}

get_mctp_i2c_interfaces() {
    for dev in /sys/class/net/mctpi2c*; do
        [ -e "$dev" ] || continue
        basename "$dev"
    done
}

setup_mctp_interface() {
    local iface="$1"
    local rc=0

    if is_interface_up "$iface"; then
        echo "$iface is already up, skipping link up"
    else
        echo "Setting up $iface"
        mctp link set "$iface" up || rc=1
    fi

    echo "Setting MTU $MCTP_MTU on $iface"
    mctp link set "$iface" mtu "$MCTP_MTU" || rc=1

    if is_eid_assigned "$iface" "$LOCAL_EID"; then
        echo "EID $LOCAL_EID already assigned to $iface, skipping"
    else
        echo "Assigning EID $LOCAL_EID to $iface"
        mctp addr add "$LOCAL_EID" dev "$iface" || rc=1
    fi

    return "$rc"
}

rc=0

for iface in $(get_mctp_i2c_interfaces); do
    setup_mctp_interface "$iface" || rc=1
done

echo "MCTP init complete"
exit "$rc"
