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

rofs=$(findmtd rofs)
rwfs=$(findmtd rwfs)

rofst=squahsfs
rwfst=ext4

if test -n "$rwfs" && test -s whitelist
then

	mkdir -p rw
	mount /dev/mtdblock${rwfs#mtd} rw -oro -t $rwfst

	while read f
	do
		if ! test -e rw/cow/$f
		then
			continue
		fi
		d="save/cow/$f"
		mkdir -p "${d%/*}"
		cp -rp rw/cow/$f "${d%/*}/"
	done < whitelist

	umount rw
fi


for f in image-*
do
	m=$(findmtd ${f#image-})
	if test -z "$m" 
	then
		echo 1>&2  "Unable to find mtd partiton for $f"
		exec /bin/sh
	fi

	echo "Updating ${f#image-}"
	# flasheraseall /dev/$m && dd if=$f of=/dev/$m
	flashcp -v $f /dev/$m
done


if test -d save/cow
then
	mount /dev/mtdblock${rwfs#mtd} rw -o rw -t $rwfst
	cp -rp save/cow/. rw/cow/
	umount rw
fi

# Execute the command systemd told us to ...
if test -d /oldroot && test -x "/sbin/$1" && test -f "/sbin/$1"
then
	if test "$1" -eq kexec
	then
		/sbin/$1 -f -e
	else
		/sbin/$1 -f
	fi
fi


echo "Execute ${1-reboot} -f if all is ok"

export PS1=update-sh#\ 
exec /bin/sh
