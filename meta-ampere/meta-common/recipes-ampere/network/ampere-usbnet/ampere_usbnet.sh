#!/bin/bash

ENV_MAC_ADDR=$(fw_printenv eth1addr)
if [ -z "$ENV_MAC_ADDR" ]; then
	ENV_MAC_ADDR=$(fw_printenv ethaddr)
fi

MAC_ADDR=$(echo "$ENV_MAC_ADDR" | cut -d "=" -f 2)

if [ -n "$MAC_ADDR" ]; then
	# Generate MAC Address from eth1addr using locally administered MAC
	# https://en.wikipedia.org/wiki/MAC_address#Universal_vs._local_(U/L_bit
	SUBMAC=$(echo "$MAC_ADDR" | cut -d ":" -f 2-6)
	/usr/bin/usb-ctrl ecm usbnet on "06:$SUBMAC" "02:$SUBMAC"
else
	/usr/bin/usb-ctrl ecm usbnet on
fi

# Use NCM (Ethernet) Gadget instead of FunctionFS Gadget
echo 0x0103 > /sys/kernel/config/usb_gadget/usbnet/idProduct
echo "OpenBMC usbnet Device" > /sys/kernel/config/usb_gadget/usbnet/strings/0x409/product
