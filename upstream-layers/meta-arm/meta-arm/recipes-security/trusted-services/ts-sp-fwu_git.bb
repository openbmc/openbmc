# SPDX-FileCopyrightText: <text>Copyright 2024 Arm Limited and/or its
# affiliates <open-source-office@arm.com></text>
#
# SPDX-License-Identifier: MIT

DESCRIPTION = "Trusted Services Firmware Update Service provider"

require ts-sp-common.inc
inherit deploy

SP_UUID = "${FWU_UUID}"
TS_SP_FWU_CONFIG ?= "default"

OECMAKE_SOURCEPATH = "${S}/deployments/fwu/config/${TS_SP_FWU_CONFIG}-${TS_ENV}"

# The GPT parser component is needed from TF-A
SRC_URI += "git://git.trustedfirmware.org/TF-A/trusted-firmware-a.git;name=tfa;protocol=https;branch=master;destsuffix=tf-a"
SRCREV_tfa = "35f4c7295bafeb32c8bcbdfb6a3f2e74a57e732b"
LIC_FILES_CHKSUM = "file://../tf-a/docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"
do_apply_local_src_patches:append() {
    apply_local_src_patches ${S}/external/tf_a ${WORKDIR}/sources/tf-a
}

EXTRA_OECMAKE:append = "-DTFA_SOURCE_DIR=${WORKDIR}/sources/tf-a"

# Deploy the secure flash image.
do_deploy() {
    cp -v ${S}/components/media/disk/disk_images/multi_location_fw.img ${DEPLOYDIR}/secure-flash.img
}
addtask deploy after do_compile

EXTRA_OECMAKE:append:qemuall = " -DSEMIHOSTING_BLK_FILE_NAME:STRING=${@oe.path.relative('${TMPDIR}', '${DEPLOY_DIR_IMAGE}')}/secure-flash.img"
