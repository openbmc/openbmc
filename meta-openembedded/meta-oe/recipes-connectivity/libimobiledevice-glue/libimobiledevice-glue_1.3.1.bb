SUMMARY = "Library with common code used by the libraries and tools around the libimobiledevice project"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=6ab17b41640564434dda85c06b7124f7 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libplist"

SRCREV = "a4ec1cf9cc7084cbe5571e31640ef773701e5f51"
SRC_URI = "git://github.com/libimobiledevice/libimobiledevice-glue;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
