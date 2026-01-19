#!/bin/bash

# TCU Shutdown vs Warm Reboot

SYSFS_GET="/sys/class/axiado_syscon/axiado_syscon0/get_value"
EXPECTED_VALUE="0xF001"

# Read 32-bits value that includes \n or \r or whatever emitted by sysfs
RAW_VALUE=$(cat "$SYSFS_GET" 2>/dev/null)

if [ -z "$RAW_VALUE" ]; then
    echo "Error: Failed to read from $SYSFS_GET"
    exit 1
fi

#echo "Raw value = '$RAW_VALUE' (length: ${#RAW_VALUE})"

# Clean the extra characters and only keep Hex characters
CURRENT_VALUE=$(echo "$RAW_VALUE" | sed -e 's/^[ \t\r\n]*//' -e 's/[ \t\r\n]*$//' -e 's/[^0-9A-Fa-fx.]//g' | tr 'A-F' 'a-f')
if [ -z "$CURRENT_VALUE" ]; then
    echo "Error: Cleaned value is empty"
    exit 1
fi

#echo "Debug: Cleaned CURRENT_VALUE = '$CURRENT_VALUE' (length: ${#CURRENT_VALUE})"

# Validate the hex value
if [[ ! "$CURRENT_VALUE" =~ ^0x[0-9A-Fa-f]+$ ]]; then
    echo "Error: Invalid Hex read from $SYSFS_GET value $CURRENT_VALUE"
    exit 1
fi

# Extract LSB 16-bits as hex string
# Remove 0x
HEX_STR="${CURRENT_VALUE#0x}"
# Only use last 4 digit (LSB 16-bits)
HEX_STR="${HEX_STR: -4}"
# Pad to 4 digits with 0s
PAD_HEX=$(printf "%04s" "$HEX_STR" | tr ' ' '0')
# Add 0x and convert to lower case for easy comparison
FINAL_HEX="0x${PAD_HEX,,}"

# Compare
if [ "${FINAL_HEX,,}" == "${EXPECTED_VALUE,,}" ]; then
    echo "TCU Shutdown Request"
else
    echo "Linux Warm Reboot/Cold Reset Request"
    #For TCU cold Reset
    /usr/bin/ax3000-fw-update -W
fi

exit 0 