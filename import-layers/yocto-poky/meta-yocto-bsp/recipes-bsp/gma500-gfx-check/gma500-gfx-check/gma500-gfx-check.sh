#!/bin/sh

# Check for devices we wish to avoid gma500_gfx for
DEVICES="0x8119 0x4108"

# Checked flag to avoid infinite modprobe
echo "gma500_gfx_checked" >> /dev/kmsg;

for DEVICE in $DEVICES; do
    if udevadm trigger --subsystem-match=pci --verbose --attr-match=device=$DEVICE | grep "pci" >> /dev/null ; then
        echo "Found $DEVICE, avoiding gma500_gfx module" >> /dev/kmsg;
        exit 0
    fi
done
exit 1
