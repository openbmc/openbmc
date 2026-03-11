#!/bin/sh
#
# SPDX-License-Identifier: GPL-2.0-only
#

### BEGIN INIT INFO
# Provides:          rmnologin
# Required-Start:    $remote_fs $all
# Required-Stop: 
# Default-Start:     2 3 4 5
# Default-Stop:
# Short-Description: Remove /etc/nologin at boot
# Description:       This script removes the /etc/nologin file as the
#                    last step in the boot process, if DELAYLOGIN=yes.
#                    If DELAYLOGIN=no, /etc/nologin was not created by
#                    bootmisc earlier in the boot process.
### END INIT INFO

if test -f /etc/nologin.boot
then
	rm -f /etc/nologin /etc/nologin.boot
fi

: exit 0
