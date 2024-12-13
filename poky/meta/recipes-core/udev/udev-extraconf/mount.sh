#!/bin/sh
#
# Called from udev
#
# Attempt to mount any added block devices and umount any removed devices

BASE_INIT="`readlink -f "@base_sbindir@/init"`"
INIT_SYSTEMD="@systemd_unitdir@/systemd"
MOUNT_BASE="@MOUNT_BASE@"

if [ "x$BASE_INIT" = "x$INIT_SYSTEMD" ];then
    # systemd as init uses systemd-mount to mount block devices
    MOUNT="/usr/bin/systemd-mount"
    UMOUNT="/usr/bin/systemd-umount"

    if [ -x $MOUNT ] && [ -x $UMOUNT ];
    then
        logger "Using systemd-mount to finish mount"
    else
        logger "Linux init is using systemd, so please install systemd-mount to finish mount"
        exit 1
    fi
else
    MOUNT="/bin/mount"
    UMOUNT="/bin/umount"
fi

PMOUNT="/usr/bin/pmount"

for line in `grep -h -v ^# /etc/udev/mount.ignorelist /etc/udev/mount.ignorelist.d/*`
do
	if [ ` expr match "$DEVNAME" "$line" ` -gt 0 ];
	then
		logger "udev/mount.sh" "[$DEVNAME] is marked to ignore"
		exit 0
	fi
done

is_filesystem_supported() {
    while read -r fs; do
       if [ "${fs#nodev}" = "$1" ];
       then
           return 0
       fi
    done < "/proc/filesystems"
    return 1
}

automount_systemd() {
    name="`basename "$DEVNAME"`"

    # Skip already mounted partitions
    if [ -f /run/systemd/transient/$(echo $MOUNT_BASE | cut -d '/' -f 2- | sed 's#/#-#g')-*$name.mount ]; then
        logger "mount.sh/automount" "$MOUNT_BASE/$name already mounted"
        return
    fi

    # Get the unique name for mount point
    get_label_name "${DEVNAME}"

    # Only go for auto-mounting when the device has been cleaned up in remove
    # or has not been identified yet
    if [ -e "/tmp/.automount-$name" ]; then
            logger "mount.sh/automount" "[$MOUNT_BASE/$name] is already cached"
            return
    fi

    # Skip the partition which are already in /etc/fstab
    grep "^[[:space:]]*$DEVNAME" /etc/fstab && return
    for n in LABEL PARTLABEL UUID PARTUUID; do
        tmp="$(lsblk -o $n $DEVNAME | sed -e '1d')"
        test -z "$tmp" && continue
        tmp="$n=$tmp"
        grep "^[[:space:]]*$tmp" /etc/fstab && return
    done

    if ! is_filesystem_supported $ID_FS_TYPE; then
        logger "mount.sh/automount" "Filesystem '$ID_FS_TYPE' on '${DEVNAME}' is unsupported"
        return
    fi

    [ -d "$MOUNT_BASE/$name" ] || mkdir -p "$MOUNT_BASE/$name"

    MOUNT="$MOUNT -o silent"

    # If filesystemtype is vfat, change the ownership group to 'disk', and
    # grant it with  w/r/x permissions.
    case $ID_FS_TYPE in
    vfat|fat)
        MOUNT="$MOUNT -o umask=007,gid=`awk -F':' '/^disk/{print $3}' /etc/group`"
        ;;
    swap)
        return ;;
    lvm*|LVM*)
        return ;;
    # TODO
    *)
        ;;
    esac

    if ! $MOUNT --collect --no-block -t auto $DEVNAME "$MOUNT_BASE/$name"
    then
        #logger "mount.sh/automount" "$MOUNT -t auto $DEVNAME \"$MOUNT_BASE/$name\" failed!"
        rm_dir "$MOUNT_BASE/$name"
    else
        logger "mount.sh/automount" "Auto-mount of [$MOUNT_BASE/$name] successful"
        echo "$name" > "/tmp/.automount-$name"
    fi
}

