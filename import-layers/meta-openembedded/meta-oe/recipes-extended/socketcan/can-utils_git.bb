SUMMARY = "Linux CAN network development utilities"
DESCRIPTION = "Linux CAN network development"
LICENSE = "GPLv2 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://include/linux/can.h;endline=43;md5=390a2c9a3c5e3595a069ac1436553ee7"

DEPENDS = "libsocketcan"

SRC_URI = "git://github.com/linux-can/${BPN}.git;protocol=git;branch=master"
SRCREV = "4c8fb05cb4d6ddcd67299008db54af423f86fd05"

PV = "0.0+gitr${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

