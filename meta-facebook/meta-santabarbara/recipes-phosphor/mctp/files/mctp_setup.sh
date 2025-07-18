#!/bin/bash

dev="${1:-}"

# Mapping table: "DeviceLabel:PhysicalAddress:EID"
declare -a endpoint_map=(
    "mb_nic0_mctp:0x32:9"
    "rainbow0_mmc_mctp:0x21:10"
    "rainbow1_mmc_mctp:0x21:20"
    "rainbow2_mmc_mctp:0x21:30"
    "rainbow3_mmc_mctp:0x21:40"
    "swb_nic1_mctp:0x32:50"
    "swb_nic2_mctp:0x32:51"
    "swb_pex90144_mctp:0x62:52"
    "swb_nic3_mctp:0x32:54"
    "swb_nic4_mctp:0x32:55"
)

# Build label -> interface mapping
declare -A label_to_iface
for iface_path in /sys/class/net/mctpi2c*; do
    label_file="$iface_path/device/of_node/mctp@10/label"
    if [[ -f "$label_file" ]]; then
        IFS= read -r label < "$label_file"
        iface=$(basename "$iface_path")
        label_to_iface["$label"]="$iface"
    fi
done

if [[ ${#label_to_iface[@]} -eq 0 ]]; then
    echo "No MCTP interfaces with labels found." >&2
    exit 1
fi

is_eid_assigned() {
    busctl tree au.com.codeconstruct.MCTP1 | grep -q "/endpoints/$1"
}

setup_endpoint() {
    local iface="$1"
    local label="$2"
    local physaddr="$3"
    local eid="$4"

    if is_eid_assigned "$eid"; then
        echo "Setting up $label on $iface (EID=$eid, Addr=$physaddr): Skipped (EID already assigned)"
        return
    fi

    if busctl call "au.com.codeconstruct.MCTP1" \
        "/au/com/codeconstruct/mctp1/interfaces/$iface" \
        "au.com.codeconstruct.MCTP.BusOwner1" AssignEndpointStatic ayy 1 "$physaddr" "$eid"; then
        echo "Setting up $label on $iface (EID=$eid, Addr=$physaddr): Success"
    else
        echo "Setting up $label on $iface (EID=$eid, Addr=$physaddr): Failed" >&2
    fi
}

if [[ -n "$dev" ]]; then
    for entry in "${endpoint_map[@]}"; do
        IFS=":" read -r label physaddr eid <<< "$entry"
        if [[ "$label" == "$dev" ]]; then
            iface="${label_to_iface[$label]}"
            if [[ -z "$iface" ]]; then
                echo "No matching MCTP interface found for label: $label" >&2
                exit 1
            fi
            setup_endpoint "$iface" "$label" "$physaddr" "$eid"
            exit 0
        fi
    done
    echo "Device '$dev' not found in mapping" >&2
    exit 1
else
    for entry in "${endpoint_map[@]}"; do
        IFS=":" read -r label physaddr eid <<< "$entry"
        iface="${label_to_iface[$label]}"
        if [[ -z "$iface" ]]; then
            echo "Skipping $label: no matching MCTP interface found" >&2
            continue
        fi
        setup_endpoint "$iface" "$label" "$physaddr" "$eid"
    done
fi
