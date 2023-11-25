SUMMARY = "U-Boot libraries and tools to access environment"

DESCRIPTION = "This package contains tools and libraries to read \
and modify U-Boot environment. \
It provides a hardware-independent replacement for fw_printenv/setenv utilities \
provided by U-Boot"

HOMEPAGE = "https://github.com/sbabic/libubootenv"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSES/LGPL-2.1-or-later.txt;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"

SRC_URI = "git://github.com/sbabic/libubootenv;protocol=https;branch=master"
SRCREV = "3f4d15e36ceb58085b08dd13f3f2788e9299877b"

S = "${WORKDIR}/git"

inherit cmake lib_package

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"

DEPENDS = "zlib libyaml"
PROVIDES += "u-boot-fw-utils"
RPROVIDES:${PN}-bin += "u-boot-fw-utils"

BBCLASSEXTEND = "native"
