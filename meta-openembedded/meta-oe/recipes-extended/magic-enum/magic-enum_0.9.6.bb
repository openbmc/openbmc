# SPDX-FileCopyrightText: 2024 Bosch Sicherheitssysteme GmbH
#
# SPDX-License-Identifier: MIT

SUMMARY = "Static reflection for enums"
DESCRIPTION = "Header-only C++17 library provides static reflection for enums, works \
with any enum type without any macro or boilerplate code."
BUGTRACKER = "https://github.com/Neargye/magic_enum/issues"
HOMEPAGE = "https://github.com/Neargye/magic_enum"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7e7717cf723eb72f57e80fdb651cb318"

SRC_URI = " \
    git://github.com/Neargye/magic_enum.git;protocol=https;branch=master \
    file://run-ptest \
"

SRCREV = "dd6a39d0ba1852cf06907e0f0573a2a10d23c2ad"
S = "${WORKDIR}/git"

inherit cmake ptest

EXTRA_OECMAKE = "\
    -DMAGIC_ENUM_OPT_BUILD_EXAMPLES=OFF \
"

do_install_ptest () {
    install -d ${D}${PTEST_PATH}/tests
    install -m 0755 ${B}/test/test_* ${D}${PTEST_PATH}/tests
}

# Add catkin and colcon (ROS build system) support
FILES:${PN}-dev += "\
    ${datadir}/magic_enum/package.xml \
"

# Header-only library
# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS:${PN}-dev = ""
RDEPENDS:${PN}-ptest = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"
