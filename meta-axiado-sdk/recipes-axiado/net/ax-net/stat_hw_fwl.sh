#!/bin/bash

fnpfwl_bs=0x84100000

devmem_rd() {
    local address=$1
	local width=$2
    local output
    output=$(devmem "$address" "$width" 2>/dev/null)
    if [[ $? -eq 0 ]]; then
        echo "$output"
    else
        echo "0xDEAD"
    fi
}

print_stat_fwl_32() {
    local desc=$1
    local addr=$2
    local offset=$((fnpfwl_bs + addr))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd "$offset" 32 | xargs)"
}

print_stat_fwl_64() {
    local desc=$1
    local addr=$2
    local offset=$((fnpfwl_bs + addr))
    printf " %s (0x%X) : \t\t%s\n" "$desc" "$offset" "$(devmem_rd "$offset" 64 | xargs)"

}

echo "********** FNPFWL HW Stats **********"
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
