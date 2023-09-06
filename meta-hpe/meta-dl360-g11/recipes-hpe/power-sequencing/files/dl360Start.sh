#!/bin/sh
# A power up request has been made we must setup the system properly
# the machine needs to get out of Hold too

# We need to enable UART PortA
devmem 0x80fc0230 8 0x1
# clear previous reset reason
devmem 0x80000074 16 0x0
# Release the Soc
currentVal=$(devmem 0xD100011A 8)
currentVal=$(( currentVal | 1 << 3 ))
devmem 0xD100011A 8 "0x""${currentVal}"
