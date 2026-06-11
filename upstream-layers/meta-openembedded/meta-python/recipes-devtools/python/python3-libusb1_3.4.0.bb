SUMMARY = "Pure-python wrapper for libusb-1.0"
HOMEPAGE = "https://github.com/vpelletier/python-libusb1"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI[sha256sum] = "9cf5638506d54f21bf36550d97ea63189111a23c4d8078f630103a2052135f45"

RDEPENDS:${PN} = "libusb1"

inherit setuptools3 pypi
