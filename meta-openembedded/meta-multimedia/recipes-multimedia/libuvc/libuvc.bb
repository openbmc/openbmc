SUMMARY = "library for USB video devices built atop libusb"
HOMEPAGE = "https://github.com/libuvc/libuvc.git"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2f1963e0bb88c93463af750daf9ba0c2"
DEPENDS = "libusb jpeg"

SRC_URI = "git://github.com/libuvc/libuvc.git"
SRCREV = "ad6c72a4e390367f0d2be81aac00cfc0b6680d74"
PV = "v0.0.6+git${SRCPV}"

S = "${WORKDIR}/git"

inherit cmake
