#!/bin/sh
# File which is managing GPIOS when detected. First parameter is which GPIOs that switched
if [ "$1" = "up" ] 
then
	busctl set-property xyz.openbmc_project.Chassis.Gpios /xyz/openbmc_project/chassis/gpios xyz.openbmc_project.Chassis.Gpios PGood b true
	devmem 0x80fc0230 8 0x1
	sleep 2
	devmem 0x80fc0230 8 0x1
	# Activate local UBM (front NVME drive)

	devmem 0xd1000087 8 1
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 2
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 3
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 4
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000088 8 5
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 6
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 7
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 8
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 9
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 0xa
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 0xb
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 0xc
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 0xd
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 0xe
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000087 8 0xf
	i2ctransfer -y 3 w4@0x40 0x34 0xbf 0x00 0xe8

	devmem 0xd1000088 8 3
	i2ctransfer -y 4 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd1000088 8 4
	i2ctransfer -y 4 w4@0x40 0x34 0xbf 0x00 0xe8

	devmem 0xd100008a 8 0x13
	i2ctransfer -y 6 w4@0x40 0x34 0xbf 0x00 0xe8
	devmem 0xd100008a 8 0x14
	i2ctransfer -y 6 w4@0x40 0x34 0xbf 0x00 0xe8

#	systemctl restart xyz.openbmc_project.EntityManager.service
#	systemctl restart phosphor-pid-control.service
else
	if [ "$1" = "down" ] 
	then
		busctl set-property xyz.openbmc_project.Chassis.Gpios /xyz/openbmc_project/chassis/gpios xyz.openbmc_project.Chassis.Gpios PGood b false
		# We can re-init the system
		devmem 0xd1000009 8 0x24
		# We need to check the reason why we have been shutdown
		shutdownReason=$(devmem 0x80000074 16)
		# If bit 10 is set Software initiated a shutdown we need to restart
		isSet2=$(( shutdownReason | 0xFBFF ))
		if [ "$isSet2" = "65535" ] 
		then
			echo "Restarting" >> /tmp/gpios
			dbus-send --system --dest=xyz.openbmc_project.State.Host --print-reply --type=method_call /xyz/openbmc_project/state/host0 org.freedesktop.DBus.Properties.Set string:RequestedHostTransition variant:string:xyz.openbmc_project.State.Host.Transition.On
			obmcutil poweron
		fi
	fi
fi
echo "$1" >> /tmp/gpios.txt
