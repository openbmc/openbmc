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

SRC_URI[md5sum] = "b11907894ce2d345439635e2b967e7e5"
SRC_URI[sha256sum] = "5256da939711deb42b74d05d2bd6bd0c73c4d564feb0c1a50212609eb680e424"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/pcsc/"
FILES_${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
