#!/bin/bash

eip_bs=0x83000000
shim_bs=0x83100000
fnpfwl_bs=0x84100000
ring_in_bs=0xFFB00
ring_out_bs=0xFFB08
MDIO_CFG_STATUS=0x30
MDIO_COMMAND=0x34
MDIO_DATA=0x38
MDIO_REGADDR=0x3C

Hopevalley="0x486f7065"
Hopevalley2="0x486f706C"
Vail1="0x7661696D"
Vail2="0x7661696E"
Vail3="0x76616970"
Tahoev1="0x5C6F786F"
ThMiniS1V1="0x6D546873"
Snowbird="0x736e6f77"
Snowbirdv2="0x736E6F78"
Tahoeminiv1="0x6D54686F"
Tahoeminiv2="0x6D546870"
Yosemite="0x79763330"


devmem_rd() {
	local address=$1
	local output
	output=$(devmem "$address" 2>/dev/null)
	if [[ $? -eq 0 ]]; then
		echo "$output"
	else
		echo "0xDEADBEEF"
	fi
}

devmem_rd_64() {
	local address=$1
	local output
	output=$(devmem "$address" 64 2>/dev/null)
	if [[ $? -eq 0 ]]; then
		echo "$output"
	else
		echo "0xDEADBEEFDEADBEEF"
	fi
}

devmem_wr() {
	local address=$1
    local data=$2
    local output
    output=$(devmem "$address" w "$data" 2>/dev/null)
	sleep 0.001
}

print_ring_in_cnt() {
    echo "         RING IN COUNT"
    for ir in {0..5};
    do
        local addr=$((eip_bs + ring_in_bs + $ir*16))
        printf " 0x%X R_%d \t: => %s\n" "$addr" "$ir" "$(devmem_rd "$addr" | xargs)"
    done
}

print_ring_out_cnt() {
    echo "         RING OUT COUNT"
    for ir in {0..5};
    do
        local addr=$((eip_bs + ring_out_bs + $ir*16))
        printf " 0x%X R_%d \t: => %s\n" "$addr" "$ir" "$(devmem_rd "$addr" | xargs)"
    done
}

print_eip_ring_stats() {
	echo -e "\n\n############## EIP #####################"
	print_ring_in_cnt
	print_ring_out_cnt
}

print_stat_1g() {
    local desc=$1
    local addr=$2
	local mac_1g_bs=$3
    local offset=$((shim_bs + mac_1g_bs + addr))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd "$offset" | xargs)"
}

print_shim_stat_1g() {
    local desc=$1
    local addr=$2
	local sgmii_idx=$3
    local offset=$((shim_bs + addr + 4 + (sgmii_idx * 4)))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd "$offset" | xargs)"
}

print_shim_mac_stats() {
	local sgmii_idx=$1
	local mac_idx=$((sgmii_idx + 1))
	local mac_1g_bs=$((mac_idx * 0x400 + 0x400))

	echo -e "\n\n############## SHIM/MAC SGMII-$sgmii_idx #####################"
	echo "********** Rx - MAC-1G **********"
	print_stat_1g "Good   " 0x120 $mac_1g_bs
	print_stat_1g "Drop   " 0x158 $mac_1g_bs
	print_stat_1g "Total  " 0x160 $mac_1g_bs
	print_stat_1g "CRC Err" 0x128 $mac_1g_bs
	print_stat_1g "If In Err" 0x138 $mac_1g_bs
	print_stat_1g "Undr Sz Err" 0x168 $mac_1g_bs
	print_stat_1g "Ovr Sz Err" 0x1A8 $mac_1g_bs
	print_stat_1g "Jabber Err " 0x1B0 $mac_1g_bs
	print_stat_1g "Frag Err" 0x1B8 $mac_1g_bs

	echo "********* SHIM Rx Stats *********"
	print_shim_stat_1g "Total   " 0x60 $sgmii_idx
	print_shim_stat_1g "Good    " 0x80 $sgmii_idx
	print_shim_stat_1g "Bad    " 0xA0 $sgmii_idx

	echo "********** Tx - MAC-1G **********"
	print_stat_1g "Total   " 0x260 $mac_1g_bs
	print_stat_1g "Good    " 0x220 $mac_1g_bs
	print_stat_1g "Drop   " 0x258 $mac_1g_bs
	print_stat_1g "CRC Err" 0x228 $mac_1g_bs
	print_stat_1g "If Out Err" 0x238 $mac_1g_bs
}


set_mdio_page() {
	local sgmii_idx=$1
	local page=$2
	local mac_idx=$((sgmii_idx + 1))
	local mac_1g_bs=$((mac_idx * 0x400 + 0x400))
	devmem_wr $((shim_bs + mac_1g_bs + MDIO_CFG_STATUS)) 0x0B0C
	devmem_wr $((shim_bs + mac_1g_bs + MDIO_COMMAND)) 0x16
	devmem_wr $((shim_bs + mac_1g_bs + MDIO_DATA)) $page
}

