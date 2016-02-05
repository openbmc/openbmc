#!/bin/sh

rodir=run/initramfs/ro
rwdir=run/initramfs/rw
upper=$rwdir/cow
work=$rwdir/work

cd /
mkdir -p sys proc dev run
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

env=$(findmtd u-boot-env)
if test -n $env
then
	ln -s /dev/$env /run/mtd:u-boot-env
	cp /run/mtd:u-boot-env /run/fw_env
fi

rofs=$(findmtd rofs)
rwfs=$(findmtd rwfs)

rofst=squashfs
rwfst=ext4

echo rofs = $rofs $rofst   rwfs = $rwfs $rwfst

if grep -w debug-init-sh /proc/cmdline ||
	! mount -o rw /dev/mtdblock${rwfs#mtd} $rwdir -t $rwfst
then
	echo Please mount the rw file system on $rwdir from this shell
	while ! sulogin && ! test -f /takeover
	do
		echo getty failed, retrying
	done
fi

# Touch /takeover in the above getty to become pid 1
if test -e /takeover
then
	export PS1=init#\ 
	exec /bin/sh
fi

mount -o ro /dev/mtdblock${rofs#mtd} $rodir -t $rofst

rm -rf $work
mkdir -p $upper
mkdir -p $work

mount -t overlay -o lowerdir=$rodir,upperdir=$upper,workdir=$work cow /root

if ! chroot /root /bin/sh -c "test -x /sbin/init -a -s /sbin/init"
then
	echo "Change Root test failed!  Invoking emergency shell."
	PS1=rescue#\  sulogin
fi

for f in sys dev proc run
do
	mount --move $f root/$f
done

# switch_root /root /sbin/init
exec chroot /root /sbin/init

