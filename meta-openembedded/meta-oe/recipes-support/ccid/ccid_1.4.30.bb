SUMMARY = "Generic USB CCID smart card reader driver"
HOMEPAGE = "https://ccid.apdu.fr/"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "virtual/libusb0 pcsc-lite"
RDEPENDS_${PN} = "pcsc-lite"

SRC_URI = "https://ccid.apdu.fr/files/ccid-${PV}.tar.bz2 \
    file://no-dep-on-libfl.patch \
"

SRC_URI[md5sum] = "817abceef715e02cb47617621fe192e0"
SRC_URI[sha256sum] = "ac17087be08880a0cdf99a8a2799a4ef004dc6ffa08b4d9b0ad995f39a53ff7c"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/pcsc/"
FILES_${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
