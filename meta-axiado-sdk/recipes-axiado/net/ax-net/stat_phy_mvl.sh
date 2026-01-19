#!/bin/bash

shim_bs=0x83100000
MDIO_CFG_STATUS=0x30
MDIO_COMMAND=0x34
MDIO_DATA=0x38
MDIO_REGADDR=0x3C

devmem_rd() {
	local offset=$1
	local addr=$((shim_bs + mac_1g_bs + offset))
	local output
    output=$(devmem "$addr" w 2>/dev/null)
    if [[ $? -eq 0 ]]; then
        echo "$output"
    else
        echo "0xDEAD"
    fi
}

devmem_wr() {
	local offset=$1
	local data=$2
	local addr=$((shim_bs + mac_1g_bs + offset))
	local output
	output=$(devmem "$addr" w "$data" 2>/dev/null)
}

set_mdio_page() {
	local page=$1
	devmem_wr $MDIO_CFG_STATUS 0x0B0C
	devmem_wr $MDIO_COMMAND 0x16
	devmem_wr $MDIO_DATA $page
}

print_mdio_1g_reg_read() {
	local desc=$1
	local reg=$2
	devmem_wr $MDIO_COMMAND $((reg | 0x8000))
	printf "%s - Reg 0x%X : 0x%08X\n" "$desc" "$reg" "$(devmem_rd "$MDIO_DATA" | xargs)"
}

print_help() {
    local name=$1
	echo "Read PHY Registers"
    echo "Usage: $name <SGMII idx>"
    echo -e "\t<SGMII idx>\t\t:  0 - Vail, 1 - Snowbird"
    echo "ex: bash $name 1"
}

if [[ $# -ne 1 || ($1 != "0" && $1 != "1") ]]; then
    echo "ERROR: Invalid args"
    print_help "$0"
    exit 1
fi

sgmii_idx=$1
mac_idx=$((sgmii_idx + 1))
mac_1g_bs=$((mac_idx * 0x400 + 0x400))


set_mdio_page 0
print_mdio_1g_reg_read "Copper Control Register" 0
print_mdio_1g_reg_read "Copper Status Register" 1
print_mdio_1g_reg_read "PHY Identifier 1" 2
print_mdio_1g_reg_read "PHY Identifier 2" 3
print_mdio_1g_reg_read "Copper Auto-Negotiation Advertisement Register" 4
print_mdio_1g_reg_read "Copper Link Partner Ability Register - Base Page" 5
print_mdio_1g_reg_read "Copper Auto-Negotiation Expansion Register" 6
print_mdio_1g_reg_read "Copper Next Page Transmit Register" 7
print_mdio_1g_reg_read "Copper Link Partner Next Page Register" 8
print_mdio_1g_reg_read "1000BASE-T Control Register" 9
print_mdio_1g_reg_read "1000BASE-T Status Register" 10
print_mdio_1g_reg_read "Extended Status Register" 15
print_mdio_1g_reg_read "Copper Specific Control Register 1" 16
print_mdio_1g_reg_read "Copper Specific Status Register 1" 17
print_mdio_1g_reg_read "Copper Specific Interrupt Enable Register" 18
print_mdio_1g_reg_read "Copper Interrupt Status Register" 19
print_mdio_1g_reg_read "Copper Specific Control Register 2" 20
print_mdio_1g_reg_read "Copper Specific Receive Error Counter Register" 21
print_mdio_1g_reg_read "Page Address" 22
print_mdio_1g_reg_read "Global Interrupt Status" 23
set_mdio_page 1
print_mdio_1g_reg_read "Fiber Control Register" 0
print_mdio_1g_reg_read "Fiber Status Register" 1
print_mdio_1g_reg_read "PHY Identifier" 2
print_mdio_1g_reg_read "PHY Identifier" 3
print_mdio_1g_reg_read "Fiber Auto-Negotiation Advertisement Register - 1000BASE-X Mode" 4
print_mdio_1g_reg_read "Fiber Link Partner Ability Register - 1000BASE-X Mode" 5
print_mdio_1g_reg_read "Fiber Auto-Negotiation Expansion Register" 6
print_mdio_1g_reg_read "Fiber Next Page Transmit Register" 7
print_mdio_1g_reg_read "Fiber Link Partner Next Page Register" 8
print_mdio_1g_reg_read "Extended Status Register" 15
print_mdio_1g_reg_read "Fiber Specific Control Register 1" 16
print_mdio_1g_reg_read "Fiber Specific Status Register" 17
print_mdio_1g_reg_read "Fiber Interrupt Enable Register" 18
print_mdio_1g_reg_read "Fiber Interrupt Status Register" 19
print_mdio_1g_reg_read "PRBS Control" 23
print_mdio_1g_reg_read "PRBS Error Counter LSB" 24
print_mdio_1g_reg_read "PRBS Error Counter MSB" 25
print_mdio_1g_reg_read "Fiber Specific Control Register 2" 26
set_mdio_page 2
print_mdio_1g_reg_read "MAC Specific Control Register 1" 16
print_mdio_1g_reg_read "MAC Specific Interrupt Enable Register" 18
print_mdio_1g_reg_read "MAC Specific Status Register" 19
print_mdio_1g_reg_read "MAC Specific Control Register 2" 21
print_mdio_1g_reg_read "RGMII Output Impedance Calibration Override" 24
set_mdio_page 3
print_mdio_1g_reg_read "LED[2:0] Function Control Register" 16
print_mdio_1g_reg_read "LED[2:0] Polarity Control Register" 17
print_mdio_1g_reg_read "LED Timer Control Register" 18
set_mdio_page 5
print_mdio_1g_reg_read "1000BASE-T Pair Skew Register" 20
print_mdio_1g_reg_read "1000BASE-T Pair Swap and Polarity" 21
set_mdio_page 6
print_mdio_1g_reg_read "Copper Port Packet Generation" 16
print_mdio_1g_reg_read "Copper Port CRC Counters" 17
print_mdio_1g_reg_read "Checker Control" 18
print_mdio_1g_reg_read "Copper Port Packet Generation" 19
print_mdio_1g_reg_read "Late Collision Counters 1 & 2" 23
print_mdio_1g_reg_read "Late Collision Counters 3 & 4" 24
print_mdio_1g_reg_read "Late Collision Window Adjust/Link Disconnect" 25
print_mdio_1g_reg_read "Misc Test" 26
print_mdio_1g_reg_read "Misc Test: Temperature Sensor Alternative Reading" 27
set_mdio_page 18
print_mdio_1g_reg_read "Packet Generation" 16
print_mdio_1g_reg_read "CRC Counters" 17
print_mdio_1g_reg_read "Checker Control" 18
print_mdio_1g_reg_read "Packet Generation" 19
print_mdio_1g_reg_read "General Control Register 1" 20
