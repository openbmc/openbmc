#!/bin/sh
#
# Copyright (c) 2012, Intel Corporation.
# All rights reserved.
#
# This program is free software;  you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY;  without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
# the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program;  if not, write to the Free Software
# Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#

LANG=C

echo
echo "WARNING: This script is deprecated and will be removed soon."
echo "Please consider using wic EFI images instead."
echo

# Set to 1 to enable additional output
DEBUG=0
OUT="/dev/null"

#
# Defaults
#
# 20 Mb for the boot partition
BOOT_SIZE=20
# 5% for swap
SWAP_RATIO=5

# Cleanup after die()
cleanup() {
	debug "Syncing and unmounting devices"
	# Unmount anything we mounted
	unmount $ROOTFS_MNT || error "Failed to unmount $ROOTFS_MNT"
	unmount $BOOTFS_MNT || error "Failed to unmount $BOOTFS_MNT"
	unmount $HDDIMG_ROOTFS_MNT || error "Failed to unmount $HDDIMG_ROOTFS_MNT"
	unmount $HDDIMG_MNT || error "Failed to unmount $HDDIMG_MNT"

	# Remove the TMPDIR
	debug "Removing temporary files"
	if [ -d "$TMPDIR" ]; then
		rm -rf $TMPDIR || error "Failed to remove $TMPDIR"
	fi
}

trap 'die "Signal Received, Aborting..."' HUP INT TERM

# Logging routines
WARNINGS=0
ERRORS=0
CLEAR="$(tput sgr0)"
INFO="$(tput bold)"
RED="$(tput setaf 1)$(tput bold)"
GREEN="$(tput setaf 2)$(tput bold)"
YELLOW="$(tput setaf 3)$(tput bold)"
info() {
	echo "${INFO}$1${CLEAR}"
}
error() {
	ERRORS=$((ERRORS+1))
	echo "${RED}$1${CLEAR}"
}
warn() {
	WARNINGS=$((WARNINGS+1))
	echo "${YELLOW}$1${CLEAR}"
}
success() {
	echo "${GREEN}$1${CLEAR}"
}
die() {
	error "$1"
	cleanup
	exit 1
}
debug() {
	if [ $DEBUG -eq 1 ]; then
		echo "$1"
	fi
}

usage() {
	echo "Usage: $(basename $0) [-v] DEVICE HDDIMG TARGET_DEVICE"
	echo "       -v: Verbose debug"
	echo "       DEVICE: The device to write the image to, e.g. /dev/sdh"
	echo "       HDDIMG: The hddimg file to generate the efi disk from"
	echo "       TARGET_DEVICE: The device the target will boot from, e.g.  /dev/mmcblk0"
}

image_details() {
	IMG=$1
	info "Image details"
	echo "    image: $(stat --printf '%N\n' $IMG)"
	echo "     size: $(stat -L --printf '%s bytes\n' $IMG)"
	echo " modified: $(stat -L --printf '%y\n' $IMG)"
	echo "     type: $(file -L -b $IMG)"
	echo ""
}

device_details() {
	DEV=$1
	BLOCK_SIZE=512

	info "Device details"
	echo "  device: $DEVICE"
	if [ -f "/sys/class/block/$DEV/device/vendor" ]; then
		echo "  vendor: $(cat /sys/class/block/$DEV/device/vendor)"
	else
		echo "  vendor: UNKOWN"
	fi
	if [ -f "/sys/class/block/$DEV/device/model" ]; then
		echo "   model: $(cat /sys/class/block/$DEV/device/model)"
	else
		echo "   model: UNKNOWN"
	fi
	if [ -f "/sys/class/block/$DEV/size" ]; then
		echo "    size: $(($(cat /sys/class/block/$DEV/size) * $BLOCK_SIZE)) bytes"
	else
		echo "    size: UNKNOWN"
	fi
	echo ""
}

unmount_device() {
	grep -q $DEVICE /proc/mounts
	if [ $? -eq 0 ]; then
		warn "$DEVICE listed in /proc/mounts, attempting to unmount"
		umount $DEVICE* 2>/dev/null
		return $?
	fi
	return 0
}

unmount() {
	if [ "$1" = "" ] ; then
		return 0
	fi
	grep -q $1 /proc/mounts
	if [ $? -eq 0 ]; then
		debug "Unmounting $1"
		umount $1
		return $?
	fi
	return 0
}

