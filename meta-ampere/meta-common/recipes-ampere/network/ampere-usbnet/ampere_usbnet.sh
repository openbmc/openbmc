#!/bin/bash

# Get MAC Address from uboot, or fallback to a random
ENV_MAC_ADDR=
ENV_MAC_ADDR=${ENV_MAC_ADDR:-"$(fw_printenv -n ethaddr)"}
ENV_MAC_ADDR=${ENV_MAC_ADDR:-"$(fw_printenv -n eth1addr)"}
MAC_ADDR=${ENV_MAC_ADDR:-"F8:C2:49:A6:09:3B"}

# Generate MAC Address using locally administered MAC
# https://en.wikipedia.org/wiki/MAC_address#Universal_vs._local_(U/L_bit
SUBMAC=$(echo "$MAC_ADDR" | cut -d ":" -f 2-5)
/usr/bin/usb-ctrl ecm usbnet off
/usr/bin/usb-ctrl ecm usbnet on "06:$SUBMAC:01" "06:$SUBMAC:00"

# Use NCM (Ethernet) Gadget instead of FunctionFS Gadget
echo 0x0103 > /sys/kernel/config/usb_gadget/usbnet/idProduct
echo "OpenBMC usbnet Device" > /sys/kernel/config/usb_gadget/usbnet/strings/0x409/product

if [ "$MAC_ADDR" != "$ENV_MAC_ADDR" ]; then
	# fail and wait for systemd to restart this service
	exit 1
fi
