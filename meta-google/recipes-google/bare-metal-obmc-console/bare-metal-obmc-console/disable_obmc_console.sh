#!/bin/bash

ENABLE_CONSOLE_FILE="/var/google/config-package/enable-bm-console.flag"
READ_ONLY_CONSOLE_FLAG="/run/readonly-console.flag"

[ ! -f $ENABLE_CONSOLE_FILE ] || exit 0

if [ "$1" == '-r' ]; then
    # re-enable obmc console
    touch $READ_ONLY_CONSOLE_FLAG

    # stop bmc console client will start the host console
    systemctl stop serial-to-bmc@*
else
    rm -f $READ_ONLY_CONSOLE_FLAG

    # stop host console client will start the bmc console
    systemctl stop serial-to-host@*
fi
