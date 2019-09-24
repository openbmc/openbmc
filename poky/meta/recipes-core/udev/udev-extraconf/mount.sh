#!/bin/sh
#
# Called from udev
#
# Attempt to mount any added block devices and umount any removed devices

BASE_INIT="`readlink -f "@base_sbindir@/init"`"
INIT_SYSTEMD="@systemd_unitdir@/systemd"

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

for line in `grep -h -v ^# /etc/udev/mount.blacklist /etc/udev/mount.blacklist.d/*`
do
	if [ ` expr match "$DEVNAME" "$line" ` -gt 0 ];
	then
		logger "udev/mount.sh" "[$DEVNAME] is blacklisted, ignoring"
		exit 0
	fi
done

automount_systemd() {
    name="`basename "$DEVNAME"`"

    # Skip already mounted partitions
    if [ -f /run/systemd/transient/run-media-$name.mount ]; then
        logger "mount.sh/automount" "/run/media/$name already mounted"
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

    [ -d "/run/media/$name" ] || mkdir -p "/run/media/$name"

    MOUNT="$MOUNT -o silent"

    # If filesystemtype is vfat, change the ownership group to 'disk', and
    # grant it with  w/r/x permissions.
    case $ID_FS_TYPE in
    vfat|fat)
        MOUNT="$MOUNT -o umask=007,gid=`awk -F':' '/^disk/{print $3}' /etc/group`"
        ;;
    swap)
        return ;;
    # TODO
    *)
        ;;
    esac

    if ! $MOUNT --no-block -t auto $DEVNAME "/run/media/$name"
    then
        #logger "mount.sh/automount" "$MOUNT -t auto $DEVNAME \"/run/media/$name\" failed!"
        rm_dir "/run/media/$name"
    else
        logger "mount.sh/automount" "Auto-mount of [/run/media/$name] successful"
        touch "/tmp/.automount-$name"
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

	! test -d "/run/media/$name" && mkdir -p "/run/media/$name"
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
	# TODO
	*)
		;;
	esac

	if ! $MOUNT -t auto $DEVNAME "/run/media/$name"
	then
		#logger "mount.sh/automount" "$MOUNT -t auto $DEVNAME \"/run/media/$name\" failed!"
		rm_dir "/run/media/$name"
	else
		logger "mount.sh/automount" "Auto-mount of [/run/media/$name] successful"
		touch "/tmp/.automount-$name"
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
    for mnt in `cat /proc/mounts | grep "$DEVNAME" | cut -f 2 -d " " `
    do
        $UMOUNT $mnt
    done

    # Remove empty directories from auto-mounter
    name="`basename "$DEVNAME"`"
    test -e "/tmp/.automount-$name" && rm_dir "/run/media/$name"
fi
