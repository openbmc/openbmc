
#!/bin/sh
#
# Script to burn entire 32MB of BMC SPI Flash 1
#
# Usage:
#    a) Local: /run/initramfs/recovery <bmc-image-file>
#
# Assumptions:
#    <bmc-image-file> is a 32MB file representing all partitions in BMC SPI Flash
#    <bmc-image-file> exists on local filesystem
#    /dev/mtd0 represents entire BMC SPI Flash device
#    /dev/mtd5 is a JFFS2 filesystem (rwfs) partition
#    /bsp/reset/bmc_upgrade is symlink pointing to proper sticky bit in CPLD
#

if [ -f $1 ]
then
   echo $0: Update BMC SPI Flash with $1
else
   echo $0: File $1 not found on target, exiting
   exit
fi

echo $0: Remounting rwfs "(/dev/mtd5)" as read-only
mount /dev/mtdblock5 /run/initramfs/rw -t jffs2 -o remount,ro

echo $0: Unmounting rofs "(/dev/mtd4)"
umount /dev/mtdblock4

devmem 0x1e785034 l 0x01;

MAC=`fw_printenv ethaddr | sed -n "s/^ethaddr=//p"`

echo $0: Burning SPI Flash "(/dev/mtd0)" with image "$1"
/usr/sbin/flashcp -v $1 /dev/mtd0

if [ -v $MAC ]; then
	echo "MAC env variable not exist. Set eth0 MAC from eeprom."
	MAC=`hexdump -n 6 -s 0xf0 -v -e '/1 "%02x:"' /sys/bus/i2c/devices/6-0055/eeprom`;MAC=${MAC::-1};
else
	echo "MAC env variable exist. Set eth0 MAC from env."
fi;

fw_setenv ethaddr $MAC

echo $0: Setting bmc_upgrade sticky bit in CPLD
echo 1 > /bsp/reset/bmc_upgrade

echo $0: Rebooting BMC
echo 0 > /bsp/reset/bmc_reset_soft

