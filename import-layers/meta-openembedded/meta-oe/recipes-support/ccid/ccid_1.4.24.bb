SUMMARY = "Generic USB CCID smart card reader driver"
HOMEPAGE = "http://pcsclite.alioth.debian.org/ccid.html"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "virtual/libusb0 pcsc-lite"
RDEPENDS_${PN} = "pcsc-lite"

SRC_URI = "https://alioth.debian.org/frs/download.php/file/4171/ccid-${PV}.tar.bz2 \
    file://no-dep-on-libfl.patch \
"

SRC_URI[md5sum] = "915a03cda85b60fefbe3654cbdc68ca9"
SRC_URI[sha256sum] = "62cb73c6c009c9799c526f05a05e25f00f0ad86d50f82a714dedcfbf4a7e4176"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/pcsc/"
FILES_${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
