#!/bin/sh
USB_GADGET=/sys/kernel/config/usb_gadget/obmc_hid
USB_GADGET_FUNC=$USB_GADGET/functions
USB_GADGET_CFG=$USB_GADGET/configs/c.1
USB_RNDIS_HOST_ADDR="de:ad:be:ef:00:01"
USB_RNDIS_DEV_ADDR="de:ad:be:ef:00:02"
USB_RNDIS_NAME="rndis.hostusb0"
USB_HID0_NAME="hid.0"
USB_HID1_NAME="hid.1"
USB_HID2_NAME="hid.2"

# Wait until the USB gadget configuration directory is created
while [ ! -d $USB_GADGET_CFG ]; do
    echo "Waiting for configfs gadget to be set up..."
    sleep 1
done

# Create and attach RNDIS function to USB gadget
if [ ! -d $USB_GADGET_FUNC/$USB_RNDIS_NAME ]; then
    mkdir $USB_GADGET_FUNC/$USB_RNDIS_NAME
    # Set MAC address for the host side of the RNDIS function
    echo $USB_RNDIS_HOST_ADDR > $USB_GADGET_FUNC/$USB_RNDIS_NAME/host_addr
    # Set MAC address for the device side of the RNDIS function
    echo $USB_RNDIS_DEV_ADDR > $USB_GADGET_FUNC/$USB_RNDIS_NAME/dev_addr

    # Create hid.2 (second keyboard)
    if [ ! -d $USB_GADGET_FUNC/$USB_HID2_NAME ]; then
        mkdir $USB_GADGET_FUNC/$USB_HID2_NAME
        echo 1 > $USB_GADGET_FUNC/$USB_HID2_NAME/protocol       # keyboard
        echo 8 > $USB_GADGET_FUNC/$USB_HID2_NAME/report_length
        echo 1 > $USB_GADGET_FUNC/$USB_HID2_NAME/subclass
        printf '\x05\x01\x09\x06\xa1\x01\x05\x07\x19\xe0\x29\xe7\x15\x00\x25\x01\x75\x01\x95\x08\x81\x02\x95\x01\x75\x08\x81\x03\x95\x05\x75\x01\x05\x08\x19\x01\x29\x05\x91\x02\x95\x01\x75\x03\x91\x03\x95\x06\x75\x08\x15\x00\x25\x65\x05\x07\x19\x00\x29\x65\x81\x00\xc0' > $USB_GADGET_FUNC/$USB_HID2_NAME/report_desc
    fi

    # Remove hid.0 and hid.1
    if [ -L "$USB_GADGET_CFG/$USB_HID0_NAME" ]; then
        rm "$USB_GADGET_CFG/$USB_HID0_NAME"
    fi
    if [ -L "$USB_GADGET_CFG/$USB_HID1_NAME" ]; then
        rm "$USB_GADGET_CFG/$USB_HID1_NAME"
    fi
    if [ -L "$USB_GADGET_CFG/$USB_HID2_NAME" ]; then
        rm "$USB_GADGET_CFG/$USB_HID2_NAME"
    fi

    # Link the RNDIS function to c.1, RNDIS must be the first one
    ln -s $USB_GADGET_FUNC/$USB_RNDIS_NAME $USB_GADGET_CFG
    ln -s $USB_GADGET_FUNC/$USB_HID0_NAME $USB_GADGET_CFG
    ln -s $USB_GADGET_FUNC/$USB_HID1_NAME $USB_GADGET_CFG
    ln -s $USB_GADGET_FUNC/$USB_HID2_NAME $USB_GADGET_CFG
fi
