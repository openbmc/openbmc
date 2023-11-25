SUMMARY = "library for USB video devices built atop libusb"
HOMEPAGE = "https://github.com/libuvc/libuvc.git"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=2f1963e0bb88c93463af750daf9ba0c2"

DEPENDS = "libusb jpeg"

SRC_URI = "git://github.com/libuvc/libuvc.git;branch=master;protocol=https"
SRCREV = "68d07a00e11d1944e27b7295ee69673239c00b4b"

S = "${WORKDIR}/git"

inherit cmake pkgconfig
