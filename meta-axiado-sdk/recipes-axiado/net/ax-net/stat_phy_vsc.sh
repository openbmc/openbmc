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


print_mdio_1g_reg_read() {
	local desc=$1
	local reg=$2
	devmem_wr $MDIO_COMMAND $((reg | 0x8000))
	printf "%s - Reg 0x%X : 0x%08X\n" "$desc" "$reg" "$(devmem_rd "$MDIO_DATA" | xargs)"
}

sgmii_idx=0
mac_idx=$((sgmii_idx + 1))
mac_1g_bs=$((mac_idx * 0x400 + 0x400))


devmem_wr $MDIO_CFG_STATUS 0x0B0C
print_mdio_1g_reg_read "Mode Control" 0
print_mdio_1g_reg_read "Mode status" 1
print_mdio_1g_reg_read "PHY identifier 1" 2
print_mdio_1g_reg_read "PHY identifier 2" 3
print_mdio_1g_reg_read "Auto-negotiation advertisement" 4
print_mdio_1g_reg_read "Auto-negotiation link partner ability" 5
print_mdio_1g_reg_read "Auto-negotiation expansion" 6
print_mdio_1g_reg_read "Auto-negotiation next-page transmit" 7
print_mdio_1g_reg_read "Auto-negotiation link partner next-page receive" 8
print_mdio_1g_reg_read "1000BASE-T control" 9
print_mdio_1g_reg_read "1000BASE-T status" 10
print_mdio_1g_reg_read "1000BASE-T status extension 1" 15
print_mdio_1g_reg_read "100BASE-TX status extension" 16
print_mdio_1g_reg_read "1000BASE-T status extension 2" 17
print_mdio_1g_reg_read "Bypass control" 18
print_mdio_1g_reg_read "Error Counter 1" 19
print_mdio_1g_reg_read "Error Counter 2" 20
print_mdio_1g_reg_read "Error Counter 3" 21
print_mdio_1g_reg_read "Extended control and status" 22
print_mdio_1g_reg_read "Extended PHY control 1" 23
print_mdio_1g_reg_read "Extended PHY control 2" 24
print_mdio_1g_reg_read "Interrupt mask" 25
print_mdio_1g_reg_read "Interrupt status" 26
print_mdio_1g_reg_read "MAC interface auto-negotiation control and status" 27
print_mdio_1g_reg_read "Auxiliary control and status" 28
print_mdio_1g_reg_read "LED mode select" 29
print_mdio_1g_reg_read "LED behavior" 30
print_mdio_1g_reg_read "Extended register page access" 31
