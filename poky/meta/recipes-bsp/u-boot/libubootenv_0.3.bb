SUMMARY = "U-Boot libraries and tools to access environment"

DESCRIPTION = "This package contains tools and libraries to read \
and modify U-Boot environment. \
It provides a hardware-independent replacement for fw_printenv/setenv utilities \
provided by U-Boot"

HOMEPAGE = "https://github.com/sbabic/libubootenv"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://Licenses/lgpl-2.1.txt;md5=4fbd65380cdd255951079008b364516c"
SECTION = "libs"

SRC_URI = "git://github.com/sbabic/libubootenv;protocol=https"
SRCREV = "ad253cfdb07c8492f2ee46a52fbc607ad0b96414"

S = "${WORKDIR}/git"

inherit cmake lib_package

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release"

DEPENDS = "zlib"
PROVIDES += "u-boot-fw-utils"
RPROVIDES_${PN}-bin += "u-boot-fw-utils"

PACKAGE_ARCH = "${MACHINE_ARCH}"

RRECOMMENDS_${PN}-bin_append_class-target = " u-boot-default-env"

BBCLASSEXTEND = "native"
