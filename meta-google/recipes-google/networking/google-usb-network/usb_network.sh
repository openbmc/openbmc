#!/bin/bash
# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# List of options the script accepts. Trailing column means that the option
# requires an argument.
ARGUMENT_LIST=(
    "help"
    "product-id:"
    "product-name:"
    "host-mac:"
    "bind-device:"
    "dev-mac:"
    "gadget-dir-name:"
    "iface-name:"
)

print_usage() {
    cat <<HELP
$0 [OPTIONS] [stop|start]
    Create USB Gadget Configuration
        --product-id USB Product Id for the gadget.
        --product-name Product name string (en) for the gadget.
        --host-mac MAC address of the host part of the connection. Optional.
        --dev-mac MAC address of the device (gadget) part of the connection. Optional.
        --bind-device Name of the device to bind, as listed in /sys/class/udc/
        --gadget-dir-name Optional base name for gadget directory. Default: "g1"
        --iface-name Optional name of the network interface. Default: "usb0"
        --help  Print this help and exit.
HELP
}

gadget_start() {
    local gadget_dir="${CONFIGFS_HOME}/usb_gadget/${GADGET_DIR_NAME}"
    mkdir -p "${gadget_dir}"
    echo ${ID_VENDOR} > "${gadget_dir}/idVendor"
    echo ${ID_PRODUCT} > "${gadget_dir}/idProduct"

    local str_en_dir="${gadget_dir}/strings/0x409"
    mkdir -p "${str_en_dir}"
    echo ${STR_EN_VENDOR} > "${str_en_dir}/manufacturer"
    echo ${STR_EN_PRODUCT} > "${str_en_dir}/product"

    local config_dir="${gadget_dir}/configs/c.1"
    mkdir -p "${config_dir}"
    echo 100 > "${config_dir}/MaxPower"
    mkdir -p "${config_dir}/strings/0x409"
    echo "ECM" > "${config_dir}/strings/0x409/configuration"

    local func_dir="${gadget_dir}/functions/ecm.${IFACE_NAME}"
    mkdir -p "${func_dir}"

    if [[ -n $HOST_MAC_ADDR ]]; then
        echo ${HOST_MAC_ADDR} > ${func_dir}/host_addr
    fi

    if [[ -n $DEV_MAC_ADDR ]]; then
        echo ${DEV_MAC_ADDR} > ${func_dir}/dev_addr
    fi

    ln -s "${func_dir}" "${config_dir}"

    echo "${BIND_DEVICE}" > ${gadget_dir}/UDC
}

gadget_stop() {
    local gadget_dir="${CONFIGFS_HOME}/usb_gadget/${GADGET_DIR_NAME}"
    rm -f ${gadget_dir}/configs/c.1/ecm.${IFACE_NAME}
    rm -rf ${gadget_dir}/configs/c.1/strings/0x409
    rm -rf ${gadget_dir}/configs/c.1
    rm -rf ${gadget_dir}/strings/0x409
    rm -rf ${gadget_dir}/functions/ecm.${IFACE_NAME}
    rm -rf ${gadget_dir}
}

opts=$(getopt \
    --longoptions "$(printf "%s," "${ARGUMENT_LIST[@]}")" \
    --name "$(basename "$0")" \
    --options "" \
    -- "$@"
)

eval set --$opts

CONFIGFS_HOME=${CONFIGFS_HOME:-/sys/kernel/config}
ID_VENDOR="0x18d1" # Google
ID_PRODUCT=""
STR_EN_VENDOR="Google"
STR_EN_PRODUCT=""
DEV_MAC_ADDR=""
HOST_MAC_ADDR=""
BIND_DEVICE=""
ACTION="start"
GADGET_DIR_NAME="g1"
IFACE_NAME="usb0"
while [[ $# -gt 0 ]]; do
    case "$1" in
        --product-id)
            ID_PRODUCT=$2
            shift 2
            ;;
        --product-name)
            STR_EN_PRODUCT=$2
            shift 2
            ;;
        --host-mac)
            HOST_MAC_ADDR=$2
            shift 2
            ;;
        --dev-mac)
            DEV_MAC_ADDR=$2
            shift 2
            ;;
        --bind-device)
            BIND_DEVICE=$2
            shift 2
            ;;
        --gadget-dir-name)
            GADGET_DIR_NAME=$2
            shift 2
            ;;
        --iface-name)
            IFACE_NAME=$2
            shift 2
            ;;
        --help)
            print_usage
            exit 0
            ;;
        start)
            ACTION="start"
            shift 1
            break
            ;;
        stop)
            ACTION="stop"
            shift 1
            break
            ;;
        --)
            shift 1
            ;;
        *)
            break
            ;;
    esac
done

if [[ $ACTION == "stop" ]]; then
    gadget_stop
else
    gadget_start
fi
