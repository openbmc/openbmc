#!/bin/sh

# if eth0 is configured DHCP and the interface is up (has carrier) and there is
# no IP address after 2 mins, restart the interface to try again

LOG_TAG=dhcp-check
CHK_DELAY=90
CFG_DELAY=60
IFC=eth0
NETWORK_CONFIG=/etc/systemd/network/00-bmc-${IFC}.network

carrier=$(cat /sys/class/net/${IFC}/carrier 2>/dev/null)
if [[ "$carrier" == "1" ]]; then
    dhcp=$(egrep -c "DHCP=(true|ipv4)" $NETWORK_CONFIG)
    if [[ "$dhcp" != "0" ]]; then
        # Give time for DHCP address to be configured
        sleep $CHK_DELAY

        hasip=$(ip addr show dev ${IFC} | grep -c dynamic)
        if [[ "$hasip" == "0" ]]; then
            logger -t $LOG_TAG \
                "$IFC: No DHCP address after ${CHK_DELAY}s; reconfigure"
            ethtool -r ${IFC}

            # give time for network to come up and acquire DHCP address
            sleep $CFG_DELAY
        fi

        IP=$(ip addr show dev ${IFC} | sed -n '/dynamic/s/^.*inet \([0-9\.]*\).*$/\1/p')
        logger -t $LOG_TAG "$IFC: DHCP address $IP"
    fi
fi

exit 0