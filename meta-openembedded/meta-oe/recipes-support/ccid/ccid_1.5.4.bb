SUMMARY = "Generic USB CCID smart card reader driver"
HOMEPAGE = "https://ccid.apdu.fr/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "autoconf-archive-native virtual/libusb0 pcsc-lite"
RDEPENDS:${PN} = "pcsc-lite"

SRC_URI = "https://ccid.apdu.fr/files/ccid-${PV}.tar.bz2 \
    file://0001-Add-build-rule-for-README.patch \
"

SRC_URI[sha256sum] = "6e832adc172ecdcfdee2b56f33144684882cbe972daff1938e7a9c73a64f88bf"

inherit autotools pkgconfig

FILES:${PN} += "${libdir}/pcsc/"
FILES:${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
