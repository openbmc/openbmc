#!/bin/bash

eip_bs=0x83000000
ring_in_bs=0xFFB00
ring_out_bs=0xFFB08
ring_dbg_state=0xFFF00

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

print_ring_in_cnt() {
	echo "         RING IN COUNT"
	for ir in {0..15};
	do
		local addr=$((eip_bs + ring_in_bs + $ir*16))
		printf " 0x%X R_%d \t: => %s\n" "$addr" "$ir" "$(devmem_rd "$addr" | xargs)"
	done
}

print_ring_out_cnt() {
	echo "         RING OUT COUNT"
	for ir in {0..15};
	do
		local addr=$((eip_bs + ring_out_bs + $ir*16))
		printf " 0x%X R_%d \t: => %s\n" "$addr" "$ir" "$(devmem_rd "$addr" | xargs)"
	done
}

print_ring_dbg_state() {
	echo "         RING DEBUG STATE"
	for ir in {0..15};
	do
		local addr=$((eip_bs + ring_dbg_state + $ir*8))
		printf " 0x%X R_%d \t: => %s\n" "$addr" "$ir" "$(devmem_rd "$addr" | xargs)"
	done
}

print_ring_in_cnt
print_ring_out_cnt
print_ring_dbg_state
