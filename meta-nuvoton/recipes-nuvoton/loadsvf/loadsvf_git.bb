DESCRIPTION = "CPLD/FPGA Programmer"
PR = "r1"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/Nuvoton-Israel/loadsvf.git;branch=master;protocol=https"

SRCREV = "013cc80447e7b2f473f0d43cc96232aba8ac98c8"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"
