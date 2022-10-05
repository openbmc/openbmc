DESCRIPTION = "PSU Firmware Updater"
PR = "r1"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/Nuvoton-Israel/update-psu.git;branch=master;protocol=https"
SRCREV = "8d35a5e840232700ed39f1b1f97373161bc630b4"
S = "${WORKDIR}/git"

inherit autotools pkgconfig

DEPENDS += "autoconf-archive-native"

