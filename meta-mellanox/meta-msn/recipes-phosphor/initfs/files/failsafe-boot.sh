#!/bin/sh

echo "----------------
System entered recovery mode. Running from backup flash!!!
help for get ip:
ifconfig eth0 $IP netmask $NETMASK up;
route add default gw $GATEWAY eth0
Or type udhcpc to get ip over dhcp.
Please scp image file to /tmp folder and re-burn main flash with:
/run/initramfs/recovery /tmp/<bmc-image-file>.
----------------";

mkdir /run/lock;

mkdir /dev/mtd;

ln -s /run/mtd\:u-boot-env /dev/mtd/u-boot-env;

/usr/bin/mlx-hw-events.sh add reset /sys/devices/platform/ahb/ahb:apb/ahb:apb:i2c@1e78a000/1e78a140.i2c-bus/i2c-4/4-0071/mlxreg-core.1137/hwmon/hwmon*;

mount /dev/mtdblock5 /run/initramfs/rw -t jffs2 -o remount,ro;
mount tmpfs tmp -t tmpfs -o mode=755,nodev;

export PS1="(recovery)\u@BMC:\w\$"

while true;
do
    /run/initramfs/ro/sbin/sulogin;
done

