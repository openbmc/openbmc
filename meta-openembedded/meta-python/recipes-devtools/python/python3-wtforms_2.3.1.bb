DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=208ce1c159f911ecf389806650582021"

SRC_URI[md5sum] = "bff06943e59671581af07f80d14bda5f"
SRC_URI[sha256sum] = "861a13b3ae521d6700dac3b2771970bd354a63ba7043ecc3a82b5288596a1972"

PYPI_PACKAGE = "WTForms"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    "
