#!/bin/sh

fslist="proc sys dev run"
rodir=run/initramfs/ro
rwdir=run/initramfs/rw
upper=$rwdir/cow
work=$rwdir/work

cd /
mkdir -p $fslist
mount dev dev -tdevtmpfs
mount sys sys -tsysfs
mount proc proc -tproc
if ! grep run proc/mounts
then
	mount tmpfs run -t tmpfs -o mode=755,nodev
fi

mkdir -p $rodir $rwdir

cp -rp init shutdown update whitelist bin sbin usr lib etc var run/initramfs

# To start a interactive shell with job control at this point, run
# getty 38400 ttyS4

findmtd() {
	m=$(grep -xl "$1" /sys/class/mtd/*/name)
	m=${m%/name}
	m=${m##*/}
	echo $m
}

blkid_fs_type() {
	# Emulate util-linux's `blkid -s TYPE -o value $1`
	# Example busybox blkid output:
	#    # blkid /dev/mtdblock5
	#    /dev/mtdblock5: TYPE="squashfs"
	# Process output to extract TYPE value "squashfs".
	blkid $1 | sed -e 's/^.*TYPE="//' -e 's/".*$//'
}

probe_fs_type() {
	fst=$(blkid_fs_type $1)
	echo ${fst:=jffs2}
}

debug_takeover() {
	echo "$@"
	test -n "$@" && echo Enter password to try to manually fix.
	cat << HERE
After fixing run exit to continue this script, or reboot -f to retry, or
touch /takeover and exit to become PID 1 allowing editing of this script.
HERE

	while ! sulogin && ! test -f /takeover
	do
		echo getty failed, retrying
	done

	# Touch /takeover in the above getty to become pid 1
	if test -e /takeover
	then
		cat << HERE

Takeover of init requested.  Executing /bin/sh as PID 1.
When finished exec new init or cleanup and run reboot -f.

Warning: No job control!  Shell exit will panic the system!
HERE
		export PS1=init#\ 
		exec /bin/sh
	fi
}

env=$(findmtd u-boot-env)
if test -n $env
then
	ln -s /dev/$env /run/mtd:u-boot-env
	cp /run/mtd:u-boot-env /run/fw_env
fi

rofs=$(findmtd rofs)
rwfs=$(findmtd rwfs)

rodev=/dev/mtdblock${rofs#mtd}
rwdev=/dev/mtdblock${rwfs#mtd}

# Set to y for yes, anything else for no.
force_rwfst_jffs2=y
flash_images_before_init=n

rofst=squashfs
rwfst=$(probe_fs_type $rwdev)
roopts=ro
rwopts=rw

image=/run/initramfs/image-
trigger=${image}rwfs

init=/sbin/init
fsckbase=/sbin/fsck.
fsck=$fsckbase$rwfst
fsckopts=-a
optfile=/run/initramfs/init-options

if test ! -f $optfile
then
	cat /proc/cmdline > $optfile
fi

echo rofs = $rofs $rofst   rwfs = $rwfs $rwfst

if grep -w debug-init-sh $optfile
then
	debug_takeover "Debug initial shell requested by command line."
fi

# If there are images in root move them to /run/initramfs/ or /run/ now.
imagebasename=${image##*/}
if test -n "${imagebasename}" && ls /${imagebasename}* > /dev/null 2>&1
then
	if test "x$flash_images_before_init" = xy
	then
		echo "Flash images found, will update before starting init."
		mv /${imagebasename}* ${image%$imagebasename}
	else
		echo "Flash images found, will use but deferring flash update."
		mv /${imagebasename}* /run/
	fi
fi

if grep -w clean-rwfs-filesystem $optfile
then
	echo "Cleaning of read-write overlay filesystem requested."
	touch $trigger
fi

if test "x$force_rwfst_jffs2" = xy -a $rwfst != jffs2 -a ! -f $trigger
then
	echo "Converting read-write overlay filesystem to jffs2 forced."
	touch $trigger
fi

if ls $image* > /dev/null 2>&1
then
	if ! test -x /update
	then
		debug_takeover "Flash update requested but /update missing!"
	elif test -f $trigger -a ! -s $trigger
	then
		echo "Saving selected files from read-write overlay filesystem."
		/update && rm -f $image*
		echo "Clearing read-write overlay filesystem."
		flash_eraseall /dev/$rwfs
		echo "Restoring saved files to read-write overlay filesystem."
		touch $trigger
		/update 
		rm -rf /save $trigger
	else
		/update && rm -f $image*
	fi

	rwfst=$(probe_fs_type $rwdev)
	fsck=$fsckbase$rwfst
fi

if grep -w overlay-filesystem-in-ram $optfile
then
	rwfst=none
fi

if test -s /run/image-rofs
then
	rodev=/run/image-rofs
	roopts=$roopts,loop
fi

mount $rodev $rodir -t $rofst -o $roopts

if test -x $rodir$fsck
then
	for fs in $fslist
	do
		mount --bind $fs $rodir/$fs
	done
	chroot $rodir $fsck $fsckopts $rwdev
	rc=$?
	for fs in $fslist
	do
		umount $rodir/$fs
	done
	if test $rc -gt 1
	then
		debug_takeover "fsck of read-write fs on $rwdev failed (rc=$rc)"
	fi
elif test "$rwfst" != jffs2 -a "$rwfst" != none
then
	echo "No '$fsck' in read only fs, skipping fsck."
fi

if test "$rwfst" = none
then
	echo "Running with read-write overlay in RAM for this boot."
	echo "No state will be preserved unless flash update performed."
elif ! mount $rwdev $rwdir -t $rwfst -o $rwopts
then
	msg="$(cat)" << HERE

Mounting read-write $rwdev filesystem failed.  Please fix and run
	mount $rwdev $rwdir -t $rwfst -o $rwopts
to to continue, or do change nothing to run from RAM for this boot.
HERE
	debug_takeover "$msg"
fi

rm -rf $work
mkdir -p $upper $work

mount -t overlay -o lowerdir=$rodir,upperdir=$upper,workdir=$work cow /root

while ! chroot /root /bin/sh -c "test -x '$init' -a -s '$init'"
do
	msg="$(cat)" << HERE

Unable to confirm /sbin/init is an executable non-empty file
in merged file system mounted at /root.

Change Root test failed!  Invoking emergency shell.
HERE
	debug_takeover "$msg"
done

for f in $fslist
do
	mount --move $f root/$f
done

# switch_root /root $init
exec chroot /root $init

