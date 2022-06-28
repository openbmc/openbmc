#!/bin/bash -eu

# SPDX-License-Identifier: Apache-2.0
# Copyright 2020-2021 YADRO

log_msg() {
    echo "$@"
}

log_err() {
    echo "$@" >&2
}

read_hw_mac() {
    local iface="$1"
    cat /sys/class/net/"${iface}"/address 2>/dev/null ||:
}

set_hw_mac() {
    local iface="$1"
    local mac="$2"
    local up=""
    if ip link show dev "${iface}" | grep -q "${iface}:.*\<UP\>" 2>/dev/null; then
        up=true
    fi

    if [ "${up}" = true ]; then
        ip link set dev "${iface}" down ||:
    fi
    ip link set dev "${iface}" address "${mac}" ||:
    if [ "${up}" = true ]; then
        ip link set dev "${iface}" up ||:
    fi
}

set_fw_env_mac() {
    local iface="$1"
    local mac="$2"
    local envname=""
    case "${iface}" in
        eth0)
            envname="ethaddr"
        ;;
        eth1)
            envname="eth1addr"
        ;;
        *)
            return 1
        ;;
    esac
    if ! fw_setenv "$envname" "$mac"; then
        return 1
    fi
}

######## MAIN ########
FRU_DBUS_SERVICE="xyz.openbmc_project.FruDevice"
FRU_DBUS_OBJECT_TEMPLATE="\/xyz.*_Motherboard"
FRU_DBUS_INTERFACE="xyz.openbmc_project.FruDevice"
fru_dbus_object=""

######## Parse D-bus data ########
n=1
while true; do
    fru_dbus_object=$(busctl tree ${FRU_DBUS_SERVICE} 2>/dev/null |
        sed -n "s/^.*\(${FRU_DBUS_OBJECT_TEMPLATE}\).*$/\1/p"
      ) && [ -n "${fru_dbus_object}" ] && break
    if [ ${n} -lt 15 ]; then
        n=$((n+1))
        sleep 1
    else
        log_err "Failed to find baseboard FRU object"
        exit 1
    fi
done

dbusData=$(dbus-send --system --print-reply=literal \
            --dest=${FRU_DBUS_SERVICE} \
            "${fru_dbus_object}" \
            org.freedesktop.DBus.Properties.GetAll \
            string:${FRU_DBUS_INTERFACE} 2>/dev/null ||:)

if [ -z "${dbusData}" ]; then
    log_err "Failed to get data from D-Bus"
    exit 1
fi

# This awk script matches strings 010xyyyyyyyyyyyy, where x is interface index
# and yyyyyyyyyyyy - interface mac address.
# The output would be in form ethx=yy:yy:yy:yy:yy:yy
macsList=$(echo "${dbusData}" | \
    awk '/BOARD_INFO_AM[0-9]+\s+variant\s+010[0-9a-f]{13}/{
        totalMacDigits=12
        singleOctet=2
        offset=4
        printf "eth%s=", substr($3, offset, 1)
        for(i = 1 + offset; i < totalMacDigits + offset; i += singleOctet) {
            printf "%s", substr($3, i, singleOctet)
            if (i <= totalMacDigits + offset - singleOctet) printf ":"
        }
        printf "\n"
    }'||:)

if [ -z "${macsList}" ]; then
    log_err "Failed to get MAC address"
    exit 1
fi

# Get hardware model name from PRODUCT_PRODUCT_NAME
# Examples:
#  'VEGMAN N110 Server' -> 'VEGMAN-N110'
#  'TATLIN.ARCHIVE.XS' -> 'TATLIN-ARCHIVE-XS'
modelName=$(echo "${dbusData}" | \
        awk '/PRODUCT_PRODUCT_NAME\s+variant\s+/{
                if ($3 ~ /^VEGMAN/) {
                    printf "%s-%s", $3, $4
                } else if ($3 ~ /^TATLIN/) {
                    print gensub(/\./, "-", "g", $3)
                }
             }'||:)

serialNumber=$(echo "${dbusData}" | \
     awk '/PRODUCT_SERIAL_NUMBER\s+variant\s+/{print $3}'||:)
if [ -z "${serialNumber}" ]; then
    log_err "Failed to get product Serial Number"
    exit 1
fi

# shellcheck disable=SC1091
source /etc/os-release


######## Check and set MAC addresses ########
retCode=0
IFS='
'
for line in ${macsList} ; do
    ifaceName=$(echo "${line}" | cut -f 1 -d '=' || :)
    macAddr=$(echo "${line}" | cut -f 2- -d '=' || :)
    if [ -n "${ifaceName}" ] && [ -n "${macAddr}" ]; then
        curMacAddr=$(read_hw_mac "${ifaceName}")
        if [ "${macAddr}" != "${curMacAddr}" ] ; then
            log_msg "Changing MAC address for ${ifaceName}: ${curMacAddr} -> ${macAddr}"
            # A factory assigned address was found, and it is newly assigned.
            # Update the active interface and save the new value to the u-boot
            # environment.
            if ! set_hw_mac "${ifaceName}" "${macAddr}"; then
                log_err "Failed to set address"
                retCode=1
            fi
            if ! set_fw_env_mac "${ifaceName}" "${macAddr}"; then
                log_err "Failed to set boot env for ${ifaceName}"
                retCode=1
            fi
        fi
    fi

    [ ${retCode} -eq 0 ] || exit ${retCode}
done


######## Check and set hostname ########
hostname=$(hostname)
if [ "${OPENBMC_TARGET_MACHINE}" = "${hostname}" ] ; then
    hostname="${modelName,,}-${serialNumber}"
    log_msg "Changing hostname to ${hostname}"

    if ! hostnamectl set-hostname "${hostname}"; then
        log_err "Failed to set new hostname"
        exit 1
    fi
fi

######## Run optional scripts ########
snmp_handler="/usr/sbin/snmpd-generate-conf.sh"
if [ -x ${snmp_handler} ]; then
    if ! ${snmp_handler} "${modelName}"; then
        log_err "Failed to setup snmp"
    fi
fi
