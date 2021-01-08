#!/bin/sh
### BEGIN INIT INFO
# Provides:          mountall
# Required-Start:    mountvirtfs
# Required-Stop: 
# Default-Start:     S
# Default-Stop:
# Short-Description: Mount all filesystems.
# Description:
### END INIT INFO

. /etc/default/rcS

#
# Mount local filesystems in /etc/fstab. For some reason, people
# might want to mount "proc" several times, and mount -v complains
# about this. So we mount "proc" filesystems without -v.
#
test "$VERBOSE" != no && echo "Mounting local filesystems..."
mount -at nonfs,nosmbfs,noncpfs 2>/dev/null


# We might have mounted something over /run; see if
# /dev/initctl is present.  Look for
# /sbin/init.sysvinit to verify that sysvinit (and
# not busybox or systemd) is installed as default init).
INITCTL="/dev/initctl"
if [ ! -p "$INITCTL" ] && [ "${INIT_SYSTEM}" = "sysvinit" ]; then
    # Create new control channel
		rm -f "$INITCTL"
		mknod -m 600 "$INITCTL" p

		# Reopen control channel.
		PID="$(pidof -s /sbin/init || echo 1)"
		[ -n "$PID" ] && kill -s USR1 "$PID"
fi

#
# Execute swapon command again, in case we want to swap to
# a file on a now mounted filesystem.
#
[ -x /sbin/swapon ] && swapon -a

: exit 0

