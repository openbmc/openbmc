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

rofst=squashfs
rwfst=ext4
roopts=ro
rwopts=rw

init=/sbin/init

echo rofs = $rofs $rofst   rwfs = $rwfs $rwfst

if grep -w debug-init-sh /proc/cmdline
then
	debug_takeover "Debug initial shell requested by command line."
fi

if ! mount $rwdev $rwdir -t $rwfst -o $rwopts
then
	msg="$(cat)" << HERE

Mounting read-write $rwdev filesystem failed.  Please fix and run
	mount $rwdev $rwdir -t $rwfs -o $rwopts
to to continue, or do change nothing to run from RAM for this boot.
HERE
	debug_takeover "$msg"
fi

mount $rodev $rodir -t $rofst -o $roopts

rm -rf $work
mkdir -p $upper
mkdir -p $work

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

