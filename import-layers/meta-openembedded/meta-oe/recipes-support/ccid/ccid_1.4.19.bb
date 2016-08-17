SUMMARY = "Generic USB CCID smart card reader driver"
HOMEPAGE = "http://pcsclite.alioth.debian.org/ccid.html"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "virtual/libusb0 pcsc-lite"
RDEPENDS_${PN} = "pcsc-lite"

SRC_URI = "https://alioth.debian.org/frs/download.php/file/4132/ccid-${PV}.tar.bz2 \
	   file://no-dep-on-libfl.patch "

SRC_URI[md5sum] = "d1eed995ba9a2eb395a65a8a78090f52"
SRC_URI[sha256sum] = "cccb2c2bb4e56689efe34559f713102d92f94735542e58d3e4334e1459e934e1"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/pcsc/"
FILES_${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
