# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: Apache-2.0

HOMEPAGE = "https://booting.oniroproject.org/distro/components/pim435"
SUMMARY = "A userspace driver application for PIM435 written in C"
DESCRIPTION = "A userspace driver application for PIM435 (Pimoroni LED matrix) \
written in C"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663"

SRC_URI = "git://booting.oniroproject.org/distro/components/pim435;protocol=https;branch=main"
SRCREV = "ee07a83de4d0ecdf4b5de20a7e374d36a9a6f5d5"
S = "${WORKDIR}/git"

DEPENDS = "i2c-tools"

EXTRA_OEMAKE += "DESTDIR=${D}"

do_install() {
    oe_runmake install
}
