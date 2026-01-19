#!/bin/sh

DEVNAME="${2}" # Get device name
KERNEL_NUMBER=${5} # Get unique kernel number 
DEVPATH="/sys${DEVPATH}"
USB_GADGET_DEV=/dev/usb-ffs
USB_GADGET_OBMC_HID=/sys/kernel/config/usb_gadget/obmc_hid
USB_GADGET_OBMC_HID_CONF=$USB_GADGET_OBMC_HID/configs/c.1
USB_GADGET_OBMC_HID_FUNC=$USB_GADGET_OBMC_HID/functions
USB_GADGET_ADB=adb
USB_GADGET_BCDUSB=0x0200
USB_GADGET_BCDDEV=0x0233
USB_GADGET_VID=0x0e8d
USB_GADGET_PID=0x2005
USB_GADGET_SN=0123456789
USB_GADGET_MANUFACT="Axiado Corp."
USB_GADGET_PRODUCT="AIOT"
REPORT_DESCRIPTOR=${DEVPATH}/report_descriptor # Get report descriptor from system Dev path of HID devices

# Get the HID protocol and subclass information
PROTOCOL=$(cat ${DEVPATH}/../bInterfaceProtocol)
SUBCLASS=$(cat ${DEVPATH}/$DEVICE/../bInterfaceSubClass)

KERNEL_NAME="$3" # Get kernel name
UDC_PATH="/sys/class/udc"
udc_0=$(ls -1 "$UDC_PATH" | head -n 1)

# Get current power state via D-Bus
power_state=$(busctl get-property xyz.openbmc_project.State.Chassis \
    /xyz/openbmc_project/state/chassis0 \
    xyz.openbmc_project.State.Chassis \
    CurrentPowerState | awk '{print $2}' | tr -d '"')

# Create Mass storage folder to configfs path
create_gadget_mass_storage_device()
{
	mkdir $USB_GADGET_OBMC_HID_FUNC/mass_storage.ms$KERNEL_NAME
}

# Set Mass Storage device information to config files
set_gadget_mass_storage_device()
{
	echo "Configfs Gadget mass called" > /dev/console
	cd $USB_GADGET_OBMC_HID

	echo "$USB_GADGET_OBMC_HID/functions/mass_storage.$KERNEL_NAME $USB_GADGET_OBMC_HID/configs/c.1" > /dev/console
	echo $USB_GADGET_ADB > configs/c.1/strings/0x409/configuration
	ln -s $USB_GADGET_OBMC_HID/functions/mass_storage.ms$KERNEL_NAME $USB_GADGET_OBMC_HID/configs/c.1
	echo "link after" > /dev/console
	# have device node to lun file link to the host
 	echo "/dev/$KERNEL_NAME" > /dev/console
	if [[ "$power_state" == *".PowerState.On" ]]; then # monitor host system power on
		echo "/dev/$KERNEL_NAME" > $USB_GADGET_OBMC_HID/functions/mass_storage.ms$KERNEL_NAME/lun.0/file
		echo 1 > $USB_GADGET_OBMC_HID/functions/mass_storage.ms$KERNEL_NAME/lun.0/removable
	fi
	cd -
}

# Create HID device folder to configfs path, and create device folder to /dev
create_gadget_hid_devices()
{
        mkdir $USB_GADGET_OBMC_HID_FUNC/ffs.adb
        mkdir $USB_GADGET_OBMC_HID_FUNC/hid.usb$KERNEL_NAME
        FILENAME="hid.usb.$KERNEL_NAME"
        echo "$FILENAME" > /dev/console
        mkdir $USB_GADGET_DEV -m 770
        mkdir $USB_GADGET_DEV/adb -m 770
        mkdir $USB_GADGET_DEV/hid -m 770
}

# Set HID Device information to configfs files
set_gadget_hid_devices()
{
	cd $USB_GADGET_OBMC_HID
        echo ${PROTOCOL} > functions/hid.usb$KERNEL_NAME/protocol
        echo ${SUBCLASS} > functions/hid.usb$KERNEL_NAME/subclass
        echo 8 > functions/hid.usb$KERNEL_NAME/report_length
        cat ${REPORT_DESCRIPTOR} > functions/hid.usb$KERNEL_NAME/report_desc
        echo $USB_GADGET_ADB > configs/c.1/strings/0x409/configuration
        ln -s functions/hid.usb$KERNEL_NAME configs/c.1

        mount -o uid=2000,gid=2000 -t functionfs adb /dev/usb-ffs/adb
        mount -o uid=2000,gid=2000 -t functionfs hid.usb$KERNEL_NAME /dev/hid/hid$KERNEL_NAME
        cd -
}

