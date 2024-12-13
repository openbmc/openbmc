# SPDX-FileCopyrightText: Huawei Inc.
#
# SPDX-License-Identifier: Apache-2.0

HOMEPAGE = "https://booting.oniroproject.org/distro/components/pim435"
SUMMARY = "A userspace driver application for PIM435 written in C"
DESCRIPTION = "A userspace driver application for PIM435 (Pimoroni LED matrix) \
written in C"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSES/MIT.txt;md5=7dda4e90ded66ab88b86f76169f28663"

SRC_URI = "git://gitlab.eclipse.org/eclipse/oniro-blueprints/core/pim435;protocol=https;branch=main"
SRCREV = "445ed623ec8d3ecbb1d566900b4ef3fb3031d689"

# Upstream repo does not tag
UPSTREAM_CHECK_COMMITS = "1"

S = "${WORKDIR}/git"

DEPENDS = "i2c-tools"

EXTRA_OEMAKE += "DESTDIR=${D}"

do_install() {
    oe_runmake install
}
