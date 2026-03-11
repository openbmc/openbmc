SUMMARY = "Pure-python wrapper for libusb-1.0"
HOMEPAGE = "https://github.com/vpelletier/python-libusb1"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI[sha256sum] = "3951d360f2daf0e0eacf839e15d2d1d2f4f5e7830231eb3188eeffef2dd17bad"

RDEPENDS:${PN} = "libusb1"

inherit setuptools3 pypi