# If physical keyboard mouse connected, need to give allocation hidg0 and hidg1 to virtual keyboard and mouse
# to connect ikvm first on boot
ikvm_UDC=/sys/kernel/config/usb_gadget/obmc_hid/UDC
if [ ! -f $ikvm_UDC ]; then
	/usr/bin/create_usbhid.sh connect
	sleep 3 # we need this sleep until /sys/kernel/config/usb_gadget/obmc_hid is mounted
fi

# When Device detects, start function called via udev rules 
start()
{
	echo "" > $USB_GADGET_OBMC_HID/UDC
	if [ "$hid" = "set" ]; then
		create_gadget_hid_devices
		set_gadget_hid_devices
	else
		create_gadget_mass_storage_device
		set_gadget_mass_storage_device
	fi

	start-stop-daemon --start --background --oknodo --quiet --exec /usr/bin/adbd
	sleep 1
	#Bind UDC
	if [[ "$power_state" == *".PowerState.On" ]]; then #checking host system power on to bind the UDC
		# Check if udc found
		if [ -z "$udc_0" ]; then
			echo "No UDC found!" > /dev/console
			exit 1
		fi
		echo "$udc_0" > $USB_GADGET_OBMC_HID/UDC #Port Name USB2_2 (USB2.0_P2 as per schametic)
		echo "UDC binded" > /dev/console
	elif ! [ -f /etc/usb/USB_dev_connect ]; then #create a file to connect USB after host power on
		echo "Nvidia power down while start" > /dev/console
		touch /etc/usb/USB_dev_connect
	fi
}

# When Device removed, stop function called via udev rules 
stop()
{
	echo "stop executing"  > /dev/console
	start-stop-daemon --stop --oknodo --quiet --exec /usr/bin/adbd
	echo "" > $USB_GADGET_OBMC_HID/UDC
	
	if [ "$hid" = "set" ]; then
		FILENAME=hid.usb$KERNEL_NAME
		echo $FILENAME > /dev/console
		if [ -n "$USB_GADGET_OBMC_HID_CONF/$FILENAME" ]; then
			rm "$USB_GADGET_OBMC_HID_CONF/$FILENAME"
			rmdir "$USB_GADGET_OBMC_HID_FUNC/$FILENAME"
    		else
       	 		echo "File not found for device $DEVICE_PATH"
    		fi
    	else
    		FILENAME=mass_storage.ms$KERNEL_NAME
		echo $FILENAME > /dev/console
		if [ -n "$USB_GADGET_OBMC_HID_CONF/$FILENAME" ]; then
			rm "$USB_GADGET_OBMC_HID_CONF/$FILENAME"
			rmdir "$USB_GADGET_OBMC_HID_FUNC/$FILENAME"
    		else
       	 		echo "File not found for device $DEVICE_PATH"
    		fi
    	fi
	if [[ "$power_state" == *".PowerState.On" ]]; then #checking host system power on to bind the UDC
		# Check if udc found
		if [ -z "$udc_0" ]; then
			echo "No UDC found!" > /dev/console
			exit 1
		fi
		echo "$udc_0" > $USB_GADGET_OBMC_HID/UDC #Port Name USB2_2 (USB2.0_P2 as per schametic)
		echo "UDC binded" > /dev/console
	elif ! [ -f /etc/usb/USB_dev_connect ]; then #create a file to connect USB after host power on
		echo "Nvidia power down while stop" > /dev/console
		touch /etc/usb/USB_dev_connect
	fi
}

# Identify HID device check
if [ "$4" = "hid" ]; then
	hid=set
fi

if [ "$1" = "--start" ]; then
	echo "START CALLED" > /dev/console
	start
elif [ "$1" = "--stop" ]; then
	echo "STOP CALLED" > /dev/console
	stop
fi
