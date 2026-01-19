#!/bin/bash

shim_bs=0x83100000

devmem_rd() {
    local address=$1
    local output
    output=$(devmem "$address" 2>/dev/null)
    if [[ $? -eq 0 ]]; then
        echo "$output"
    else
        echo "0xDEAD"
    fi
}

print_stat_1g() {
    local desc=$1
    local addr=$2
    local offset=$((shim_bs + mac_1g_bs + addr))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd "$offset" | xargs)"
}

print_shim_stat_1g() {
    local desc=$1
    local addr=$2
    local offset=$((shim_bs + addr + 4 + (sgmii_idx * 4)))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd "$offset" | xargs)"
}

print_help() {
    local name=$1
    echo "Usage: $name <SGMII idx>"
    echo -e "\t<SGMII idx>\t\t:  0 - Vail, 1 - Snowbird"
    echo "ex: bash $name 1"
}

if [[ $# -ne 1 || ($1 < 0 || $1 > 3) ]]; then
    echo "ERROR: Invalid args"
    print_help "$0"
    exit 1
fi

sgmii_idx=$1
mac_idx=$((sgmii_idx + 1))
mac_1g_bs=$((mac_idx * 0x400 + 0x400))

echo "********** Rx - MAC-1G **********"
print_stat_1g "Good   " 0x120
print_stat_1g "Drop   " 0x158
print_stat_1g "Total  " 0x160
print_stat_1g "CRC Err" 0x128
print_stat_1g "If In Err" 0x138
print_stat_1g "Undr Sz Err" 0x168
print_stat_1g "Ovr Sz Err" 0x1A8
print_stat_1g "Jabber Err " 0x1B0
print_stat_1g "Frag Err" 0x1B8

echo "********* SHIM Rx Stats *********"
print_shim_stat_1g "Total   " 0x60
print_shim_stat_1g "Good    " 0x80
print_shim_stat_1g "Bad    " 0xA0

echo "********** Tx - MAC-1G **********"
print_stat_1g "Total   " 0x260
print_stat_1g "Good    " 0x220
print_stat_1g "Drop   " 0x258
print_stat_1g "CRC Err" 0x228
print_stat_1g "If Out Err" 0x238
