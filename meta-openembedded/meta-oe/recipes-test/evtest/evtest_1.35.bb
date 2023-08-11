SUMMARY = "Simple tool for input event debugging"
HOMEPAGE = "http://people.freedesktop.org/~whot/evtest/"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "libxml2"

SRCREV = "da347a8f88d2e5729dd12d61ee9743f902065b55"
SRC_URI = "git://gitlab.freedesktop.org/libevdev/evtest.git;protocol=https;branch=master \
           file://0001-Fix-build-on-32bit-arches-with-64bit-time_t.patch \
           "

S = "${WORKDIR}/git"

inherit autotools pkgconfig