print_mdio_1g_reg_read() {
	local sgmii_idx=$1
    local desc=$2
    local reg=$3
	local mac_idx=$((sgmii_idx + 1))
	local mac_1g_bs=$((mac_idx * 0x400 + 0x400))
	local addr_data=$((shim_bs + mac_1g_bs + MDIO_DATA))
    devmem_wr $((shim_bs + mac_1g_bs + MDIO_COMMAND)) $((reg | 0x8000))
    printf "%s - Reg 0x%X : 0x%08X\n" "$desc" "$reg" "$(devmem_rd "$addr_data" | xargs)"
}

print_phy_mvl_stats() {
	local sgmii_idx=$1
	set_mdio_page $sgmii_idx 0
	echo -e "\n\n############## PHY #####################"
	print_mdio_1g_reg_read $sgmii_idx "Copper Control Register" 0
	print_mdio_1g_reg_read $sgmii_idx "Copper Status Register" 1
	print_mdio_1g_reg_read $sgmii_idx "PHY Identifier 1" 2
	print_mdio_1g_reg_read $sgmii_idx "PHY Identifier 2" 3
	print_mdio_1g_reg_read $sgmii_idx "Copper Auto-Negotiation Advertisement Register" 4
	print_mdio_1g_reg_read $sgmii_idx "Copper Link Partner Ability Register - Base Page" 5
	print_mdio_1g_reg_read $sgmii_idx "Copper Auto-Negotiation Expansion Register" 6
	print_mdio_1g_reg_read $sgmii_idx "Copper Next Page Transmit Register" 7
	print_mdio_1g_reg_read $sgmii_idx "Copper Link Partner Next Page Register" 8
	print_mdio_1g_reg_read $sgmii_idx "1000BASE-T Control Register" 9
	print_mdio_1g_reg_read $sgmii_idx "1000BASE-T Status Register" 10
	print_mdio_1g_reg_read $sgmii_idx "Extended Status Register" 15
	print_mdio_1g_reg_read $sgmii_idx "Copper Specific Control Register 1" 16
	print_mdio_1g_reg_read $sgmii_idx "Copper Specific Status Register 1" 17
	print_mdio_1g_reg_read $sgmii_idx "Copper Specific Interrupt Enable Register" 18
	print_mdio_1g_reg_read $sgmii_idx "Copper Interrupt Status Register" 19
	print_mdio_1g_reg_read $sgmii_idx "Copper Specific Control Register 2" 20
	print_mdio_1g_reg_read $sgmii_idx "Copper Specific Receive Error Counter Register" 21
	print_mdio_1g_reg_read $sgmii_idx "Page Address" 22
	print_mdio_1g_reg_read $sgmii_idx "Global Interrupt Status" 23
	set_mdio_page $sgmii_idx 1
	print_mdio_1g_reg_read $sgmii_idx "Fiber Control Register" 0
	print_mdio_1g_reg_read $sgmii_idx "Fiber Status Register" 1
	print_mdio_1g_reg_read $sgmii_idx "PHY Identifier" 2
	print_mdio_1g_reg_read $sgmii_idx "PHY Identifier" 3
	print_mdio_1g_reg_read $sgmii_idx "Fiber Auto-Negotiation Advertisement Register - 1000BASE-X Mode" 4
	print_mdio_1g_reg_read $sgmii_idx "Fiber Link Partner Ability Register - 1000BASE-X Mode" 5
	print_mdio_1g_reg_read $sgmii_idx "Fiber Auto-Negotiation Expansion Register" 6
	print_mdio_1g_reg_read $sgmii_idx "Fiber Next Page Transmit Register" 7
	print_mdio_1g_reg_read $sgmii_idx "Fiber Link Partner Next Page Register" 8
	print_mdio_1g_reg_read $sgmii_idx "Extended Status Register" 15
	print_mdio_1g_reg_read $sgmii_idx "Fiber Specific Control Register 1" 16
	print_mdio_1g_reg_read $sgmii_idx "Fiber Specific Status Register" 17
	print_mdio_1g_reg_read $sgmii_idx "Fiber Interrupt Enable Register" 18
	print_mdio_1g_reg_read $sgmii_idx "Fiber Interrupt Status Register" 19
	print_mdio_1g_reg_read $sgmii_idx "PRBS Control" 23
	print_mdio_1g_reg_read $sgmii_idx "PRBS Error Counter LSB" 24
	print_mdio_1g_reg_read $sgmii_idx "PRBS Error Counter MSB" 25
	print_mdio_1g_reg_read $sgmii_idx "Fiber Specific Control Register 2" 26
	set_mdio_page $sgmii_idx 2
	print_mdio_1g_reg_read $sgmii_idx "MAC Specific Control Register 1" 16
	print_mdio_1g_reg_read $sgmii_idx "MAC Specific Interrupt Enable Register" 18
	print_mdio_1g_reg_read $sgmii_idx "MAC Specific Status Register" 19
	print_mdio_1g_reg_read $sgmii_idx "MAC Specific Control Register 2" 21
	print_mdio_1g_reg_read $sgmii_idx "RGMII Output Impedance Calibration Override" 24
	set_mdio_page $sgmii_idx 3
	print_mdio_1g_reg_read $sgmii_idx "LED[2:0] Function Control Register" 16
	print_mdio_1g_reg_read $sgmii_idx "LED[2:0] Polarity Control Register" 17
	print_mdio_1g_reg_read $sgmii_idx "LED Timer Control Register" 18
	set_mdio_page $sgmii_idx 5
	print_mdio_1g_reg_read $sgmii_idx "1000BASE-T Pair Skew Register" 20
	print_mdio_1g_reg_read $sgmii_idx "1000BASE-T Pair Swap and Polarity" 21
	set_mdio_page $sgmii_idx 6
	print_mdio_1g_reg_read $sgmii_idx "Copper Port Packet Generation" 16
	print_mdio_1g_reg_read $sgmii_idx "Copper Port CRC Counters" 17
	print_mdio_1g_reg_read $sgmii_idx "Checker Control" 18
	print_mdio_1g_reg_read $sgmii_idx "Copper Port Packet Generation" 19
	print_mdio_1g_reg_read $sgmii_idx "Late Collision Counters 1 & 2" 23
	print_mdio_1g_reg_read $sgmii_idx "Late Collision Counters 3 & 4" 24
	print_mdio_1g_reg_read $sgmii_idx "Late Collision Window Adjust/Link Disconnect" 25
	print_mdio_1g_reg_read $sgmii_idx "Misc Test" 26
	print_mdio_1g_reg_read $sgmii_idx "Misc Test: Temperature Sensor Alternative Reading" 27
	set_mdio_page $sgmii_idx 18
	print_mdio_1g_reg_read $sgmii_idx "Packet Generation" 16
	print_mdio_1g_reg_read $sgmii_idx "CRC Counters" 17
	print_mdio_1g_reg_read $sgmii_idx "Checker Control" 18
	print_mdio_1g_reg_read $sgmii_idx "Packet Generation" 19
	print_mdio_1g_reg_read $sgmii_idx "General Control Register 1" 20
}

