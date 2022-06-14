SUMMARY = "Python interface to Linux uinput kernel module."
HOMEPAGE = "https://pypi.org/project/python-uinput/"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"

SRC_URI += "file://0001-setup-use-setuptools-instead-of-distutils.patch \
            file://0001-Deal-with-64bit-time_t-default-on-32bit-architecture.patch \
"
SRC_URI[sha256sum] = "99392b676c77b5795b86b7d75274db33fe754fd1e06fb3d58b167c797dc47f0c"

PYPI_PACKAGE = "python-uinput"

inherit pypi setuptools3

DEPENDS += "udev"
RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-distutils \
"
RRECOMMENDS:${PN} += "kernel-module-uinput"
