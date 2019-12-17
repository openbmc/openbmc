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

SRC_URI[md5sum] = "e4690f3362f95adb2332fd47a2b08212"
SRC_URI[sha256sum] = "6b48d7b6e4390e038d25630f8664fe81618ab00f232d6efbe0e3cc6df28ce8f7"

inherit autotools pkgconfig

FILES_${PN} += "${libdir}/pcsc/"
FILES_${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
