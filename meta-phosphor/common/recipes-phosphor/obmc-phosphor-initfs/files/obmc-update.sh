#!/bin/sh

echo update: "$@"

cd /
if ! test -r /proc/mounts || ! test -f /proc/mounts
then
	mkdir -p /proc
	mount -t proc proc proc
fi
if ! test -d /sys/class
then
	mkdir -p /sys
	mount -t sysfs sys sys
fi
if ! test -c /dev/null
then
	mkdir -p /dev
	mount -t devtmpfs dev dev
fi

if grep mtd /proc/mounts
then
	echo 1>&2 "Error: A mtd device is mounted."
	exit 1
fi

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

rwfs=$(findmtd rwfs)

rwdev=/dev/mtdblock${rwfs#mtd}
rwopts=rw
rorwopts=ro${rwopts#rw}

rwdir=/run/initramfs/rw
upper=$rwdir/cow
save=/run/save/${upper##*/}

mounted=
doclean=
dosave=y
dorestore=y
toram=

whitelist=/run/initramfs/whitelist
image=/run/initramfs/image-

while test "$1" != "${1#-}"
do
	case "$1" in
	--no-clean-saved-files)
		doclean=
		shift ;;
	--clean-saved-files)
		doclean=y
		shift ;;
	--no-save-files)
		dosave=
		shift ;;
	--save-files)
		dosave=y
		shift ;;
	--no-restore-files)
		dorestore=
		shift ;;
	--restore-files)
		dorestore=y
		shift ;;
	--copy-files)
		toram=y
		shift ;;
	*)
		echo 2>&1 "Unknown option $1"
		exit 1 ;;
	esac
done

if test "x$dosave" = xy
then
	if test ! -d $upper -a -n "$rwfs"
	then
		mkdir -p $rwdir
		mount $rwdev $rwdir -t $(probe_fs_type $rwdev) -o $rorwopts
		mounted=$rwdir
	fi

	while read f
	do
		if ! test -e $upper/$f
		then
			continue
		fi
		d="$save/$f"
		mkdir -p "${d%/*}"
		cp -rp $upper/$f "${d%/*}/"
	done < $whitelist

	if test -n "$mounted"
	then
		umount $mounted
	fi
fi

for f in $image*
do
	m=$(findmtd ${f#$image})
	if test -z "$m"
	then
		echo 1>&2  "Unable to find mtd partiton for ${f##*/}."
		exit 1
	fi
done

for f in $image*
do
	if test ! -s $f
	then
		echo "Skipping empty update of ${f#$image}."
		rm $f
		continue
	fi
	m=$(findmtd ${f#$image})
	echo "Updating ${f#$image}..."
	flashcp -v $f /dev/$m && rm $f
done

if test "x$toram" = xy
then
	mkdir -p $upper
	cp -rp $save/. $upper/
fi

if test -d $save -a "x$dorestore" = xy
then
	odir=$rwdir
	rwdir=/run/rw
	upper=$rwdir${upper#$odir}

	mkdir -p $rwdir
	mount $rwdev $rwdir -t $(probe_fs_type $rwdev) -o $rwopts
	mkdir -p $upper
	cp -rp $save/. $upper/
	umount $rwdir
	rmdir $rwdir
fi

if test "x$doclean" = xy
then
	rm -rf $save
fi

exit
