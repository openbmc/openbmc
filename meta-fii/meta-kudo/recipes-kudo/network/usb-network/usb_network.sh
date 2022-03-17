#!/bin/bash

cd /sys/kernel/config/usb_gadget || exit 1

if [ ! -f "g1" ]; then
    mkdir g1
    cd g1 || exit 1

    echo 0x1d6b > idVendor  # Linux foundation
    echo 0x0104 > idProduct # Multifunction composite gadget
    mkdir -p strings/0x409
    echo "Linux" > strings/0x409/manufacturer
    echo "Ethernet/ECM gadget" > strings/0x409/product

    mkdir -p configs/c.1
    echo 100 > configs/c.1/MaxPower
    mkdir -p configs/c.1/strings/0x409
    echo "ECM" > configs/c.1/strings/0x409/configuration

    mkdir -p functions/ecm.usb0

    ln -s functions/ecm.usb0 configs/c.1

    echo f0835000.udc > UDC

fi
exit 0
