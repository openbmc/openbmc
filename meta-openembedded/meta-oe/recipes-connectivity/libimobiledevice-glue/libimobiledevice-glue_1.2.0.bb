SUMMARY = "Library with common code used by the libraries and tools around the libimobiledevice project"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=6ab17b41640564434dda85c06b7124f7 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libplist"

SRCREV = "fde8946a3988790fd5d3f01fc0a1fd43609ab1d1"
SRC_URI = "git://github.com/libimobiledevice/libimobiledevice-glue;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
