#!/bin/bash

LOCAL_EID=8

is_interface_up() {
    ip link show "$1" | grep -q ".*UP.*"
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

    if is_interface_up "$iface"; then
        echo "$iface is already up, skipping"
    else
        echo "Setting up $iface"
        mctp link set "$iface" mtu 68 up
    fi

    if is_eid_assigned "$iface" "$LOCAL_EID"; then
        echo "EID $LOCAL_EID already assigned to $iface, skipping"
    else
        echo "Assigning EID $LOCAL_EID to $iface"
        mctp addr add "$LOCAL_EID" dev "$iface"
    fi
}

for iface in $(get_mctp_i2c_interfaces); do
    setup_mctp_interface "$iface"
done

echo "MCTP init complete"