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


TEMPDIRS=()
# Script under test
SUT=$PWD/usb_network.sh

TEST_STATUS="OK"

test_setup() {
    echo -n "Testing $1 ..."
    FAKE_CONFIGFS="$(mktemp -d)"
    TEMPDIRS+=("${FAKE_CONFIGFS}")
    FAKE_GADGETFS="$FAKE_CONFIGFS"/usb_gadget
    mkdir -p "$FAKE_GADGETFS"
}

test_teardown() {
    echo ${TEST_STATUS}
    rm -rf -- "${TEMPDIRS[@]}"
    TEMPDIRS=()
}

test_fail() {
    echo -n " $* " >&2
    TEST_STATUS=FAIL

    test_teardown
    exit 1
}

check_file_content() {
    local filename="$1"
    local expected_content="$2"

    if [[ ! -f ${filename} ]]; then
        test_fail "File ${filename} does not exist!"
    fi

    local actual_content
    actual_content=$(<"${filename}")
    if [[ $expected_content != "$actual_content" ]]; then
        test_fail "Expected ${expected_content}, got ${actual_content}"
    fi
}

test_gadget_creation_with_defaults() {
    local extra_args=()
    local gadget_dir="$1"
    if [[ $gadget_dir == "" ]]; then
        gadget_dir="g1";
    else
        extra_args+=(--gadget-dir-name "${gadget_dir}")
    fi
    local product_name="Souvenier BMC"
    local product_id="0xcafe"
    local host_mac="ab:cd:ef:10:11:12"
    local dev_mac="12:11:10:ef:cd:ab"
    local bind_device="f80002000.udc"
    if ! CONFIGFS_HOME="${FAKE_CONFIGFS}" "${SUT}" --product-id "${product_id}" \
        --product-name "${product_name}" \
        --host-mac "${host_mac}" \
        --dev-mac "${dev_mac}" \
        --bind-device "${bind_device}" \
        "${extra_args[@]}"; then
        test_fail "${SUT} failed"
    fi

    if [[ ! -d ${FAKE_GADGETFS}/${gadget_dir} ]]; then
        test_fail "Gadget was not created!"
    fi

    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/idVendor" "0x18d1"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/idProduct" "${product_id}"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/strings/0x409/manufacturer" "Google"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/strings/0x409/product" "${product_name}"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/MaxPower" "100"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/strings/0x409/configuration" "EEM"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/functions/eem.usb0/dev_addr" "${dev_mac}"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/functions/eem.usb0/host_addr" "${host_mac}"

    if [[ ! -d ${FAKE_GADGETFS}/${gadget_dir}/functions/eem.usb0 ]]; then
        test_fail "Function directory was not created"
    fi

    local func_link="${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/ee.usb0"
    if [[ ! -L ${func_link} ]]; then
        test_fail "Symlink to the function was not created in the config"
    fi

    local link_dest
    link_dest="$(realpath "${func_link}")"
    if [[ $link_dest != "${FAKE_GADGETFS}/${gadget_dir}/functions/eem.usb0" ]]; then
        test_fail "Symlink points to the wrong file/dir"
    fi

    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/UDC" "${bind_device}"
}

test_gadget_creation_with_override() {
    mkdir -p "${FAKE_GADGETFS}"/g1/{strings,configs,functions}
    touch "${FAKE_GADGETFS}"/g1/{idVendor,idProduct}

    test_gadget_creation_with_defaults
}

test_gadget_stopping() {
    local extra_args=()
    local gadget_dir="$1"
    local iface_name="$2"
    if [[ $gadget_dir == "" ]]; then
        gadget_dir="g1";
    else
        extra_args+=(--gadget-dir-name "${gadget_dir}")
    fi

    if [[ $iface_name == "" ]]; then
        iface_name="usb0";
    else
        extra_args+=(--iface-name "${iface_name}")
    fi

    CONFIGFS_HOME=${FAKE_CONFIGFS} ${SUT} "${extra_args[@]}" stop

    if test -d "${FAKE_GADGETFS}/${gadget_dir}"; then
        test_fail "Gadget was not removed!"
    fi
}

