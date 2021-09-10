#!/bin/bash

source /usr/sbin/gpio-defs.sh
source /usr/sbin/gpio-lib.sh

host_status() {
    st=$(busctl get-property xyz.openbmc_project.State.Host /xyz/openbmc_project/state/host0 xyz.openbmc_project.State.Host CurrentHostState | cut -d"." -f6)
    if [ "$st" == "Running\"" ]; then
        echo "on"
    else
        echo "off"
    fi
}

createFile=$1
setState=$2

if [ $(host_status) == "on" ]; then
    exit 0
fi

# Time out to check S0_FW_BOOT_OK is 60 seconds
cnt=60
val=0
while [ $cnt -gt 0 ];
do
    val=$(gpio_get_val $S0_CPU_FW_BOOT_OK)
    cnt=$((cnt - 1))
    echo "$cnt S0_CPU_FW_BOOT_OK = $val"
    if [ $val == 1 ]; then
        # Sleep 5 second before the host is ready
        sleep 5
        if [ $createFile == 1 ]; then
            if [ ! -d "/run/openbmc" ]; then
                mkdir -p /run/openbmc
            fi
            echo "Creating /run/openbmc/host@0-on"
            touch /run/openbmc/host@0-on
        fi
        exit 0
    fi
    sleep 1
done

exit 1