print_phy_vsc_stats() {
	local sgmii_idx=$1
	local mac_idx=$((sgmii_idx + 1))
	local mac_1g_bs=$((mac_idx * 0x400 + 0x400))
	echo -e "\n\n############## PHY #####################"
	devmem_wr $((shim_bs + mac_1g_bs + MDIO_CFG_STATUS)) 0x0B0C
    print_mdio_1g_reg_read $sgmii_idx "Mode Control" 0
    print_mdio_1g_reg_read $sgmii_idx "Mode status" 1
    print_mdio_1g_reg_read $sgmii_idx "PHY identifier 1" 2
    print_mdio_1g_reg_read $sgmii_idx "PHY identifier 2" 3
    print_mdio_1g_reg_read $sgmii_idx "Auto-negotiation advertisement" 4
    print_mdio_1g_reg_read $sgmii_idx "Auto-negotiation link partner ability" 5
    print_mdio_1g_reg_read $sgmii_idx "Auto-negotiation expansion" 6
    print_mdio_1g_reg_read $sgmii_idx "Auto-negotiation next-page transmit" 7
    print_mdio_1g_reg_read $sgmii_idx "Auto-negotiation link partner next-page receive" 8
    print_mdio_1g_reg_read $sgmii_idx "1000BASE-T control" 9
    print_mdio_1g_reg_read $sgmii_idx "1000BASE-T status" 10
    print_mdio_1g_reg_read $sgmii_idx "1000BASE-T status extension 1" 15
    print_mdio_1g_reg_read $sgmii_idx "100BASE-TX status extension" 16
    print_mdio_1g_reg_read $sgmii_idx "1000BASE-T status extension 2" 17
    print_mdio_1g_reg_read $sgmii_idx "Bypass control" 18
    print_mdio_1g_reg_read $sgmii_idx "Error Counter 1" 19
    print_mdio_1g_reg_read $sgmii_idx "Error Counter 2" 20
    print_mdio_1g_reg_read $sgmii_idx "Error Counter 3" 21
    print_mdio_1g_reg_read $sgmii_idx "Extended control and status" 22
    print_mdio_1g_reg_read $sgmii_idx "Extended PHY control 1" 23
    print_mdio_1g_reg_read $sgmii_idx "Extended PHY control 2" 24
    print_mdio_1g_reg_read $sgmii_idx "Interrupt mask" 25
    print_mdio_1g_reg_read $sgmii_idx "Interrupt status" 26
    print_mdio_1g_reg_read $sgmii_idx "MAC interface auto-negotiation control and status" 27
    print_mdio_1g_reg_read $sgmii_idx "Auxiliary control and status" 28
    print_mdio_1g_reg_read $sgmii_idx "LED mode select" 29
    print_mdio_1g_reg_read $sgmii_idx "LED behavior" 30
    print_mdio_1g_reg_read $sgmii_idx "Extended register page access" 31
}


