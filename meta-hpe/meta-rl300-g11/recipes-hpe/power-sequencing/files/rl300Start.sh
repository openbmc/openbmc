#!/bin/sh
# A power up request has been made we must setup the system properly
# RL300 requires UEFI VAR ROM export through spi controller 1
# the machine needs to get out of Hold too

rmmod gxp_spifi_ctrl1
# vejmarie was 58
devmem 0xd1000119 32 0x5d
modprobe gxp_spifi_ctrl1

# clear previous reset reason
devmem 0x80000074 16 0x0
# Release the Soc
currentVal=$(devmem 0xD100011A 8)
currentVal=$(( currentVal | 1 << 3 ))
devmem 0xD100011A 8 "0x""${currentVal}"
