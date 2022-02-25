SUMMARY = "U-Boot libraries and tools to access environment"

DESCRIPTION = "This package contains tools and libraries to read \
and modify U-Boot environment. \
It provides a hardware-independent replacement for fw_printenv/setenv utilities \
provided by U-Boot"

HOMEPAGE = "https://github.com/sbabic/libubootenv"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://Licenses/lgpl-2.1.txt;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"

SRC_URI = "git://github.com/sbabic/libubootenv;protocol=https;branch=master"
SRCREV = "ba7564f5006d09bec51058cf4f5ac90d4dc18b3c"

S = "${WORKDIR}/git"

inherit cmake lib_package

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"

DEPENDS = "zlib"
PROVIDES += "u-boot-fw-utils"
RPROVIDES:${PN}-bin += "u-boot-fw-utils"

BBCLASSEXTEND = "native"
