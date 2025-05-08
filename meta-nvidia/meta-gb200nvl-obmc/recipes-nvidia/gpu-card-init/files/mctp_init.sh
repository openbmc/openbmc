#!/bin/bash

wait_for_mctp_usb() {
    echo "Waiting for mctpusb0 device..."
    while true; do
        if mctp link | grep -q "dev mctpusb0"; then
            echo "mctpusb0 device found"
            return 0
        fi
        sleep 0.1
    done
}

## Prep MCTPD
wait_for_mctp_usb
# 8 is the local/BMC EID
mctp addr add 8 dev mctpusb0
mctp link set mctpusb0 up

echo "MCTP init complete"
