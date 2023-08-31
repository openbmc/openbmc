DESCRIPTION = "CPLD/FPGA Programmer"
PR = "r1"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/Nuvoton-Israel/loadsvf.git;branch=master;protocol=https"

SRCREV = "ffe63f714380ce99047daaa357fdd1876d95901b"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"
