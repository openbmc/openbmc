#! /bin/bash
set -e

cd /sys/kernel/config/usb_gadget || exit 1
mkdir -p host_ethernet_gadget
cd host_ethernet_gadget || exit 1

echo 0x1d6b > idVendor
echo 0x0104 > idProduct
echo 0x0100 > bcdDevice
echo 0x0200 > bcdUSB

mkdir -p strings/0x409
echo "OpenBMC" > strings/0x409/manufacturer
echo "OpenBMC ECM Ethernet" > strings/0x409/product

mkdir -p configs/c.1/strings/0x409
echo "Config 1: ECM network" > configs/c.1/strings/0x409/configuration
echo 250 > configs/c.1/MaxPower

mkdir functions/ecm.usb0
echo "02:B0:2D:00:00:00" > functions/ecm.usb0/dev_addr
echo "06:B0:2D:00:00:01" > functions/ecm.usb0/host_addr
ln -s functions/ecm.usb0 configs/c.1/

echo 1e6a0000.usb-vhub:p2 > UDC

# IP configuration is now handled by systemd-networkd via 00-bmc-usb0.network
