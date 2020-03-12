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

# This fw_get_env_var is a possibly broken version of fw_printenv that
# does not check the crc or flag byte.
# The u-boot environment starts with a crc32, followed by a flag byte
# when a redundannt environment is configured, followed by var=value\0 sets.
# The flag byte for nand is a 1 byte counter; for nor it is a 1 or 0 byte.

get_fw_env_var() {
	# do we have 1 or 2 copies of the environment?
	# count non-blank non-comment lines
	# copies=$(grep -v ^# /etc/fw_env.config | grep -c [::alnum::])
	# ... we could if we had the fw_env.config in the initramfs
	copies=2

	# * Change \n to \r and \0 to \n
	# * Skip to the 5th byte to skip over crc
	# * then skip to the first or 2nd byte to skip over flag if it exists
	# * stop parsing at first empty line corresponding to the
	#   double \0 at the end of the environment.
	# * print the value of the variable name passed as argument

	envdev=$(findmtd u-boot-env)
	if test -n $envdev
	then
		cat /dev/$envdev |
		tr '\n\000' '\r\n' |
		tail -c +5 | tail -c +${copies-1} |
		sed -ne '/^$/,$d' -e "s/^$1=//p"
	fi
}

setup_resolv() {
	runresolv=/run/systemd/resolve/resolv.conf
	etcresolv=/etc/resolv.conf

	if test ! -e $etcresolv -a ! -L $etcresolv
	then
		mkdir -p ${runresolv%/*}
		ln -s $runresolv $etcresolv
	fi
	if test ! -f $runresolv
	then
		cat  /proc/net/pnp > $runresolv
	fi

	return 0
}

try_tftp() {
	# split into  tftp:// host:port/ path/on/remote
	# then spilt off / and then :port from the end of host:port/
	# and : from the beginning of port

	rest="${1#tftp://}"
	path=${rest#*/}
	host=${rest%$path}
	host="${host%/}"
	port="${host#${host%:*}}"
	host="${host%$port}"
	port="${port#:}"

	setup_resolv

	if test -z "$host" -o -z "$path"
	then
		debug_takeover "Invalid tftp download url '$url'."
	elif echo "Downloading '$url' from $host ..."  &&
		! tftp -g -r "$path" -l /run/image-rofs "$host" ${port+"$port"}
	then
		debug_takeover "Download of '$url' failed."
	fi
}

try_wget() {
	setup_resolv

	echo "Downloading '$1' ..."
	if ! wget -O /run/image-rofs "$1"
	then
		debug_takeover "Download of '$url' failed."
	fi
}

