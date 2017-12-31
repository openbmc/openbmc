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

if [ $CPU_COUNT -gt 1 ]; then
	vrm-control.sh vdna=1.0 vdnb=1.0
else
	vrm-control.sh vdna=1.0
fi
