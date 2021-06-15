#!/bin/sh
### BEGIN INIT INFO
# Provides:          mountvirtfs
# Required-Start:
# Required-Stop:
# Default-Start:     S
# Default-Stop:
# Short-Description: Mount kernel virtual file systems.
# Description:       Mount initial set of virtual filesystems the kernel
#                    provides and that are required by everything.
### END INIT INFO

if [ -e /proc ] && ! [ -e /proc/mounts ]; then
  mount -t proc proc /proc
fi

if [ -e /sys ] && grep -q sysfs /proc/filesystems && ! [ -e /sys/class ]; then
  mount -t sysfs sysfs /sys
fi

if [ -e /sys/kernel/debug ] && grep -q debugfs /proc/filesystems; then
  mount -t debugfs debugfs /sys/kernel/debug
fi

if [ -e /sys/kernel/config ] && grep -q configfs /proc/filesystems; then
  mount -t configfs configfs /sys/kernel/config
fi

if [ -e /sys/firmware/efi/efivars ] && grep -q efivarfs /proc/filesystems; then
  mount -t efivarfs efivarfs /sys/firmware/efi/efivars
fi

if ! [ -e /dev/zero ] && [ -e /dev ] && grep -q devtmpfs /proc/filesystems; then
  mount -n -t devtmpfs devtmpfs /dev
fi
