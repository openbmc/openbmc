SUMMARY = "Generic USB CCID smart card reader driver"
HOMEPAGE = "https://ccid.apdu.fr/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "autoconf-archive-native virtual/libusb0 pcsc-lite"
RDEPENDS:${PN} = "pcsc-lite"

SRC_URI = "https://ccid.apdu.fr/files/ccid-${PV}.tar.bz2 \
    file://0001-Add-build-rule-for-README.patch \
"

SRC_URI[sha256sum] = "13934487e6f8b48f699a16d367cc7a1af7a3ca874de721ac6e9633beb86e7219"

inherit autotools pkgconfig

FILES:${PN} += "${libdir}/pcsc/"
FILES:${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
