SUMMARY = "Linux network configuration library for Python"
DESCRIPTION = "This library contains Python bindings to ioctl calls"
SECTION = "devel/python"
HOMEPAGE = "http://github.com/rlisagor/pynetlinux"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=74e1861736ee959824fe7542323c12e9"

SRC_URI[sha256sum] = "4ad08298c9f5ba15a11cddc639ba8778cabdfc402b51066d9e0a325e5a5b391c"

SRC_URI += " \
    file://0001-setup.py-switch-to-setuptools.patch \
    file://0002-Fixed-relative-imports.patch \
"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-ctypes \
    python3-fcntl \
    python3-io \
"
