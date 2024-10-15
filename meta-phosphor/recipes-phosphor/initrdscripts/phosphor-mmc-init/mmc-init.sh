#!/bin/sh

kgetopt ()
{
    _cmdline="$(cat /proc/cmdline)"
    _optname="$1"
    _optval="$2"
    for _opt in $_cmdline
    do
        case "$_opt" in
            "${_optname}"=*)
                _optval="${_opt##"${_optname}"=}"
                ;;
            *)
                ;;
        esac
    done
    [ -n "$_optval" ] && echo "$_optval"
}

fslist="proc sys dev run"
rodir=/mnt/rofs

cd /

# We want to make all the directories in $fslist, not one directory named by
# concatonating the names with spaces
#
# shellcheck disable=SC2086
mkdir -p $fslist

mount dev dev -tdevtmpfs
mount sys sys -tsysfs
mount proc proc -tproc
mount tmpfs run -t tmpfs -o mode=755,nodev

# There eMMC GPT labels for the rootfs are rofs-a and rofs-b, and the label for
# the read-write partition is rwfs. Run udev to make the partition labels show
# up. Mounting by label allows for partition numbers to change if needed.
udevd --daemon
udevadm trigger --type=devices --action=add
udevadm settle --timeout=10
# The real udevd will be started a bit later by systemd-udevd.service
# so kill the one we started above now that we have the needed
# devices loaded
udevadm control --exit

# Get device sector size
rwfsdev="/dev/disk/by-partlabel/rwfs"
diskPartition=$(basename "$(readlink -f ${rwfsdev})")
blkdevname=$(basename "$(dirname "$(realpath "/sys/class/block/${diskPartition}")")")
blkdev="/dev/${blkdevname}"
sector_size=$(cat "/sys/class/block/${blkdevname}/queue/hw_sector_size")

# Check if any variable is missing, print error and drop to shell
# Print the values for debugging
echo "diskPartition: $diskPartition blkdev: $blkdev sector_size: $sector_size"

if [ -z "$diskPartition" ] || [ -z "$blkdev" ] || [ -z "$sector_size" ]; then
    echo "Error: diskPartition, blkdev, or sector_size is missing!"
    /bin/sh
fi

# Wait up to 5s for the mmc device to appear. Continue even if the count is
# exceeded. A failure will be caught later like in the mount command.
count=0
while [ $count -lt 5 ]; do
    if [ -e "${blkdev}" ]; then
        break
    fi
    echo "waiting for $blkdev"
    sleep 1
    count=$((count + 1))
done

# Move the secondary GPT to the end of the device if needed. Look for the GPT
# header signature "EFI PART" located sector_size bytes from the end of the device.
if ! tail -c "${sector_size}" "${blkdev}" | hexdump -C -n 8 | grep -q "EFI PART"; then
    sgdisk -e "${blkdev}"
    partprobe
fi

mkdir -p $rodir
if ! mount /dev/disk/by-partlabel/"$(kgetopt root=PARTLABEL)" $rodir -t ext4 -o ro; then
    /bin/sh
fi

# Determine if a factory reset has been requested
mkdir -p /var/lock
resetval=$(fw_printenv -n rwreset 2>/dev/null)
if gpiopresent=$(gpiofind factory-reset-toggle) ; then
    # gpiopresent contains both the gpiochip and line number as
    # separate words, and gpioget needs to see them as such.
    # shellcheck disable=SC2086
    gpioval=$(gpioget $gpiopresent)
else
    gpioval=""
fi
# Prevent unnecessary resets on first boot
if [ -n "$gpioval" ] && [ -z "$resetval" ]; then
    fw_setenv rwreset "$gpioval"
    resetval=$gpioval
fi
if [ "$resetval" = "true" ] || [ -n "$gpioval" ] && [ "$resetval" != "$gpioval" ]; then
    echo "Factory reset requested."
    if ! mkfs.ext4 -F "${rwfsdev}"; then
        echo "Reformat for factory reset failed."
        /bin/sh
    else
        # gpioval will be an empty string if factory-reset-toggle was not found
        fw_setenv rwreset "$gpioval"
        echo "rwfs has been formatted."
    fi
fi

fsck.ext4 -p "${rwfsdev}"
if ! mount "${rwfsdev}" $rodir/var -t ext4 -o rw; then
    /bin/sh
fi

rm -rf $rodir/var/persist/etc-work/
mkdir -p $rodir/var/persist/etc $rodir/var/persist/etc-work $rodir/var/persist/home/root
mount overlay $rodir/etc -t overlay -o lowerdir=$rodir/etc,upperdir=$rodir/var/persist/etc,workdir=$rodir/var/persist/etc-work

init="$(kgetopt init /sbin/init)"

for f in $fslist; do
    mount --move "$f" "$rodir/$f"
done

exec switch_root $rodir "$init"
