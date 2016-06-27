#!/bin/sh -e
#
# Copyright (c) 2012, Intel Corporation.
# All rights reserved.
#
# install.sh [device_name] [rootfs_name]
#

PATH=/sbin:/bin:/usr/sbin:/usr/bin

# We need 200 Mb for the boot partition
boot_size=200

# 50% for the second rootfs
testfs_ratio=50

found="no"

echo "Searching for a hard drive..."
for device in 'hda' 'hdb' 'sda' 'sdb' 'mmcblk0' 'mmcblk1'
do
    if [ -e /sys/block/${device}/removable ]; then
        if [ "$(cat /sys/block/${device}/removable)" = "0" ]; then
            found="yes"

            while true; do
                # Try sleeping here to avoid getting kernel messages
                # obscuring/confusing user
                sleep 5
                echo "Found drive at /dev/${device}. Do you want to install this image there ? [y/n]"
                read answer
                if [ "$answer" = "y" ] ; then
                    break
                fi

                if [ "$answer" = "n" ] ; then
                    found=no
                    break
                fi

                echo "Please answer y or n"
            done
        fi
    fi

    if [ "$found" = "yes" ]; then
        break;
    fi

done

if [ "$found" = "no" ]; then
    exit 1
fi

echo "Installing image on /dev/${device}"

#
# The udev automounter can cause pain here, kill it
#
rm -f /etc/udev/rules.d/automount.rules
rm -f /etc/udev/scripts/mount*

#
# Unmount anything the automounter had mounted
#
umount /dev/${device}* 2> /dev/null || /bin/true

mkdir -p /tmp
cat /proc/mounts > /etc/mtab

disk_size=$(parted /dev/${device} unit mb print | grep '^Disk .*: .*MB' | cut -d" " -f 3 | sed -e "s/MB//")

testfs_size=$((disk_size*testfs_ratio/100))
rootfs_size=$((disk_size-boot_size-testfs_size))

rootfs_start=$((boot_size))
rootfs_end=$((rootfs_start+rootfs_size))
testfs_start=$((rootfs_end))

# MMC devices are special in a couple of ways
# 1) they use a partition prefix character 'p'
# 2) they are detected asynchronously (need rootwait)
rootwait=""
part_prefix=""
if [ ! "${device#mmcblk}" = "${device}" ]; then
    part_prefix="p"
    rootwait="rootwait"
fi
bootfs=/dev/${device}${part_prefix}1
rootfs=/dev/${device}${part_prefix}2
testfs=/dev/${device}${part_prefix}3

echo "*****************"
echo "Boot partition size:   $boot_size MB ($bootfs)"
echo "Rootfs partition size: $rootfs_size MB ($rootfs)"
echo "Testfs partition size:   $testfs_size MB ($testfs)"
echo "*****************"
echo "Deleting partition table on /dev/${device} ..."
dd if=/dev/zero of=/dev/${device} bs=512 count=2

echo "Creating new partition table on /dev/${device} ..."
parted /dev/${device} mklabel gpt

echo "Creating boot partition on $bootfs"
parted /dev/${device} mkpart primary 0% $boot_size
parted /dev/${device} set 1 boot on

echo "Creating rootfs partition on $rootfs"
parted /dev/${device} mkpart primary $rootfs_start $rootfs_end

echo "Creating testfs partition on $testfs"
parted /dev/${device} mkpart primary $testfs_start 100%

parted /dev/${device} print

echo "Formatting $bootfs to vfat..."
mkfs.vfat -n "boot" $bootfs

echo "Formatting $rootfs to ext3..."
mkfs.ext3 -L "platform" $rootfs

echo "Formatting $testfs to ext3..."
mkfs.ext3 -L "testrootfs" $testfs

mkdir /ssd
mkdir /rootmnt
mkdir /bootmnt

mount $rootfs /ssd
mount -o rw,loop,noatime,nodiratime /run/media/$1/$2 /rootmnt

echo "Copying rootfs files..."
cp -a /rootmnt/* /ssd

touch /ssd/etc/masterimage

if [ -d /ssd/etc/ ] ; then
    # We dont want udev to mount our root device while we're booting...
    if [ -d /ssd/etc/udev/ ] ; then
        echo "/dev/${device}" >> /ssd/etc/udev/mount.blacklist
    fi
fi

umount /ssd
umount /rootmnt

echo "Preparing boot partition..."
mount $bootfs /ssd

EFIDIR="/ssd/EFI/BOOT"
mkdir -p $EFIDIR
cp /run/media/$1/vmlinuz /ssd
# Copy the efi loader
cp /run/media/$1/EFI/BOOT/*.efi $EFIDIR

if [ -f /run/media/$1/EFI/BOOT/grub.cfg ]; then
    GRUBCFG="$EFIDIR/grub.cfg"
    cp /run/media/$1/EFI/BOOT/grub.cfg $GRUBCFG
    # Update grub config for the installed image
    # Delete the install entry
    sed -i "/menuentry 'install'/,/^}/d" $GRUBCFG
    # Delete the initrd lines
    sed -i "/initrd /d" $GRUBCFG
    # Delete any LABEL= strings
    sed -i "s/ LABEL=[^ ]*/ /" $GRUBCFG
    # Delete any root= strings
    sed -i "s/ root=[^ ]*/ /" $GRUBCFG
    # Add the root= and other standard boot options
    sed -i "s@linux /vmlinuz *@linux /vmlinuz root=$rootfs rw $rootwait quiet @" $GRUBCFG
fi

if [ -d /run/media/$1/loader ]; then
    GUMMIBOOT_CFGS="/ssd/loader/entries/*.conf"
    # copy config files for gummiboot
    cp -dr /run/media/$1/loader /ssd
    # delete the install entry
    rm -f /ssd/loader/entries/install.conf
    # delete the initrd lines
    sed -i "/initrd /d" $GUMMIBOOT_CFGS
    # delete any LABEL= strings
    sed -i "s/ LABEL=[^ ]*/ /" $GUMMIBOOT_CFGS
    # delete any root= strings
    sed -i "s/ root=[^ ]*/ /" $GUMMIBOOT_CFGS
    # add the root= and other standard boot options
    sed -i "s@options *@options root=$rootfs rw $rootwait quiet @" $GUMMIBOOT_CFGS
    # Add the test label
    echo -ne "title test\nlinux /test-kernel\noptions root=$testfs rw $rootwait quiet\n" > /ssd/loader/entries/test.conf
fi

umount /ssd
sync

echo "Remove your installation media, and press ENTER"

read enter

echo "Rebooting..."
reboot -f
