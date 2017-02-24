DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=c459accc90c6ed6a94878c8fe0535be2"

SRC_URI[md5sum] = "6938a541fafd1a1ae2f6b9b88588eef2"
SRC_URI[sha256sum] = "ffdf10bd1fa565b8233380cb77a304cd36fd55c73023e91d4b803c96bc11d46f"

PYPI_PACKAGE = "WTForms"
PYPI_PACKAGE_EXT = "zip"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    "
