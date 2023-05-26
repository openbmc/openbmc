SUMMARY = "The libirecovery library allows communication with iBoot/iBSS of iOS devices via USB"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "\
    file://COPYING;md5=23c2a5e0106b99d75238986559bb5fc6 \
"

HOMEPAGE = "http://www.libimobiledevice.org/"

DEPENDS = "libimobiledevice-glue libusb1 readline"

PV = "1.1.0"

SRCREV = "98c9f7055ec1f2e09fac69ef1413a8757113b838"
SRC_URI = "git://github.com/libimobiledevice/libirecovery;protocol=https;branch=master"

S = "${WORKDIR}/git"
inherit autotools pkgconfig
