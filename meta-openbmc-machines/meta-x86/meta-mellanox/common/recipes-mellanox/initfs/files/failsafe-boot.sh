#!/bin/sh

echo "System entered recovery mode. Running from backup flash!!!
Please scp image file to /tmp folder and re-burn main flash with:
/run/initramfs/recovery /tmp/<bmc-image-file>.
";

mkdir /run/lock;

HWMON=`ls /sys/devices/platform/ahb/ahb:apb/1e78a000.i2c/i2c-4/i2c-4/4-0071/hwmon/`;

/usr/bin/mlx-hw-events.sh add reset /sys/devices/platform/ahb/ahb:apb/1e78a000.i2c/i2c-4/i2c-4/4-0071/hwmon/$HWMON;

mount /dev/mtdblock7 /run/initramfs/rw -t jffs2 -o remount,ro;
mount tmpfs tmp -t tmpfs -o mode=755,nodev;

echo 1 > /bsp/reset/cpu_reset_hard;

IP=`fw_printenv ipaddr | sed -n "s/^ipaddr=//p"`;
NETMASK=`fw_printenv netmask | sed -n "s/^netmask=//p"`;
GATEWAY=`fw_printenv gatewayip | sed -n "s/^gatewayip=//p"`;

if [[ -n $IP && -n $NETMASK && -n $GATEWAY ]]; then
    ifconfig eth0 $IP netmask $NETMASK up;
    route add default gw $GATEWAY eth0;
else
    echo "No ipaddr, netmask or gatewayip env variable.";
fi;

source /etc/profile
export PS1="(recovery)\u@BMC:\w\$"

while true;
do
    sulogin;
done

