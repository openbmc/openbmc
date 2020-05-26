#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin
RDEV=""
ROOT_DIR="/new_root"

mkdir -p /proc
mkdir -p /sys
mkdir -p /run
mkdir -p /tmp
mount -t proc proc /proc
mount -t sysfs sysfs /sys
mount -t devtmpfs none /dev

udevd --daemon
udevadm trigger --type=subsystems --action=add
udevadm trigger --type=devices --action=add
udevadm settle --timeout=10

for PARAM in $(cat /proc/cmdline); do
	case $PARAM in
		root=*)
			RDEV=${PARAM#root=}
			;;
	esac
done

if ! [ -b $RDEV ]; then
	echo "Missing root command line argument!"
	exit 1
fi

case $RDEV in
	UUID=*)
		RDEV=$(realpath /dev/disk/by-uuid/${RDEV#UUID=})
		;;
esac

. /usr/share/dm-verity.env

echo "Mounting $RDEV over dm-verity as the root filesystem"

veritysetup --data-block-size=1024 --hash-offset=$DATA_SIZE create rootfs $RDEV $RDEV $ROOT_HASH
mkdir -p $ROOT_DIR
mount -o ro /dev/mapper/rootfs $ROOT_DIR
exec switch_root $ROOT_DIR /sbin/init
