SUMMARY = "Library with common code used by the libraries and tools around the libimobiledevice project"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=6ab17b41640564434dda85c06b7124f7 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libplist"

PV = "1.0.0+git${SRCPV}"

SRCREV = "bc6c44b92091c9587a9bed0ed3f2c3248bfd13b3"
SRC_URI = "git://github.com/libimobiledevice/libimobiledevice-glue;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
