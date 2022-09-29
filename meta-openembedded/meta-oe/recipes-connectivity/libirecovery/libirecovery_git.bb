SUMMARY = "The libirecovery library allows communication with iBoot/iBSS of iOS devices via USB"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libimobiledevice-glue libusb1"

PV = "1.0.1+git${SRCPV}"

SRCREV = "ab5b4d8d4c0e90c05d80f80c7e99a6516de9b5c6"
SRC_URI = "git://github.com/libimobiledevice/libirecovery;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
