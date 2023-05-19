DESCRIPTION = "Libdivecomputer is a cross-platform and open source library for communication with dive computers from various manufacturers."
HOMEPAGE = "http://www.divesoftware.org/libdc/"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

DEPENDS = "libusb1"

inherit autotools pkgconfig

PV = "0.8.0"

SRCREV = "db9371cf9f38b5a3b2f6e4fae3f92eb052d2d929"
SRC_URI = "git://github.com/libdivecomputer/libdivecomputer.git;protocol=https;branch=master \
          "

S = "${WORKDIR}/git"


