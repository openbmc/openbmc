SUMMARY = "Pure-python wrapper for libusb-1.0"
HOMEPAGE = "http://github.com/vpelletier/python-libusb1"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI[sha256sum] = "a11a6095e718cd49418a96329314da271cca6be7b4317a142724523371ac8961"

RDEPENDS:${PN} = "libusb1"

inherit setuptools3 pypi
