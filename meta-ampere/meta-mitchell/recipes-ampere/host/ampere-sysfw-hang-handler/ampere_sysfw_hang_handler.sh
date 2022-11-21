#!/bin/bash

# shellcheck disable=SC2046
# shellcheck source=/dev/null

source /usr/sbin/gpio-lib.sh

# Do event trigger
function sel_trigger()
{
	echo "Error: system firmware hang, trigger sel"
	ampere_add_redfishevent.sh OpenBMC.0.1.SystemPowerOnFailed.Critical
}

# Do reset the system
function reset_system()
{
	echo "Error: system firmware hang, reset the system"
	ipmitool chassis power reset
}

s0_last_hb_state=0
cnt=-1
while true
do
    # Monitor heart beat GPIO value, GPIOF4 for Socket 0
    s0_hb_state=$(gpio_name_get s0-heartbeat)
    if [ "$s0_last_hb_state" != "$s0_hb_state" ]; then
        cnt=0
    else
        cnt=$((cnt + 1))
    fi

    if [ "$cnt" -ge 6 ]; then
        echo "Error: system firmware hang"
        sel_trigger
        reset_system
        exit 0
    fi
    s0_last_hb_state="$s0_hb_state"
    sleep 0.5
done

exit 0
