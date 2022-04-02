SUMMARY = "The libirecovery library allows communication with iBoot/iBSS of iOS devices via USB"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libimobiledevice-glue libusb1"

PV = "1.0.1+git${SRCPV}"

SRCREV = "e19094594552b7bed60418ffe6f46f455e6bb78f"
SRC_URI = "git://github.com/libimobiledevice/libirecovery;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
