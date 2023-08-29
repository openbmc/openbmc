#!/bin/sh

findmtd() {
  m=$(grep -xl "$1" /sys/class/mtd/*/name)
  m=${m%/name}
  m=${m##*/}
  echo "$m"
}

hostPrime=$(findmtd host-prime)
if test -z "$hostPrime"
then
  echo "Unable to find mtd partition for ${hostPrime}"
  exit 1
fi

hostSecond=$(findmtd host-second)
if test -z "$hostSecond"
then
  echo "Unable to find mtd partition for ${hostSecond}"
  exit 1
fi

vromPrime=$(findmtd vrom-prime)
if test -z "$vromPrime"
then
  echo "Unable to find mtd partition for ${vromPrime}"
  exit 1
fi

vromSecond=$(findmtd vrom-second)
if test -z "$vromSecond"
then
  echo "Unable to find mtd partition for ${vromSecond}"
  exit 1
fi


#enable vrom
# host-prime to vrom-prime
dd if=/dev/"${hostPrime}" of=/dev/"${vromPrime}"
# host-second to vrom-second
dd if=/dev/"${hostSecond}" of=/dev/"${vromSecond}"

# enable UART on rl300
systemctl start obmc-console@ttyS3.service

echo 0x1800008a > /sys/class/soc/srom/vromoff

devmem 0xd1000008 8 128
devmem 0xd1000009 8 36
devmem 0xd1000041 8 255
devmem 0xd100004b 8 8
# setup PCIe ID
devmem 0x802f002e 16 0x03d8
#enable debug vsp output
devmem 0x80fc0230 8 0x1

# Check the current power status

currentstate=$(busctl get-property xyz.openbmc_project.State.Chassis0 /xyz/openbmc_project/state/chassis0 xyz.openbmc_project.State.Chassis CurrentPowerState | awk '{ print $2 }')


if [ "$currentstate" = "\"xyz.openbmc_project.State.Chassis.PowerState.On\"" ]
then
	systemctl stop phosphor-virtual-sensor.service
	sleep 2
	devmem 0x80fc0230 8 0x1
	systemctl start phosphor-virtual-sensor.service
fi

while 'true'
do
	sleep 1
done