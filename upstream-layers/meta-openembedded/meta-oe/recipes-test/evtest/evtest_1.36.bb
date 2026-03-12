SUMMARY = "Simple tool for input event debugging"
HOMEPAGE = "http://people.freedesktop.org/~whot/evtest/"
SECTION = "console/utils"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "libxml2"

SRCREV = "96e6cb5ae90726883be095a3e467e847c06225f7"
SRC_URI = "git://gitlab.freedesktop.org/libevdev/evtest.git;protocol=https;branch=master \
           file://0001-Fix-build-on-32bit-arches-with-64bit-time_t.patch \
           "


inherit autotools pkgconfig
