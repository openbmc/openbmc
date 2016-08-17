DESCRIPTION = "Libdivecomputer is a cross-platform and open source library for communication with dive computers from various manufacturers."
HOMEPAGE = "http://www.divesoftware.org/libdc/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

DEPENDS = "libusb1"

inherit autotools pkgconfig

PV = "0.4.2+gitr${SRCPV}"

SRCREV = "5f765f91430f16932d96b3777404420aa2dd4c7c"
SRC_URI = "git://git.libdivecomputer.org/libdivecomputer.git \
           file://fix-ar.patch \
          "

S = "${WORKDIR}/git"


