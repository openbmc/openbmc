SUMMARY = "Library with common code used by the libraries and tools around the libimobiledevice project"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=6ab17b41640564434dda85c06b7124f7 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libplist"

PV = "1.0.0+git${SRCPV}"

SRCREV = "ecb0996fd2a3b0539153dd3ef901d137bf498ffe"
SRC_URI = "\
    git://github.com/libimobiledevice/libimobiledevice-glue;protocol=https;branch=master \
    file://0001-fix-undefined-bswap32-and-bswap64-errors-for-MIPS-ma.patch \
"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
