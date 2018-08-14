#!/bin/sh

PATH=/sbin:/bin:/usr/sbin:/usr/bin

do_mount_fs() {
	grep -q "$1" /proc/filesystems || return
	test -d "$2" || mkdir -p "$2"
	mount -t "$1" "$1" "$2"
}

do_mknod() {
	test -e "$1" || mknod "$1" "$2" "$3" "$4"
}

mkdir -p /proc
mount -t proc proc /proc

do_mount_fs sysfs /sys
do_mount_fs debugfs /sys/kernel/debug
do_mount_fs devtmpfs /dev
do_mount_fs devpts /dev/pts
do_mount_fs tmpfs /dev/shm

mkdir -p /run
mkdir -p /var/run

do_mknod /dev/console c 5 1
do_mknod /dev/null c 1 3
do_mknod /dev/zero c 1 5

exec sh </dev/console >/dev/console 2>/dev/console 