print_stat_fwl_32() {
    local desc=$1
    local addr=$2
    local offset=$((fnpfwl_bs + addr))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd "$offset" | xargs)"
}

print_stat_fwl_64() {
    local desc=$1
    local addr=$2
    local offset=$((fnpfwl_bs + addr))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd_64 "$offset" | xargs)"
}

print_hw_fwl_stats() {
	echo -e "\n\n############## HW-FWL #####################"
	print_stat_fwl_64 "STATS_BYTE_CNT           " 0x66498
	print_stat_fwl_64 "STATS_PKT_CNT            " 0x66490
	print_stat_fwl_64 "STATS_SRC_MATCH_CNT      " 0x664a0
	print_stat_fwl_64 "STATS_SRC_NOMATCH_CNT    " 0x664a8
	#print_stat_fwl_64 "STATS_SRC_TO_ML_CNT      " 0x664b0
	#print_stat_fwl_64 "STATS_SRC_TO_DOS_CNT     " 0x664b8
	print_stat_fwl_64 "STATS_PKT_ABORT_CNT      " 0x66510
	print_stat_fwl_64 "STATS_PARSE_UNDERFLOW_CNT" 0x66518
	#print_stat_fwl_32 "FRD_WATCHDOG_MAX_COUNT   " 0x663e0
	print_stat_fwl_32 "FRD_FIFO_STATUS          " 0x663e8
	#print_stat_fwl_32 "FWL_FSM_ENABLE           " 0x663b8
	#print_stat_fwl_64 "PKT_PTR                  " 0x663c0
	#print_stat_fwl_64 "FRD_PTR                  " 0x663d0
	#print_stat_fwl_64 "SRC_RSLT                 " 0x66140
	#print_stat_fwl_64 "PKT_PTR_FIFO-0           " 0x65000
	#print_stat_fwl_64 "PKT_PTR_FIFO-8           " 0x65008
	#print_stat_fwl_64 "PKT_PTR_FIFO-10          " 0x65010
	#print_stat_fwl_64 "FRD_MEM-0                " 0x60000
	#print_stat_fwl_64 "FRD_MEM-8                " 0x60008
	#print_stat_fwl_64 "FRD_MEM-10               " 0x60010
	#print_stat_fwl_64 "FRD_MEM-18               " 0x60018
}

print_hw_fwl_stats
print_eip_ring_stats

board_type=$(devmem 0x80620808 2>&1)
case "$board_type" in
$Hopevalley)
	print_shim_mac_stats 0
	print_phy_vsc_stats 0
	print_shim_mac_stats 1
	print_shim_mac_stats 2
	print_shim_mac_stats 3
    ;;
$Hopevalley2)
	print_shim_mac_stats 0
	print_phy_mvl_stats 0
	print_shim_mac_stats 1
	print_phy_mvl_stats 1
	print_shim_mac_stats 2
	print_phy_mvl_stats 2
	print_shim_mac_stats 3
	print_phy_mvl_stats 3
    ;;
$Vail1)
	print_shim_mac_stats 0
	print_phy_mvl_stats 0
    ;;
$Vail2)
	print_shim_mac_stats 0
	print_phy_mvl_stats 0
    ;;
$Vail3)
	print_shim_mac_stats 0
	print_phy_mvl_stats 0
    ;;
$Tahoev1)
	print_shim_mac_stats 0
	print_phy_mvl_stats 0
	print_shim_mac_stats 1
    ;;
$ThMiniS1V1)
	print_shim_mac_stats 3
    ;;
$Snowbird)
	print_shim_mac_stats 0
	print_shim_mac_stats 1
	print_phy_mvl_stats 1
    ;;
$Snowbirdv2)
	print_shim_mac_stats 3
	print_phy_mvl_stats 3
    ;;
$Tahoeminiv1)
	print_shim_mac_stats 0
    ;;
$Tahoeminiv2)
	print_shim_mac_stats 3
    ;;
$Yosemite)
	print_shim_mac_stats 0
	print_shim_mac_stats 1
    ;;
*) 
   ;;
esac
