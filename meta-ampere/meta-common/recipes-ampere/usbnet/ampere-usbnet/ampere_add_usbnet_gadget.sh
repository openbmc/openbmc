#!/bin/sh

# Add an Ethernet over USB gadget device and connect to a port of Aspeed USB
# virtual hub. If can't find any free port on virtual hub, exit with failure.
# If can't find the virtual hub, exit with failure.

# Author: Thinh Hung Pham <thinh.pham@amperecomputing.com>
# Signed-off-by: Chanh Nguyen <chnguyen@amperecomputing.com>

UDC_SYSPATH=/sys/class/udc
VHUB_DEVICE=1e6a0000.usb-vhub:p
GADGET_CONFIG_SYSPATH=/sys/kernel/config/usb_gadget
USBNET=usbnet
# The number of port on AST2500 USB virtual hub
NUM_PORT_USB_HUB=5
# idVendor = 0x1d6b: Linux Foundation
VENDORID=0x1d6b
# idProduct = 0x0103: NCM (Ethernet) Gadget
PRODUCTID=0x0103
# Language code = 0x409: English – United States
LANGUAGEID=0x409
SERIALNUMBER=cafecafe
MANUFACTURER=Aspeed
FUNCTION=ecm.usb0

if [ ! -d ${GADGET_CONFIG_SYSPATH} ]; then
	exit 1
fi

find_free_vhub_port(){
	for ((i=1;i<=${NUM_PORT_USB_HUB};i++))
	do
		state=$(cat ${UDC_SYSPATH}/${VHUB_DEVICE}${i}/state)
		if [ "${state}" == "not attached" ]; then
			FREEUDC=${VHUB_DEVICE}${i}
			break
		fi
	done
	if [ ${i} -eq 6 ]; then
		# Can't find a free port, exit
		exit 1
	fi
}

# Create the gadget
mkdir ${GADGET_CONFIG_SYSPATH}/${USBNET}
cd ${GADGET_CONFIG_SYSPATH}/${USBNET}

# Configure the gadget
echo ${VENDORID} > idVendor
echo ${PRODUCTID} > idProduct
mkdir strings/${LANGUAGEID}
echo ${SERIALNUMBER} > strings/${LANGUAGEID}/serialnumber
echo ${MANUFACTURER} > strings/${LANGUAGEID}/manufacturer
echo ${USBNET} > strings/${LANGUAGEID}/product

# Create the configuration
mkdir configs/c.1
mkdir configs/c.1/strings/${LANGUAGEID}

# Create the function
mkdir functions/${FUNCTION}

# Associate the function with its configuration
ln -s functions/${FUNCTION} configs/c.1

# Find an available virtual hub port
find_free_vhub_port

# Enable the gadget
echo ${FREEUDC} > UDC

# End
cd - > /dev/null
