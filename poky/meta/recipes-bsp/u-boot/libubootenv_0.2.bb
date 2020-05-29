SUMMARY = "U-Boot libraries and tools to access environment"
DEPENDS += "mtd-utils zlib"

DESCRIPTION = "This package contains tools and libraries to read \
and modify U-Boot environment. \
It provides a hardware-independent replacement for fw_printenv/setenv utilities \
provided by U-Boot"

HOMEPAGE = "https://github.com/sbabic/libubootenv"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://Licenses/lgpl-2.1.txt;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"

PV = "0.2+git${SRCPV}"
SRC_URI = "git://github.com/sbabic/libubootenv;protocol=https"
SRCREV = "f4b9cde3815abe84a98079cedd515283ea08c16b"

S = "${WORKDIR}/git"

inherit cmake lib_package

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"

PROVIDES += "u-boot-fw-utils"
RPROVIDES_${PN}-bin += "u-boot-fw-utils"

BBCLASSEXTEND = "native"
