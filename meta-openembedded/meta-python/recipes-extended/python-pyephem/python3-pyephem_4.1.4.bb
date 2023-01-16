SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "http://rhodesmill.org/pyephem/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9c930b395b435b00bb13ec83b0c99f40"

SRC_URI[sha256sum] = "73a59f0d2162d1624535c3c3b75f956556bdbb2055eaf554a7bef147d3f9c760"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
    "
