#!/bin/bash

dev="${1:-}"

# Mapping table: "DeviceLabel:PhysicalAddress:EID"
declare -a endpoint_map=(
    "mb_nic0_mctp:0x32:49"
    "rainbow0_mmc_mctp:0x21:10"
    "rainbow1_mmc_mctp:0x21:20"
    "rainbow2_mmc_mctp:0x21:30"
    "rainbow3_mmc_mctp:0x21:40"
    "swb_nic1_mctp:0x32:50"
    "swb_nic2_mctp:0x32:51"
    "swb_PCIe_switch_mctp:0x62:52"
    "swb_nic3_mctp:0x32:54"
    "swb_nic4_mctp:0x32:55"
)

# Build label -> interface mapping
declare -A label_to_iface

# Static device mapping
label_to_iface["mb_nic0_mctp"]="mctpi2c11"

# Hub device mapping (dynamic)
declare -A hub0_port_mapping=(
    [".port0"]="rainbow0_mmc_mctp"
    [".port1"]="rainbow2_mmc_mctp"
    [".port4"]="rainbow3_mmc_mctp"
    [".port5"]="rainbow1_mmc_mctp"
)

declare -A hub1_port_mapping=(
    [".port0"]="swb_nic1_mctp"
    [".port1"]="swb_nic2_mctp"
    [".port2"]="swb_PCIe_switch_mctp"
    [".port4"]="swb_nic3_mctp"
    [".port5"]="swb_nic4_mctp"
)

# Discover hub interfaces
hub_devices=$(i2cdetect -l | grep "hub0-" | grep -E "\.port[0-9]")
if [[ -n "$hub_devices" ]]; then
    mapfile -t hub_ids < <(echo "$hub_devices" | grep -oE "hub0-[a-f0-9]+" | awk '!seen[$0]++')

    hub_index=0
    for hub_id in "${hub_ids[@]}"; do
        hub_ports=$(echo "$hub_devices" | grep "$hub_id" | grep -oE "\.port[0-9]" | sort -n -t. -k2)

        for port in $hub_ports; do
            device_name=""
            if [[ $hub_index -eq 0 ]]; then
                device_name="${hub0_port_mapping[$port]}"
            elif [[ $hub_index -eq 1 ]]; then
                device_name="${hub1_port_mapping[$port]}"
            fi

            if [[ -n "$device_name" ]]; then
                i2c_num=$(echo "$hub_devices" | grep "$hub_id$port" | awk '{print $1}' | cut -d'-' -f2)
                label_to_iface["$device_name"]="mctpi2c$i2c_num"
            fi
        done

        hub_index=$((hub_index + 1))
    done
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
