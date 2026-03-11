#
# Copyright OpenEmbedded Contributors
#
# SPDX-License-Identifier: MIT
#

##################################################################
# Specific image creation and rootfs population info.
##################################################################

IMAGE_BASENAME ?= "${PN}"
IMAGE_VERSION_SUFFIX ?= "-${DATETIME}"
IMAGE_VERSION_SUFFIX[vardepsexclude] += "DATETIME SOURCE_DATE_EPOCH"
IMAGE_NAME ?= "${IMAGE_LINK_NAME}${IMAGE_VERSION_SUFFIX}"
IMAGE_LINK_NAME ?= "${IMAGE_BASENAME}${IMAGE_MACHINE_SUFFIX}${IMAGE_NAME_SUFFIX}"

# This needs to stay in sync with IMAGE_LINK_NAME, but with INITRAMFS_IMAGE instead of IMAGE_BASENAME
# and without ${IMAGE_NAME_SUFFIX} which all initramfs images should set to empty
INITRAMFS_IMAGE_NAME ?= "${@['${INITRAMFS_IMAGE}${IMAGE_MACHINE_SUFFIX}', ''][d.getVar('INITRAMFS_IMAGE') == '']}"

# The default DEPLOY_DIR_IMAGE is ${MACHINE} directory:
# meta/conf/bitbake.conf:DEPLOY_DIR_IMAGE ?= "${DEPLOY_DIR}/images/${MACHINE}"
# so many people find it unnecessary to include this suffix to every image
# stored there, but other people often fetch various images for different
# MACHINEs to the same downloads directory and then the suffix is very helpful
# add separate variable for projects to decide which scheme works best for them
# without understanding the IMAGE_NAME/IMAGE_LINK_NAME structure.
IMAGE_MACHINE_SUFFIX ??= "-${MACHINE}"

# IMAGE_NAME is the base name for everything produced when building images.
# The actual image that contains the rootfs has an additional suffix (.rootfs
# by default) followed by additional suffices which describe the format (.ext4,
# .ext4.xz, etc.).
IMAGE_NAME_SUFFIX ??= ".rootfs"

python () {
    if bb.data.inherits_class('deploy', d) and d.getVar("IMAGE_VERSION_SUFFIX") == "-${DATETIME}":
        import datetime
        d.setVar("IMAGE_VERSION_SUFFIX", "-" + datetime.datetime.fromtimestamp(int(d.getVar("SOURCE_DATE_EPOCH")), datetime.timezone.utc).strftime('%Y%m%d%H%M%S'))
        d.setVarFlag("IMAGE_VERSION_SUFFIX", "vardepvalue", "")
}
