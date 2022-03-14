DESCRIPTION = "Libdivecomputer is a cross-platform and open source library for communication with dive computers from various manufacturers."
HOMEPAGE = "http://www.divesoftware.org/libdc/"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

DEPENDS = "libusb1"

inherit autotools pkgconfig

PV = "0.7.0"

SRCREV = "47cbed5355ffda7b952193a770a9a9fa9f89b25b"
SRC_URI = "git://github.com/libdivecomputer/libdivecomputer.git;protocol=https;branch=master \
          "

S = "${WORKDIR}/git"


