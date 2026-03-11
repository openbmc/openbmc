#!/bin/sh
#
# SPDX-License-Identifier: GPL-2.0-only
#

### BEGIN INIT INFO
# Provides:          umountnfs
# Required-Start:
# Required-Stop:     umountfs
# Should-Stop:       $network $portmap
# Default-Start:
# Default-Stop:      0 6
# Short-Description: Unmount all network filesystems
### END INIT INFO

PATH=/sbin:/bin:/usr/sbin:/usr/bin

# Write a reboot record to /var/log/wtmp before unmounting
halt -w

echo "Unmounting remote filesystems..."

test -f /etc/fstab && (

#
#	Read through fstab line by line and unount network file systems
#
while read device mountpt fstype options
do
	if test "$fstype" = nfs ||  test "$fstype" = smbfs ||  test "$fstype" = ncpfs || test "$fstype" = cifs
	then
		umount -f $mountpt
	fi
done
) < /etc/fstab

: exit 0