automount() {
	name="`basename "$DEVNAME"`"

	if [ -x "$PMOUNT" ]; then
		$PMOUNT $DEVNAME 2> /dev/null
	elif [ -x $MOUNT ]; then
		$MOUNT $DEVNAME 2> /dev/null
	fi

	# If the device isn't mounted at this point, it isn't
	# configured in fstab
	grep -q "^$DEVNAME " /proc/mounts && return

	# Get the unique name for mount point
	get_label_name "${DEVNAME}"

        # Only go for auto-mounting when the device has been cleaned up in remove
        # or has not been identified yet
        if [ -e "/tmp/.automount-$name" ]; then
                logger "mount.sh/automount" "[$MOUNT_BASE/$name] is already cached"
                return
        fi

	! test -d "$MOUNT_BASE/$name" && mkdir -p "$MOUNT_BASE/$name"
	# Silent util-linux's version of mounting auto
	if [ "x`readlink $MOUNT`" = "x/bin/mount.util-linux" ] ;
	then
		MOUNT="$MOUNT -o silent"
	fi

	# If filesystem type is vfat, change the ownership group to 'disk', and
	# grant it with  w/r/x permissions.
	case $ID_FS_TYPE in
	vfat|fat)
		MOUNT="$MOUNT -o umask=007,gid=`awk -F':' '/^disk/{print $3}' /etc/group`"
		;;
	swap)
		return ;;
	lvm*|LVM*)
                return ;;
	# TODO
	*)
		;;
	esac

	if ! $MOUNT -t auto $DEVNAME "$MOUNT_BASE/$name"
	then
		#logger "mount.sh/automount" "$MOUNT -t auto $DEVNAME \"$MOUNT_BASE/$name\" failed!"
		rm_dir "$MOUNT_BASE/$name"
	else
		logger "mount.sh/automount" "Auto-mount of [$MOUNT_BASE/$name] successful"
		# The actual device might not be present in the remove event so blkid cannot
		# be used to calculate what name was generated here. Simply save the mount
		# name in our tmp file.
		echo "$name" > "/tmp/.automount-$name"
	fi
}
	
rm_dir() {
	# We do not want to rm -r populated directories
	if test "`find "$1" | wc -l | tr -d " "`" -lt 2 -a -d "$1"
	then
		! test -z "$1" && rm -r "$1"
	else
		logger "mount.sh/automount" "Not removing non-empty directory [$1]"
	fi
}

get_label_name() {
	# Get the LABEL or PARTLABEL
	LABEL=`/sbin/blkid | grep "$1:" | grep -o 'LABEL=".*"' | cut -d '"' -f2`
	# If the $DEVNAME has a LABEL or a PARTLABEL
	if [ -n "$LABEL" ]; then
	        # Set the mount location dir name to LABEL appended
        	# with $name e.g. label-sda. That would avoid overlapping
	        # mounts in case two devices have same LABEL
        	name="${LABEL}-${name}"
	fi
}

# No ID_FS_TYPE for cdrom device, yet it should be mounted
name="`basename "$DEVNAME"`"
[ -e /sys/block/$name/device/media ] && media_type=`cat /sys/block/$name/device/media`

if [ "$ACTION" = "add" ] && [ -n "$DEVNAME" ] && [ -n "$ID_FS_TYPE" -o "$media_type" = "cdrom" ]; then
    # Note the root filesystem can show up as /dev/root in /proc/mounts,
    # so check the device number too
    if expr $MAJOR "*" 256 + $MINOR != `stat -c %d /`; then
        if [ "`basename $MOUNT`" = "systemd-mount" ];then
            automount_systemd
        else
            automount
        fi
    fi
fi

if [ "$ACTION" = "remove" ] || [ "$ACTION" = "change" ] && [ -x "$UMOUNT" ] && [ -n "$DEVNAME" ]; then
    name="`basename "$DEVNAME"`"
    tmpfile=`find /tmp | grep "\.automount-.*${name}$"`
    if [ ! -e "/sys/$DEVPATH" -a -e "$tmpfile" ]; then
        logger "mount.sh/remove" "cleaning up $DEVNAME, was mounted by the auto-mounter"
        for mnt in `cat /proc/mounts | grep "$DEVNAME" | cut -f 2 -d " " `
        do
                $UMOUNT "`printf $mnt`"
        done
        # Remove mount directory created by the auto-mounter
        # and clean up our tmp cache file
        mntdir=`cat "$tmpfile"`
        rm_dir "$MOUNT_BASE/$mntdir"
        rm "$tmpfile"
    fi
fi
