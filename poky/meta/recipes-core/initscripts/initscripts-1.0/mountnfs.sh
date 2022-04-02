#!/bin/sh
#
# SPDX-License-Identifier: GPL-2.0-only
#

### BEGIN INIT INFO
# Provides:          mountnfs
# Required-Start:    $local_fs $network $rpcbind
# Required-Stop:
# Default-Start:     S
# Default-Stop:
### END INIT INFO

#
#	Run in a subshell because of I/O redirection.
#
test -f /etc/fstab && (

#
#	Read through fstab line by line. If it is NFS, set the flag
#	for mounting NFS filesystems. If any NFS partition is found and it
#	not mounted with the nolock option, we start the rpcbind.
#
rpcbind=no
mount_nfs=no
mount_smb=no
mount_ncp=no
mount_cifs=no
while read device mountpt fstype options
do
	case "$device" in
		""|\#*)
			continue
			;;
	esac

	case "$options" in
		*noauto*)
			continue
			;;
	esac

	if test "$fstype" = nfs
	then
		mount_nfs=yes
		case "$options" in
			*nolock*)
				;;
			*)
				rpcbind=yes
				;;
		esac
	fi
	if test "$fstype" = smbfs
	then
		mount_smb=yes
	fi
	if test "$fstype" = ncpfs
	then
		mount_ncp=yes
	fi
	if test "$fstype" = cifs
	then
		mount_cifs=yes
	fi
done

exec 0>&1

if test "$rpcbind" = yes
then
	if test -x /usr/sbin/rpcbind
	then
		service rpcbind status > /dev/null
		if [ $? != 0 ]; then
			echo -n "Starting rpcbind..."
			start-stop-daemon --start --quiet --exec /usr/sbin/rpcbind
			sleep 2
		fi
	fi
fi

if test "$mount_nfs" = yes || test "$mount_smb" = yes || test "$mount_ncp" = yes || test "$mount_cifs" = yes
then
	echo "Mounting remote filesystems..."
	test "$mount_nfs" = yes && mount -a -t nfs
	test "$mount_smb" = yes && mount -a -t smbfs
	test "$mount_ncp" = yes && mount -a -t ncpfs
	test "$mount_cifs" = yes && mount -a -t cifs
fi

) < /etc/fstab

: exit 0

