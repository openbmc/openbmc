SUMMARY = "Python interface to Linux uinput kernel module."
HOMEPAGE = "https://pypi.org/project/python-uinput/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI += "file://0001-Deal-with-64bit-time_t-default-on-32bit-architecture.patch"
SRC_URI[sha256sum] = "853697344b64df5537d4ae32ba6fbcf0515d51a9010910f5d5019959038b6eba"

PYPI_PACKAGE = "python-uinput"

inherit pypi setuptools3

DEPENDS += "udev"
RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-setuptools \
"
RRECOMMENDS:${PN} += "kernel-module-uinput"
