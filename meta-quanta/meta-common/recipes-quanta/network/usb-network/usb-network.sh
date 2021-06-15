#!/bin/bash

mac_config="/usr/share/mac-address/config.txt"
dev_mac_path="/tmp/usb0_dev"
host_mac_path="/tmp/usb0_host"

check_usb_local_administered() {
    is_enable="$(cat ${mac_config} | grep 'USBLAA')"
    echo ${is_enable}
}

# Set the locally administered bit (the second least-significant
# bit of the first octet) of the MAC address
set_local_administered_bit() {
    mac="$(cat $1)"
    first_byte="${mac:0:2}"
    first_byte="$((0x$first_byte|2))"
    first_byte="$(printf "%02x\n" "$first_byte")"
    mac="${first_byte}${mac:2}"
    echo $mac
}

cd /sys/kernel/config/usb_gadget

if [ ! -f "g1" ]; then
    mkdir g1
    cd g1

    echo 0x1d6b > idVendor  # Linux foundation
    echo 0x0104 > idProduct # Multifunction composite gadget
    mkdir -p strings/0x409
    echo "Linux" > strings/0x409/manufacturer
    echo "Etherned/ECM gadget" > strings/0x409/product

    mkdir -p configs/c.1
    echo 100 > configs/c.1/MaxPower
    mkdir -p configs/c.1/strings/0x409
    echo "ECM" > configs/c.1/strings/0x409/configuration


    if [[ $(check_usb_local_administered) == "USBLAA=true" ]]; then
        dev_mac="$(set_local_administered_bit $dev_mac_path)"
        host_mac="$(set_local_administered_bit $host_mac_path)"
        echo $dev_mac > $dev_mac_path
        echo $host_mac > $host_mac_path
    fi

    mkdir -p functions/ecm.usb0
    cat $dev_mac_path > functions/ecm.usb0/dev_addr # write device mac address
    cat $host_mac_path > functions/ecm.usb0/host_addr # write usb mac address

    ln -s functions/ecm.usb0 configs/c.1

    echo "$UDC" > UDC

    rm $dev_mac_path
    rm $host_mac_path

fi

exit 0
