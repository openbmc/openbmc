DESCRIPTION = "Jalali implementation of Python's datetime module"
HOMEPAGE = "https://github.com/slashmili/python-jalali"
LICENSE = "Python-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=fc0a323542d2fbe0ad822fa0b1a0a96f"

SRC_URI[md5sum] = "3c9ee1bc7de48ff4d9c773046b2eb081"
SRC_URI[sha256sum] = "a589e35f0dab89283c1a3de9d70ed6cf657932aaed8e8ce1b0e5801aaab1da67"

PYPI_PACKAGE = "jdatetime"

inherit pypi setuptools3

CLEANBROKEN = "1"

RDEPENDS_${PN} += " \
        ${PYTHON_PN}-modules \
"

