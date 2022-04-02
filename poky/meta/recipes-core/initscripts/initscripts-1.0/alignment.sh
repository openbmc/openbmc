#!/bin/sh
#
# SPDX-License-Identifier: GPL-2.0-only
#

### BEGIN INIT INFO
# Provides: alignment
# Required-Start:    mountkernfs
# Required-Stop:     mountkernfs
# Default-Start:     S
# Default-Stop:
### END INIT INFO

if [ -e /proc/cpu/alignment ]; then
   echo "3" > /proc/cpu/alignment
fi

