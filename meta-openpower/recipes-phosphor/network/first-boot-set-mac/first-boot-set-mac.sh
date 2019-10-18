#!/bin/sh -eu

show_error() {
    if [ -n "${JOURNAL_STREAM-}" ]; then
        echo "$@" | systemd-cat -t first-boot-set-mac -p emerg
    else
        echo "$@" >&2
    fi
}

sync_mac() {

    MAPPER_IFACE='xyz.openbmc_project.ObjectMapper'
    MAPPER_PATH='/xyz/openbmc_project/object_mapper'
    INVENTORY_PATH='/xyz/openbmc_project/inventory'
    NETWORK_ITEM_IFACE='xyz.openbmc_project.Inventory.Item.NetworkInterface'

    # Get the NETWORK ITEM count
    NETWORK_ITEM_PATH_COUNT=$(busctl --no-pager --verbose call \
                            ${MAPPER_IFACE} \
                            ${MAPPER_PATH} \
                            ${MAPPER_IFACE} \
                            GetSubTree sias \
                            ${INVENTORY_PATH} 0 1 ${NETWORK_ITEM_IFACE} \
                        2>/dev/null | grep ${INVENTORY_PATH} | wc -l || true)

    if [ $NETWORK_ITEM_PATH_COUNT -gt 1 ]; then
        # If there are more than 2 NETOWRK ITEM and path must contain $1
        # for finding the right NETWORK ITEM
        NETWORK_ITEM_PATH=$(busctl --no-pager --verbose call \
                            ${MAPPER_IFACE} \
                            ${MAPPER_PATH} \
                            ${MAPPER_IFACE} \
                            GetSubTree sias \
                            ${INVENTORY_PATH} 0 1 ${NETWORK_ITEM_IFACE} \
                        2>/dev/null | grep ${INVENTORY_PATH} | grep $1 || true)
    else
        NETWORK_ITEM_PATH=$(busctl --no-pager --verbose call \
                            ${MAPPER_IFACE} \
                            ${MAPPER_PATH} \
                            ${MAPPER_IFACE} \
                            GetSubTree sias \
                            ${INVENTORY_PATH} 0 1 ${NETWORK_ITEM_IFACE} \
                        2>/dev/null | grep ${INVENTORY_PATH} || true)
    fi

    # '     STRING "/xyz/openbmc_project/inventory/system/chassis/ethernet";'
    NETWORK_ITEM_PATH=${NETWORK_ITEM_PATH#*\"}
    NETWORK_ITEM_PATH=${NETWORK_ITEM_PATH%\"*}

    NETWORK_ITEM_SERVICE=$(mapper get-service \
                                ${NETWORK_ITEM_PATH} 2>/dev/null || true)

    if [[ -z "${NETWORK_ITEM_SERVICE}" ]]; then
        show_error 'No Ethernet interface found in the Inventory. Is VPD EEPROM empty?'
        return
    fi

    MAC_ADDR=$(busctl get-property ${NETWORK_ITEM_SERVICE} \
                                ${NETWORK_ITEM_PATH} \
                                ${NETWORK_ITEM_IFACE} MACAddress)

    # 's "54:52:01:02:03:04"'
    MAC_ADDR=${MAC_ADDR#*\"}
    MAC_ADDR=${MAC_ADDR%\"*}

    if [[ -n "${MAC_ADDR}" ]]; then
        busctl set-property xyz.openbmc_project.Network \
                            /xyz/openbmc_project/network/$1 \
                            xyz.openbmc_project.Network.MACAddress \
                            MACAddress s ${MAC_ADDR}
    fi
}

if [ $# -eq 0 ]; then
    show_error 'No Ethernet interface name is given'
    exit 1
fi

sync_mac $1

# Prevent start at next boot time
mkdir -p "/var/lib/first-boot-set-mac"
touch "/var/lib/first-boot-set-mac/${1}"
