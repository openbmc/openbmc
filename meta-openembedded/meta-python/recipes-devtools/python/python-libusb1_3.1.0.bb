SUMMARY = "Pure-python wrapper for libusb-1.0"
HOMEPAGE = "http://github.com/vpelletier/python-libusb1"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI = "https://github.com/vpelletier/${BPN}/releases/download/${PV}/libusb1-${PV}.tar.gz"
SRC_URI[sha256sum] = "4ee9b0a55f8bd0b3ea7017ae919a6c1f439af742c4a4b04543c5fd7af89b828c"

S = "${WORKDIR}/libusb1-${PV}"

RDEPENDS:${PN} = "libusb1"

inherit setuptools3
