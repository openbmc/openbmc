# SPDX-License-Identifier: MIT
#
# Copyright Leica Geosystems AG
#

SUMMARY = "C++ library for kernel mode setting"
HOMEPAGE = "https://github.com/tomba/kmsxx"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

BRANCH = "master"
SRC_URI = "git://github.com/tomba/kmsxx.git;protocol=https;branch=${BRANCH}"
SRCREV = "412935a47b762c33e54a464243f2d789b065bbb6"
PACKAGES =+ "${PN}-python"

PACKAGECONFIG ?= "utils python "
PACKAGECONFIG[omap] += "-Domap=enabled, -Domap=disabled"
PACKAGECONFIG[python] += "-Dpykms=enabled, -Dpykms=disabled, python3 python3-pybind11"
PACKAGECONFIG[utils] += "-Dutils=true, -Dutils=false"

DEPENDS += "libdrm libevdev fmt"

S = "${WORKDIR}/git"

inherit meson pkgconfig

do_install:append() {
    if ${@bb.utils.contains('PACKAGECONFIG', 'utils', 'true', 'false', d)}; then
        # kmstest already provided by libdrm-tests
        mv ${D}${bindir}/kmstest ${D}${bindir}/kmsxxtest
    fi
}

FILES:${PN} ="${bindir} ${libdir}"
FILES:${PN}-python += "${PYTHON_SITEPACKAGES_DIR}/*"
