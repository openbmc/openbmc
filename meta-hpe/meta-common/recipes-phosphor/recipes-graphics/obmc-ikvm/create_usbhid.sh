#!/bin/sh

hid_conf_directory="/sys/kernel/config/usb_gadget/obmc_hid"
dev_name="80401000.udc"

create_hid() {
    # create gadget
    mkdir "${hid_conf_directory}"
    cd "${hid_conf_directory}" || exit 1

    # add basic information
    echo 0x0100 > bcdDevice
    echo 0x0200 > bcdUSB
    echo 0x0104 > idProduct		# Multifunction Composite Gadget
    echo 0x1d6b > idVendor		# Linux Foundation

    # create English locale
    mkdir strings/0x409

    echo "OpenBMC" > strings/0x409/manufacturer
    echo "virtual_input" > strings/0x409/product
    echo "OBMC0001" > strings/0x409/serialnumber

    # Create HID keyboard function
    mkdir functions/hid.0

    echo 1 > functions/hid.0/protocol	# 1: keyboard
    echo 8 > functions/hid.0/report_length
    echo 1 > functions/hid.0/subclass

    # Binary HID keyboard descriptor
    #  0x05, 0x01, // USAGE_PAGE (Generic Desktop)
    #  0x09, 0x06, // USAGE (Keyboard)
    #  0xa1, 0x01, // COLLECTION (Application)
    #  0x05, 0x07, //   USAGE_PAGE (Keyboard)
    #  0x19, 0xe0, //   USAGE_MINIMUM (Keyboard LeftControl)
    #  0x29, 0xe7, //   USAGE_MAXIMUM (Keyboard Right GUI)
    #  0x15, 0x00, //   LOGICAL_MINIMUM (0)
    #  0x25, 0x01, //   LOGICAL_MAXIMUM (1)
    #  0x75, 0x01, //   REPORT_SIZE (1)
    #  0x95, 0x08, //   REPORT_COUNT (8)
    #  0x81, 0x02, //   INPUT (Data,Var,Abs)
    #  0x95, 0x01, //   REPORT_COUNT (1)
    #  0x75, 0x08, //   REPORT_SIZE (8)
    #  0x81, 0x03, //   INPUT (Data,Var,Abs)
    #  0x95, 0x05, //   REPORT_COUNT (5)
    #  0x75, 0x01, //   REPORT_SIZE (1)
    #  0x05, 0x08, //   USAGE_PAGE (LEDs)
    #  0x19, 0x01, //   USAGE_MINIMUM (Num Lock)
    #  0x29, 0x05, //   USAGE_MAXIMUM (Kana)
    #  0x91, 0x02, //   OUTPUT (Data,Var,Abs)
    #  0x95, 0x01, //   REPORT_COUNT (1)
    #  0x75, 0x03, //   REPORT_SIZE (3)
    #  0x91, 0x03, //   OUTPUT (Cnst,Var,Abs)
    #  0x95, 0x06, //   REPORT_COUNT (6)
    #  0x75, 0x08, //   REPORT_SIZE (8)
    #  0x15, 0x00, //   LOGICAL_MINIMUM (0)
    #  0x25, 0x65, //   LOGICAL_MAXIMUM (101)
    #  0x05, 0x07, //   USAGE_PAGE (Keyboard)
    #  0x19, 0x00, //   USAGE_MINIMUM (Reserved (no event indicated))
    #  0x29, 0x65, //   USAGE_MAXIMUM (Keyboard Application)
    #  0x81, 0x00, //   INPUT (Data,Ary,Abs)
    #  0xc0        // END_COLLECTION
    printf '\x05\x01\x09\x06\xa1\x01\x05\x07\x19\xe0\x29\xe7\x15\x00\x25\x01\x75\x01\x95\x08\x81\x02\x95\x01\x75\x08\x81\x03\x95\x05\x75\x01\x05\x08\x19\x01\x29\x05\x91\x02\x95\x01\x75\x03\x91\x03\x95\x06\x75\x08\x15\x00\x25\x65\x05\x07\x19\x00\x29\x65\x81\x00\xc0' > functions/hid.0/report_desc

    # Create HID mouse function
    mkdir functions/hid.1

    echo 2 > functions/hid.1/protocol	# 2: mouse
    echo 6 > functions/hid.1/report_length
    echo 1 > functions/hid.1/subclass

    # Binary HID mouse descriptor (absolute coordinate)
    #  0x05, 0x01,       // USAGE_PAGE (Generic Desktop)
    #  0x09, 0x02,       // USAGE (Mouse)
    #  0xa1, 0x01,       // COLLECTION (Application)
    #  0x09, 0x01,       //   USAGE (Pointer)
    #  0xa1, 0x00,       //   COLLECTION (Physical)
    #  0x05, 0x09,       //     USAGE_PAGE (Button)
    #  0x19, 0x01,       //     USAGE_MINIMUM (Button 1)
    #  0x29, 0x03,       //     USAGE_MAXIMUM (Button 3)
    #  0x15, 0x00,       //     LOGICAL_MINIMUM (0)
    #  0x25, 0x01,       //     LOGICAL_MAXIMUM (1)
    #  0x95, 0x03,       //     REPORT_COUNT (3)
    #  0x75, 0x01,       //     REPORT_SIZE (1)
    #  0x81, 0x02,       //     INPUT (Data,Var,Abs)
    #  0x95, 0x01,       //     REPORT_COUNT (1)
    #  0x75, 0x05,       //     REPORT_SIZE (5)
    #  0x81, 0x03,       //     INPUT (Cnst,Var,Abs)
    #  0x05, 0x01,       //     USAGE_PAGE (Generic Desktop)
    #  0x09, 0x30,       //     USAGE (X)
    #  0x09, 0x31,       //     USAGE (Y)
    #  0x35, 0x00,       //     PHYSICAL_MINIMUM (0)
    #  0x46, 0xff, 0x7f, //     PHYSICAL_MAXIMUM (32767)
    #  0x15, 0x00,       //     LOGICAL_MINIMUM (0)
    #  0x26, 0xff, 0x7f, //     LOGICAL_MAXIMUM (32767)
    #  0x65, 0x11,       //     UNIT (SI Lin:Distance)
    #  0x55, 0x00,       //     UNIT_EXPONENT (0)
    #  0x75, 0x10,       //     REPORT_SIZE (16)
    #  0x95, 0x02,       //     REPORT_COUNT (2)
    #  0x81, 0x02,       //     INPUT (Data,Var,Abs)
    #  0x09, 0x38,       //     Usage (Wheel)
    #  0x15, 0xff,       //     LOGICAL_MINIMUM (-1)
    #  0x25, 0x01,       //     LOGICAL_MAXIMUM (1)
    #  0x35, 0x00,       //     PHYSICAL_MINIMUM (-127)
    #  0x45, 0x00,       //     PHYSICAL_MAXIMUM (127)
    #  0x75, 0x08,       //     REPORT_SIZE (8)
    #  0x95, 0x01,       //     REPORT_COUNT (1)
    #  0x81, 0x06,       //     INPUT (Data,Var,Rel)
    #  0xc0,             //   END_COLLECTION
    #  0xc0              // END_COLLECTION
    printf '\x05\x01\x09\x02\xa1\x01\x09\x01\xa1\x00\x05\x09\x19\x01\x29\x03\x15\x00\x25\x01\x95\x03\x75\x01\x81\x02\x95\x01\x75\x05\x81\x03\x05\x01\x09\x30\x09\x31\x35\x00\x46\xff\x7f\x15\x00\x26\xff\x7f\x65\x11\x55\x00\x75\x10\x95\x02\x81\x02\x09\x38\x15\xff\x25\x01\x35\x00\x45\x00\x75\x08\x95\x01\x81\x06\xc0\xc0' > functions/hid.1/report_desc

    # Create configuration
    mkdir configs/c.1
    mkdir configs/c.1/strings/0x409

    echo 0xe0 > configs/c.1/bmAttributes
    echo 200 > configs/c.1/MaxPower
    echo "" > configs/c.1/strings/0x409/configuration

    # Link HID functions to configuration
    ln -s functions/hid.0 configs/c.1
    ln -s functions/hid.1 configs/c.1
}

connect_hid() {
    if ! grep -q "${dev_name}" UDC; then
        echo "${dev_name}" > UDC
    fi
}

disconnect_hid() {
    if grep -q "${dev_name}" UDC; then
        echo "" > UDC
    fi
}

if [ ! -e "${hid_conf_directory}" ]; then
    create_hid
else
    cd "${hid_conf_directory}" || exit 1
fi

if [ "$1" = "connect" ]; then
    connect_hid
elif [ "$1" = "disconnect" ]; then
    disconnect_hid
else
    echo >&2 "Invalid option: $1. Use 'connect' or 'disconnect'."
    exit 1
fi
