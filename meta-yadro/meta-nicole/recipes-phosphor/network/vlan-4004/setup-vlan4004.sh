#!/bin/sh -eu

VLAN='VLAN=eth0.4004'
ETH0_CFG='/etc/systemd/network/00-bmc-eth0.network'

if ( ! grep -q "${VLAN}" ${ETH0_CFG} ); then
    sed -i "/^\[Network\]$/a ${VLAN}" ${ETH0_CFG}
fi
