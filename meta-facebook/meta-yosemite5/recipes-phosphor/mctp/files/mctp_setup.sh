#!/bin/bash

dev="${1:-}"

# Mapping table: "devname:iface:physaddr:eid"
declare -a endpoint_map=(
    "nic_mctp:mctpi2c4:0x32:10"
    "cxl_mctp:mctpi2c12:0x32:20"
)

is_eid_assigned() {
    busctl tree au.com.codeconstruct.MCTP1 | grep -q "/endpoints/$1"
}

setup_endpoint() {
    local devname="$1"
    local iface="$2"
    local physaddr="$3"
    local eid="$4"

    if is_eid_assigned "$eid"; then
        echo "Setting up $devname on $iface (EID=$eid, Addr=$physaddr): Skipped (EID already assigned)"
        return
    fi

    if busctl call "au.com.codeconstruct.MCTP1" \
        "/au/com/codeconstruct/mctp1/interfaces/$iface" \
        "au.com.codeconstruct.MCTP.BusOwner1" AssignEndpointStatic ayy 1 "$physaddr" "$eid"; then
        echo "Setting up $devname on $iface (EID=$eid, Addr=$physaddr): Success"
    else
        echo "Setting up $devname on $iface (EID=$eid, Addr=$physaddr): Failed" >&2
    fi
}

if [[ -n "$dev" ]]; then
    for entry in "${endpoint_map[@]}"; do
        IFS=":" read -r devname iface physaddr eid <<< "$entry"
        if [[ "$devname" == "$dev" ]]; then
            setup_endpoint "$devname" "$iface" "$physaddr" "$eid"
            exit 0
        fi
    done
    echo "Device '$dev' not found in mapping" >&2
    exit 1
else
    for entry in "${endpoint_map[@]}"; do
        IFS=":" read -r devname iface physaddr eid <<< "$entry"
        setup_endpoint "$devname" "$iface" "$physaddr" "$eid"
    done
fi