test_gadget_creation_no_macs() {
    local gadget_dir="g1";
    local product_name="Souvenier BMC"
    local product_id="0xcafe"
    local bind_device="f80002000.udc"
    CONFIGFS_HOME=${FAKE_CONFIGFS} ${SUT} --product-id "${product_id}" \
        --product-name "${product_name}" \
        --bind-device "${bind_device}"

    if test $? -ne 0; then
        test_fail "${SUT} failed"
    fi

    if ! test -d "${FAKE_GADGETFS}/${gadget_dir}"; then
        test_fail "Gadget was not created!"
    fi

    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/idVendor" "0x18d1"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/idProduct" "${product_id}"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/strings/0x409/manufacturer" "Google"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/strings/0x409/product" "${product_name}"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/MaxPower" "100"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/strings/0x409/configuration" "EEM"

    if [[ -e ${FAKE_GADGETFS}/${gadget_dir}/functions/eem.usb0/dev_addr ]]; then
        test_fail "dev_addr should not be set"
    fi

    if [[ -e ${FAKE_GADGETFS}/${gadget_dir}/functions/eem.usb0/host_addr ]]; then
        test_fail "host_addr should not be set"
    fi

    local func_link="${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/eem.usb0"
    if [[ ! -L ${func_link} ]]; then
        test_fail "Symlink to the function was not created in the config"
    fi

    local link_dest
    link_dest="$(realpath "${func_link}")"
    if [[ $link_dest != ${FAKE_GADGETFS}/${gadget_dir}/functions/eem.usb0 ]]; then
        test_fail "Symlink points to the wrong file/dir"
    fi

    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/UDC" "${bind_device}"
}

test_gadget_creation_alt_iface() {
    local gadget_dir="g1";
    local product_name="Souvenier BMC"
    local product_id="0xcafe"
    local bind_device="f80002000.udc"
    local iface_name="iface0"
    if ! CONFIGFS_HOME=${FAKE_CONFIGFS} ${SUT} --product-id "${product_id}" \
        --product-name "${product_name}" \
        --bind-device "${bind_device}" \
        --iface-name "${iface_name}"; then
        test_fail "${SUT} failed"
    fi

    if [[ ! -d "${FAKE_GADGETFS}/${gadget_dir}" ]]; then
        test_fail "Gadget was not created!"
    fi

    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/idVendor" "0x18d1"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/idProduct" "${product_id}"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/strings/0x409/manufacturer" "Google"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/strings/0x409/product" "${product_name}"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/MaxPower" "100"
    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/strings/0x409/configuration" "EEM"

    if [[ ! -d ${FAKE_GADGETFS}/${gadget_dir}/functions/eem.${iface_name} ]]; then
        test_fail "Function directory was not created"
    fi

    if [[ -e ${FAKE_GADGETFS}/${gadget_dir}/functions/eem.${iface_name}/dev_addr ]]; then
        test_fail "dev_addr should not be set"
    fi

    if [[ -e ${FAKE_GADGETFS}/${gadget_dir}/functions/eem.${iface_name}/host_addr ]]; then
        test_fail "host_addr should not be set"
    fi

    local func_link="${FAKE_GADGETFS}/${gadget_dir}/configs/c.1/eem.${iface_name}"
    if [[ ! -L ${func_link} ]]; then
        test_fail "Symlink to the function was not created in the config"
    fi

    local link_dest
    link_dest="$(realpath "${func_link}")"
    if [[ $link_dest != "${FAKE_GADGETFS}/${gadget_dir}/functions/eem.${iface_name}" ]]; then
        test_fail "Symlink points to the wrong file/dir"
    fi

    check_file_content "${FAKE_GADGETFS}/${gadget_dir}/UDC" "${bind_device}"
}


# -------------------------------------------------------------------
test_setup "Device Creation"

test_gadget_creation_with_defaults

test_teardown
# -------------------------------------------------------------------

# -------------------------------------------------------------------
test_setup "Device Creation With Override"

test_gadget_creation_with_override

test_teardown
# -------------------------------------------------------------------

# -------------------------------------------------------------------
test_setup "Test Device Stop"

test_gadget_creation_with_defaults
test_gadget_stopping

test_teardown
# -------------------------------------------------------------------

# -------------------------------------------------------------------
test_setup "Device Creation/Stopping, Alternative Name"

test_gadget_creation_with_defaults "gAlt"
test_gadget_stopping "gAlt"

test_teardown
# -------------------------------------------------------------------

# -------------------------------------------------------------------
test_setup "Device Creation without MAC Addrs"

test_gadget_creation_no_macs

test_teardown
# -------------------------------------------------------------------

# -------------------------------------------------------------------
test_setup "Device Creation/Stopping, Alternative Interface"

test_gadget_creation_alt_iface

test_teardown
# -------------------------------------------------------------------

echo "SUCCESS!"
