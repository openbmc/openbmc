SUMMARY = "Generic USB CCID smart card reader driver"
HOMEPAGE = "https://ccid.apdu.fr/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "virtual/libusb0 pcsc-lite"
RDEPENDS_${PN} = "pcsc-lite"

SRC_URI = "https://ccid.apdu.fr/files/ccid-${PV}.tar.bz2 \
    file://no-dep-on-libfl.patch \
    file://0001-Add-build-rule-for-README.patch \
"

SRC_URI[md5sum] = "c4c70e928455f6ac3c4d02c4d2a654e9"
SRC_URI[sha256sum] = "545f4ab7887d512aa4b6967b80ef18a77b790c34769718452737a633cefc1639"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/pcsc/"
FILES_${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
