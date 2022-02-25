SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "http://rhodesmill.org/pyephem/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c930b395b435b00bb13ec83b0c99f40"

SRC_URI[sha256sum] = "7fa18685981ba528edd504052a9d5212a09aa5bf15c11a734edc6a86e8a8b56a"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
    "
