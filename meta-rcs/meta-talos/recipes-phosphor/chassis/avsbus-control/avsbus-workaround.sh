#!/bin/sh

# Get CPU count
CPU_COUNT=1
STATUS_FLAGS=$(i2cget -y 12 0x31 0x7)
if [ $? != 0 ]; then
	STATUS_FLAGS=$(i2cget -y 12 0x31 0x7)
fi
if [ $? != 0 ]; then
	STATUS_FLAGS=$(i2cget -y 12 0x31 0x7)
fi
CPU_PRESENT_FLAG_N=$(( ${STATUS_FLAGS} & 0x20 ))
if [ $CPU_PRESENT_FLAG_N != 0 ]; then
	CPU_COUNT=$(( ${CPU_COUNT} + 1 ))
fi
echo "Found $CPU_COUNT CPU(s)"

i2cset -y 4 0x28 0x2E 0x23 b # VDD/VCS 0
i2cset -y 4 0x2B 0x2E 0x23 b # VDN 0
if [ $CPU_COUNT -gt 1 ]; then
	i2cset -y 5 0x28 0x2E 0x23 b # VDD/VCS 1
	i2cset -y 5 0x2B 0x2E 0x23 b # VDN 1
fi
