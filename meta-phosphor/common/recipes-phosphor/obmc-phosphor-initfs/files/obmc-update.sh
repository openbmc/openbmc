#!/bin/sh

echo update: "$@"

export PS1=update-sh#\ 
# exec /bin/sh

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
while grep mtd /proc/mounts
do
	echo 1>&2 "Error: A mtd device is mounted."
	sulogin
	# exec /bin/sh
done

findmtd() {
	m=$(grep -xl "$1" /sys/class/mtd/*/name)
	m=${m%/name}
	m=${m##*/}
	echo $m
}

rwfs=$(findmtd rwfs)

rwdev=/dev/mtdblock${rwfs#mtd}
rwfst=ext4
rwopts=rw
rorwopts=ro${rwopts#rw}

rwdir=rw
upper=$rwdir/cow
save=save/${upper##*/}

if test -n "$rwfs" && test -s whitelist
then

	mkdir -p $rwdir
	mount $rwdev $rwdir -t $rwfst -o $rorwopts

	while read f
	do
		if ! test -e $upper/$f
		then
			continue
		fi
		d="$save/$f"
		mkdir -p "${d%/*}"
		cp -rp $upper/$f "${d%/*}/"
	done < whitelist

	umount $rwdir
fi

image=/run/initramfs/image-
for f in $image*
do
	m=$(findmtd ${f#$image})
	if test -z "$m"
	then
		echo 1>&2  "Unable to find mtd partiton for ${f##*/}."
		exec /bin/sh
	fi
done

for f in $image*
do
	m=$(findmtd ${f#$image})
	echo "Updating ${f#$image}..."
	# flasheraseall /dev/$m && dd if=$f of=/dev/$m
	flashcp -v $f /dev/$m
done

if test -d $save
then
	mount $rwdev $rwdir -t $rwfst -o $rwopts
	cp -rp $save/. $upper/
	umount $rwdir
fi

exit

# NOT REACHED without edit
# NOT REACHED without edit

echo "Flash completed.  Inspect, cleanup and reboot -f to continue."

export PS1=update-sh#\ 
exec /bin/sh
