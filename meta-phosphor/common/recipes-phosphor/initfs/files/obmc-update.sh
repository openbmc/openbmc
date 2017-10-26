#!/bin/sh

echo update: "$@"

echoerr() {
	echo 1>&2 "ERROR: $@"
}

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

# mtd number N with mtd name Name can be mounted via mtdN, or mtd:Name
# (with a mtd aware fs) or by /dev/mtdblockN (with a mtd or block fs).
mtdismounted() {
	m=${1##mtd}
	if grep -s "mtdblock$m " /proc/mounts || grep -s "mtd$m " /proc/mounts
	then
		return 0
	fi
	n=$(cat /sys/class/mtd/mtd$m/name)
	if test -n "$n" && grep -s "mtd:$n " /proc/mounts
	then
		return 0
	fi
	return 1
}

# Detect child partitions when the whole flash is to be updated.
# Ignore mtdNro and mtdblockN names in the class subsystem directory.
childmtds() {
	for m in /sys/class/mtd/$1/mtd*
	do
		m=${m##*/}
		if test "${m%ro}" = "${m#mtdblock}"
		then
			echo $m
		fi
	done
}

toobig() {
	if test $(stat -L -c "%s" "$1") -gt $(cat /sys/class/mtd/"$2"/size)
	then
		return 0
	fi
	return 1
}

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
doflash=y
doclean=
dosave=y
dorestore=y
toram=
checksize=y
checkmount=y

whitelist=/run/initramfs/whitelist
image=/run/initramfs/image-
imglist=

while test "$1" != "${1#-}"
do
	case "$1" in
	--help)
		cat <<HERE
Usage: $0 [options] -- Write images in /run/initramfs to flash (/dev/mtd*)
    --help                    Show this message
    --no-flash                Don't attempt to write images to flash
    --ignore-size             Don't compare image size to mtd device size
    --ignore-mount            Don't check if destination is mounted
    --save-files              Copy whitelisted files to save directory in RAM
    --no-save-files           Don't copy whitelisted files to save directory
    --copy-files              Copy files from save directory to rwfs mountpoint
    --restore-files           Restore files from save directory to rwfs layer
    --no-restore-files        Don't restore saved files from ram to rwfs layer
    --clean-saved-files       Delete saved whitelisted files from RAM
    --no-clean-saved-files    Retain saved whitelisted files in RAM
HERE

	    exit 0 ;;

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
	--no-flash)
		doflash=
		shift ;;
	--ignore-size)
		checksize=
		shift ;;
	--ignore-mount)
		checkmount=
		doflash=
		shift ;;
	--copy-files)
		toram=y
		shift ;;
	*)
		echoerr "Unknown option $1.  Try $0 --help."
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
		# Entries shall start with /, no trailing /.. or embedded /../
		if test "/${f#/}" != "$f" -o "${f%/..}" != "${f#*/../}"
		then
			echo 1>&2 "WARNING: Skipping bad whitelist entry $f."
			continue
		fi
		if ! test -e "$upper/$f"
		then
			continue
		fi
		d="$save/$f"
		while test "${d%/}" != "${d%/.}"
		do
			d="${d%/.}"
			d="${d%/}"
		done
		mkdir -p "${d%/*}"
		cp -rp "$upper/$f" "${d%/*}/"
	done < $whitelist

	if test -n "$mounted"
	then
		umount $mounted
	fi
fi

imglist=$(echo $image*)
if test "$imglist" = "$image*" -a ! -e "$imglist"
then
	# shell didn't expand the wildcard, so no files exist
	echo "No images found to update."
	imglist=
fi

for f in $imglist
do
	m=$(findmtd ${f#$image})
	if test -z "$m"
	then
		echoerr "Unable to find mtd partition for ${f##*/}."
		exit 1
	fi
	if test -n "$checksize" && toobig "$f" "$m"
	then
		echoerr "Image ${f##*/} too big for $m."
		exit 1
	fi
	for s in $m $(childmtds $m)
	do
		if test -n "$checkmount" && mtdismounted $s
		then
			echoerr "Device $s is mounted, ${f##*/} is busy."
			exit 1
		fi
	done
done

if test -n "$doflash"
then
	for f in $imglist
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
fi

if test -d $save -a "x$toram" = xy
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
