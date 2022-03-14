DESCRIPTION = "A flexible forms validation and rendering library for python web development."
HOMEPAGE = "https://pypi.python.org/pypi/WTForms"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"

LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=53dbfa56f61b90215a9f8f0d527c043d"

SRC_URI[sha256sum] = "6b351bbb12dd58af57ffef05bc78425d08d1914e0fd68ee14143b7ade023c5bc"

PYPI_PACKAGE = "WTForms"

inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-pip-native \
    ${PYTHON_PN}-babel-native \
    "

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-markupsafe \
    "
