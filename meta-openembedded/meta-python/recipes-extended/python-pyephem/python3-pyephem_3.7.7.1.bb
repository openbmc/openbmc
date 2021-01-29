SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "http://rhodesmill.org/pyephem/"

LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f288303760f6e5ceaafe3aaa32186ab1"

SRC_URI[sha256sum] = "36b51a8dc7cfdeb456dd6b8ab811accab8341b2d562ee3c6f4c86f6d3dbb984e"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
    "