#
# Parse and validate arguments
#
if [ $# -lt 3 ] || [ $# -gt 4 ]; then
    if [ $# -eq 1 ]; then
        AVAILABLE_DISK=`lsblk | grep "disk" | cut -f 1 -d " "`
        X=0
        for disk in `echo $AVAILABLE_DISK`; do
            mounted=`lsblk /dev/$disk | awk {'print $7'} | sed "s/MOUNTPOINT//"`
            if [ -z "$mounted" ]; then
                UNMOUNTED_AVAILABLES="$UNMOUNTED_AVAILABLES /dev/$disk"
                info "$X - /dev/$disk"
                X=`expr $X + 1`
            fi
        done
        if [ $X -eq 0 ]; then
            die "No unmounted device found."
        fi
        read -p "Choose unmounted device number: " DISK_NUMBER
        X=0
        for line in `echo $UNMOUNTED_AVAILABLES`; do
            if [ $DISK_NUMBER -eq $X ]; then
                DISK_TO_BE_FLASHED=$line
                break
            else
                X=`expr $X + 1`
            fi
        done
        if [ -z "$DISK_TO_BE_FLASHED" ]; then
            die "Option \"$DISK_NUMBER\" is invalid. Choose a valid option"
        else
            if [ -z `echo $DISK_TO_BE_FLASHED | grep "mmc"` ]; then
                TARGET_TO_BE_BOOT="/dev/sda"
            else
                TARGET_TO_BE_BOOT="/dev/mmcblk0"
            fi
        fi
        echo ""
        echo "Choose a name of the device that will be boot from"
        echo -n "Recommended name is: "
        info "$TARGET_TO_BE_BOOT"
        read -p "Is target device okay? [y/N]: " RESPONSE
        if [ "$RESPONSE" != "y" ]; then
            read -p "Choose target device name: " TARGET_TO_BE_BOOT
        fi
        echo ""
        if [ -z "$TARGET_TO_BE_BOOT" ]; then
            die "Error: choose a valid target name"
        fi
    else
        usage
	    exit 1
    fi
fi

if [ "$1" = "-v" ]; then
	DEBUG=1
	OUT="1"
	shift
fi

if [ -z "$AVAILABLE_DISK" ]; then
    DEVICE=$1
    HDDIMG=$2
    TARGET_DEVICE=$3
else
    DEVICE=$DISK_TO_BE_FLASHED
    HDDIMG=$1
    TARGET_DEVICE=$TARGET_TO_BE_BOOT
fi

LINK=$(readlink $DEVICE)
if [ $? -eq 0 ]; then
	DEVICE="$LINK"
fi

if [ ! -w "$DEVICE" ]; then
	usage
	if [ ! -e "${DEVICE}" ] ; then
		die "Device $DEVICE cannot be found"
	else
		die "Device $DEVICE is not writable (need to run under sudo?)"
	fi
fi

if [ ! -e "$HDDIMG" ]; then
	usage
	die "HDDIMG $HDDIMG does not exist"
fi

#
# Ensure the hddimg is not mounted
#
unmount "$HDDIMG" || die "Failed to unmount $HDDIMG"

#
# Check if any $DEVICE partitions are mounted
#
unmount_device || die "Failed to unmount $DEVICE"

#
# Confirm device with user
#
image_details $HDDIMG
device_details $(basename $DEVICE)
echo -n "${INFO}Prepare EFI image on $DEVICE [y/N]?${CLEAR} "
read RESPONSE
if [ "$RESPONSE" != "y" ]; then
	echo "Image creation aborted"
	exit 0
fi


#
# Prepare the temporary working space
#
TMPDIR=$(mktemp -d mkefidisk-XXX) || die "Failed to create temporary mounting directory."
HDDIMG_MNT=$TMPDIR/hddimg
HDDIMG_ROOTFS_MNT=$TMPDIR/hddimg_rootfs
ROOTFS_MNT=$TMPDIR/rootfs
BOOTFS_MNT=$TMPDIR/bootfs
mkdir $HDDIMG_MNT || die "Failed to create $HDDIMG_MNT"
mkdir $HDDIMG_ROOTFS_MNT || die "Failed to create $HDDIMG_ROOTFS_MNT"
mkdir $ROOTFS_MNT || die "Failed to create $ROOTFS_MNT"
mkdir $BOOTFS_MNT || die "Failed to create $BOOTFS_MNT"


#
# Partition $DEVICE
#
DEVICE_SIZE=$(parted -s $DEVICE unit mb print | grep ^Disk | cut -d" " -f 3 | sed -e "s/MB//")
# If the device size is not reported there may not be a valid label
if [ "$DEVICE_SIZE" = "" ] ; then
	parted -s $DEVICE mklabel msdos || die "Failed to create MSDOS partition table"
	DEVICE_SIZE=$(parted -s $DEVICE unit mb print | grep ^Disk | cut -d" " -f 3 | sed -e "s/MB//")
fi
SWAP_SIZE=$((DEVICE_SIZE*SWAP_RATIO/100))
ROOTFS_SIZE=$((DEVICE_SIZE-BOOT_SIZE-SWAP_SIZE))
ROOTFS_START=$((BOOT_SIZE))
ROOTFS_END=$((ROOTFS_START+ROOTFS_SIZE))
SWAP_START=$((ROOTFS_END))

# MMC devices use a partition prefix character 'p'
PART_PREFIX=""
if [ ! "${DEVICE#/dev/mmcblk}" = "${DEVICE}" ] || [ ! "${DEVICE#/dev/loop}" = "${DEVICE}" ]; then
	PART_PREFIX="p"
fi
BOOTFS=$DEVICE${PART_PREFIX}1
ROOTFS=$DEVICE${PART_PREFIX}2
SWAP=$DEVICE${PART_PREFIX}3

TARGET_PART_PREFIX=""
if [ ! "${TARGET_DEVICE#/dev/mmcblk}" = "${TARGET_DEVICE}" ]; then
	TARGET_PART_PREFIX="p"
fi
TARGET_ROOTFS=$TARGET_DEVICE${TARGET_PART_PREFIX}2
TARGET_SWAP=$TARGET_DEVICE${TARGET_PART_PREFIX}3

echo ""
info "Boot partition size:   $BOOT_SIZE MB ($BOOTFS)"
info "ROOTFS partition size: $ROOTFS_SIZE MB ($ROOTFS)"
info "Swap partition size:   $SWAP_SIZE MB ($SWAP)"
echo ""

# Use MSDOS by default as GPT cannot be reliably distributed in disk image form
# as it requires the backup table to be on the last block of the device, which
# of course varies from device to device.

info "Partitioning installation media ($DEVICE)"

debug "Deleting partition table on $DEVICE"
dd if=/dev/zero of=$DEVICE bs=512 count=2 >$OUT 2>&1 || die "Failed to zero beginning of $DEVICE"

debug "Creating new partition table (MSDOS) on $DEVICE"
parted -s $DEVICE mklabel msdos >$OUT 2>&1 || die "Failed to create MSDOS partition table"

debug "Creating boot partition on $BOOTFS"
parted -s $DEVICE mkpart primary 0% $BOOT_SIZE >$OUT 2>&1 || die "Failed to create BOOT partition"

debug "Enabling boot flag on $BOOTFS"
parted -s $DEVICE set 1 boot on >$OUT 2>&1 || die "Failed to enable boot flag"

debug "Creating ROOTFS partition on $ROOTFS"
parted -s $DEVICE mkpart primary $ROOTFS_START $ROOTFS_END >$OUT 2>&1 || die "Failed to create ROOTFS partition"

debug "Creating swap partition on $SWAP"
parted -s $DEVICE mkpart primary $SWAP_START 100% >$OUT 2>&1 || die "Failed to create SWAP partition"

if [ $DEBUG -eq 1 ]; then
	parted -s $DEVICE print
fi


#
# Check if any $DEVICE partitions are mounted after partitioning
#
unmount_device || die "Failed to unmount $DEVICE partitions"


#
# Format $DEVICE partitions
#
info "Formatting partitions"
debug "Formatting $BOOTFS as vfat"
if [ ! "${DEVICE#/dev/loop}" = "${DEVICE}" ]; then
	mkfs.vfat -I $BOOTFS -n "EFI" >$OUT 2>&1 || die "Failed to format $BOOTFS"
else
	mkfs.vfat $BOOTFS -n "EFI" >$OUT 2>&1 || die "Failed to format $BOOTFS"
fi

debug "Formatting $ROOTFS as ext3"
mkfs.ext3 -F $ROOTFS -L "ROOT" >$OUT 2>&1 || die "Failed to format $ROOTFS"

debug "Formatting swap partition ($SWAP)"
mkswap $SWAP >$OUT 2>&1 || die "Failed to prepare swap"


#
# Installing to $DEVICE
#
debug "Mounting images and device in preparation for installation"
mount -o ro,loop $HDDIMG $HDDIMG_MNT >$OUT 2>&1 || error "Failed to mount $HDDIMG"
mount -o ro,loop $HDDIMG_MNT/rootfs.img $HDDIMG_ROOTFS_MNT >$OUT 2>&1 || error "Failed to mount rootfs.img"
mount $ROOTFS $ROOTFS_MNT >$OUT 2>&1 || error "Failed to mount $ROOTFS on $ROOTFS_MNT"
mount $BOOTFS $BOOTFS_MNT >$OUT 2>&1 || error "Failed to mount $BOOTFS on $BOOTFS_MNT"

info "Preparing boot partition"
EFIDIR="$BOOTFS_MNT/EFI/BOOT"
cp $HDDIMG_MNT/vmlinuz $BOOTFS_MNT >$OUT 2>&1 || error "Failed to copy vmlinuz"
# Copy the efi loader and configs (booti*.efi and grub.cfg if it exists)
cp -r $HDDIMG_MNT/EFI $BOOTFS_MNT >$OUT 2>&1 || error "Failed to copy EFI dir"
# Silently ignore a missing systemd-boot loader dir (we might just be a GRUB image)
cp -r $HDDIMG_MNT/loader $BOOTFS_MNT >$OUT 2>&1

# Update the boot loaders configurations for an installed image
# Remove any existing root= kernel parameters and:
# o Add a root= parameter with the target rootfs
# o Specify ro so fsck can be run during boot
# o Specify rootwait in case the target media is an asyncronous block device
#   such as MMC or USB disks
# o Specify "quiet" to minimize boot time when using slow serial consoles

# Look for a GRUB installation
GRUB_CFG="$EFIDIR/grub.cfg"
if [ -e "$GRUB_CFG" ]; then
	info "Configuring GRUB"
	# Delete the install entry
	sed -i "/menuentry 'install'/,/^}/d" $GRUB_CFG
	# Delete the initrd lines
	sed -i "/initrd /d" $GRUB_CFG
	# Delete any LABEL= strings
	sed -i "s/ LABEL=[^ ]*/ /" $GRUB_CFG

	sed -i "s@ root=[^ ]*@ @" $GRUB_CFG
	sed -i "s@vmlinuz @vmlinuz root=$TARGET_ROOTFS ro rootwait console=ttyS0 console=tty0 @" $GRUB_CFG
fi

# Look for a systemd-boot installation
SYSTEMD_BOOT_ENTRIES="$BOOTFS_MNT/loader/entries"
SYSTEMD_BOOT_CFG="$SYSTEMD_BOOT_ENTRIES/boot.conf"
if [ -d "$SYSTEMD_BOOT_ENTRIES" ]; then
	info "Configuring SystemD-boot"
	# remove the install target if it exists
	rm $SYSTEMD_BOOT_ENTRIES/install.conf >$OUT 2>&1

	if [ ! -e "$SYSTEMD_BOOT_CFG" ]; then
		echo "ERROR: $SYSTEMD_BOOT_CFG not found"
	fi

	sed -i "/initrd /d" $SYSTEMD_BOOT_CFG
	sed -i "s@ root=[^ ]*@ @" $SYSTEMD_BOOT_CFG
	sed -i "s@options *LABEL=boot @options LABEL=Boot root=$TARGET_ROOTFS ro rootwait console=ttyS0 console=tty0 @" $SYSTEMD_BOOT_CFG
fi

# Ensure we have at least one EFI bootloader configured
if [ ! -e $GRUB_CFG ] && [ ! -e $SYSTEMD_BOOT_CFG ]; then
	die "No EFI bootloader configuration found"
fi


info "Copying ROOTFS files (this may take a while)"
cp -a $HDDIMG_ROOTFS_MNT/* $ROOTFS_MNT >$OUT 2>&1 || die "Root FS copy failed"

echo "$TARGET_SWAP     swap             swap       defaults              0 0" >> $ROOTFS_MNT/etc/fstab

# We dont want udev to mount our root device while we're booting...
if [ -d $ROOTFS_MNT/etc/udev/ ] ; then
	echo "$TARGET_DEVICE" >> $ROOTFS_MNT/etc/udev/mount.blacklist
fi

# Add startup.nsh script for automated boot
printf "fs0:\%s\BOOT\%s\n" "EFI" "bootx64.efi" > $BOOTFS_MNT/startup.nsh


# Call cleanup to unmount devices and images and remove the TMPDIR
cleanup

echo ""
if [ $WARNINGS -ne 0 ] && [ $ERRORS -eq 0 ]; then
	echo "${YELLOW}Installation completed with warnings${CLEAR}"
	echo "${YELLOW}Warnings: $WARNINGS${CLEAR}"
elif [ $ERRORS -ne 0 ]; then
	echo "${RED}Installation encountered errors${CLEAR}"
	echo "${RED}Errors: $ERRORS${CLEAR}"
	echo "${YELLOW}Warnings: $WARNINGS${CLEAR}"
else
	success "Installation completed successfully"
fi
echo ""
