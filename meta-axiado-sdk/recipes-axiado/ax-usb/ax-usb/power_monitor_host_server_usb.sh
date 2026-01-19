#!/bin/sh

obmc_hid_UDC_file="/sys/kernel/config/usb_gadget/obmc_hid/UDC"
obmc_hid_function_dir="/sys/kernel/config/usb_gadget/obmc_hid/functions"
USB_device_connect="/etc/usb/USB_dev_connect"
USB_device_reconnect="/etc/usb/USB_dev_reconnect"
#these files created by bmc-common that used for monitoring host boot monitoring by powerctrl
cpu_boot_timeout="/run/bmc-state/CPU_BOOT_TIMEOUT"
cpu_boot_complete="/run/bmc-state/CPU_BOOT_COMPLETE"
UDC_PATH="/sys/class/udc"
udc_0=$(ls -1 "$UDC_PATH" | head -n 1)

retry_count=0
systemd-notify --ready --status="Entering USB power monitor Loop"

while true;
do
    # Get current power state via D-Bus
    power_state=$(busctl get-property xyz.openbmc_project.State.Chassis \
        /xyz/openbmc_project/state/chassis0 \
        xyz.openbmc_project.State.Chassis \
        CurrentPowerState | awk '{print $2}' | tr -d '"')
	sleep 5
	if [[ "$power_state" == *".PowerState.On" ]]; then # host system power monitoring
		echo "Power ON Nvidia USB"
		if [ -f $USB_device_connect ]; then #monitoring the if USB devices already connected.
			echo "USB dev connect on boot"
			rm $USB_device_connect
			# Check if udc found
			if [ -z "$udc_0" ]; then
				echo "No UDC found!"
				exit 1
			fi
			massdev=`cd /dev;ls sd* | grep -i "sd"`
			for kname in $massdev
			do
				echo "mass storage kernel name: $kname"
				echo "/dev/$kname" > /sys/kernel/config/usb_gadget/obmc_hid/functions/mass_storage.ms$kname/lun.0/file
				echo 1 > /sys/kernel/config/usb_gadget/obmc_hid/functions/mass_storage.ms$kname/lun.0/removable
			done
			sleep 1
			echo "$udc_0" > $obmc_hid_UDC_file
			echo "UDC node is binded"
		fi
		if [ -d $obmc_hid_function_dir ]; then #if function directory exist
			#check if hid and mass storage already configured and existed in function directory
			if [ "$(ls -A $obmc_hid_function_dir/hid.usb*)" -o "$(ls -A $obmc_hid_function_dir/mass*)" ]; then
				if [ -z "$(cat ${obmc_hid_UDC_file})" ]; then #check if UDC node binded or not
					if [ -z "$udc_0" ]; then
						echo "No UDC found!"
						exit 1
					fi
					massdev=`cd /dev;ls sd* | grep -i "sd"`
					for kname in $massdev
					do
						echo "mass storage kernel name: $kname"
						lundir="/sys/kernel/config/usb_gadget/obmc_hid/functions/mass_storage.ms$kname/lun.0"
						if [ -z "$(cat ${lundir}/file)" ]; then
							echo "mass storage linked $lundir"
							echo "/dev/$kname" > $lundir/file
							echo 1 > $lundir/removable
						fi
					done
					sleep 1
					if [ -f $USB_device_reconnect ]; then #monitoring the if USB devices already connected.
						if [ -f $cpu_boot_timeout  -o -f $cpu_boot_complete ]; then #monitoring host boot process
							rm $USB_device_reconnect
							retry_count=0
							echo "$udc_0" > $obmc_hid_UDC_file
							echo "UDC node is binded"
						elif [ $retry_count -gt 5 ]; then
							rm $USB_device_reconnect
							echo "$cpu_boot_timeout or $cpu_boot_complete is not created"
							retry_count=0
						fi
						retry_count=$((retry_count+1))
						echo "Retry count $retry_count"
					else
						echo "$udc_0" > $obmc_hid_UDC_file
						echo "UDC node is binded only"
					fi
				fi
			fi
		fi
        if [ ! -z "$(cat ${obmc_hid_UDC_file})" ]; then
            echo "UDC node already binded"
        else
            sleep 1
			# Check if udc found
			if [ -z "$udc_0" ]; then
				echo "No UDC found!"
				exit 1
			fi
			echo "$udc_0" > $obmc_hid_UDC_file
			echo "UDC node connect"
        fi
	else
		echo "Host System Power down"
		if [ ! -z "$(cat ${obmc_hid_UDC_file})" ]; then #check if UDC binded then unbind it
			echo "" > $obmc_hid_UDC_file #Port Name USB2_2 (USB2.0_P2 as per schametic)
			echo "UDC node is unbinded only"
			if [ "$(ls -A $obmc_hid_function_dir/hid.usb*)" -o "$(ls -A $obmc_hid_function_dir/mass*)" ]; then
					touch $USB_device_reconnect
					echo "USB devices reconnected"
					retry_count=0
			fi
		fi
	fi
done