debug_takeover() {
	echo "$@"

	if ! grep -w enable-initrd-debug-sh "$optfile"
	then
		echo "Fatal error, triggering kernel panic!"
		exit 1
	fi

	test -n "$@" && echo Try to manually fix.
	cat << HERE
After fixing run exit to continue this script, or reboot -f to retry, or
touch /takeover and exit to become PID 1 allowing editing of this script.
HERE

	while ! /bin/sh && ! test -f /takeover
	do
		echo /bin/sh failed, retrying
	done

	# Touch /takeover in the above shell to become pid 1
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

rofs=$(findmtd rofs)
rwfs=$(findmtd rwfs)

rodev=/dev/mtdblock${rofs#mtd}
rwdev=/dev/mtdblock${rwfs#mtd}

# Set to y for yes, anything else for no.
force_rwfst_jffs2=y
flash_images_before_init=n
consider_download_files=y
consider_download_tftp=y
consider_download_http=y
consider_download_ftp=y

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
optbase=/run/initramfs/init-options-base
urlfile=/run/initramfs/init-download-url
update=/run/initramfs/update

if test -e /${optfile##*/}
then
	cp /${optfile##*/} $optfile
fi

if test -e /${optbase##*/}
then
	cp /${optbase##*/} $optbase
else
	touch $optbase
fi

if test ! -f $optfile
then
	cat /proc/cmdline $optbase > $optfile
	get_fw_env_var openbmcinit >> $optfile
	get_fw_env_var openbmconce >> $optfile
fi

echo rofs = $rofs $rofst   rwfs = $rwfs $rwfst

if grep -w debug-init-sh $optfile
then
	if grep -w enable-initrd-debug-sh "$optfile"
	then
		debug_takeover "Debug initial shell requested by command line."
	else
		echo "Need to also add enable-initrd-debug-sh for debug shell."
	fi
fi

if test "x$consider_download_files" = xy &&
	grep -w openbmc-init-download-files $optfile
then
	if test -f ${urlfile##*/}
	then
		cp ${urlfile##*/} $urlfile
	fi
	if test ! -f $urlfile
	then
		get_fw_env_var openbmcinitdownloadurl > $urlfile
	fi
	url="$(cat $urlfile)"
	rest="${url#*://}"
	proto="${url%$rest}"

	if test -z "$url"
	then
		echo "Download url empty.  Ignoring download request."
	elif test -z "$proto"
	then
		echo "Download failed."
	elif test "$proto" = tftp://
	then
		if test "x$consider_download_tftp" = xy
		then
			try_tftp "$url"
		else
			echo "Download failed."
		fi
	elif test "$proto" = http://
	then
		if test "x$consider_download_http" = xy
		then
			try_wget "$url"
		else
			echo "Download failed."
		fi
	elif test "$proto" = ftp://
	then
		if test "x$consider_download_ftp" = xy
		then
			try_wget "$url"
		else
			echo "Download failed."
		fi
	else
		echo "Download failed."
	fi
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

if grep -w factory-reset $optfile
then
	echo "Factory reset requested."
	touch $trigger
	do_save=--no-save-files
else
	do_save=--save-files
fi

if test "x$force_rwfst_jffs2" = xy -a $rwfst != jffs2 -a ! -f $trigger
then
	echo "Converting read-write overlay filesystem to jffs2 forced."
	touch $trigger
fi

if ls $image* > /dev/null 2>&1
then
	if ! test -x $update
	then
		debug_takeover "Flash update requested but $update missing!"
	elif test -f $trigger -a ! -s $trigger
	then
		if [ $do_save = "--save-files" ]
		then
			echo "Saving selected files from read-write overlay filesystem."
		else
			echo "No files will be selected for save."
		fi
		$update --no-restore-files $do_save
		echo "Clearing read-write overlay filesystem."
		flash_eraseall /dev/$rwfs
		echo "Restoring saved files to read-write overlay filesystem."
		touch $trigger
		$update --no-save-files --clean-saved-files
	else
		$update --clean-saved-files $do_save
	fi

	rwfst=$(probe_fs_type $rwdev)
	fsck=$fsckbase$rwfst
fi

if grep -w overlay-filesystem-in-ram $optfile
then
	rwfst=none
fi

copyfiles=
if grep -w copy-files-to-ram $optfile
then
	rwfst=none
	copyfiles=y
fi

# It would be nice to do this after fsck but that mean rofs is mounted
# which triggers the mtd is mounted check
if test "$rwfst$copyfiles" = noney
then
	touch $trigger
	$update --copy-files --clean-saved-files --no-restore-files
fi

if grep -w copy-base-filesystem-to-ram $optfile &&
	test ! -e /run/image-rofs && ! cp $rodev /run/image-rofs
then
	# Remove any partial copy to avoid attempted usage later
	if test -e  /run/image-rofs
	then
		ls -l /run/image-rofs
		rm -f /run/image-rofs
	fi
	debug_takeover "Copying $rodev to /run/image-rofs failed."
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
or perform a factory reset with the clean-rwfs-filesystem option.
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

Change Root test failed!
HERE
	debug_takeover "$msg"
done

for f in $fslist
do
	mount --move $f root/$f
done

# switch_root /root $init
exec chroot /root $init

