DESCRIPTION = "Libdivecomputer is a cross-platform and open source library for communication with dive computers from various manufacturers."
HOMEPAGE = "http://www.divesoftware.org/libdc/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

DEPENDS = "libusb1"

inherit autotools pkgconfig

PV = "0.6.0"

SRCREV = "1195abc2f4acc7b10175d570ec73549d0938c83e"
SRC_URI = "git://github.com/libdivecomputer/libdivecomputer.git;protocol=https \
          "

S = "${WORKDIR}/git"


