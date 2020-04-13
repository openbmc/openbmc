SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "http://rhodesmill.org/pyephem/"

LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f288303760f6e5ceaafe3aaa32186ab1"

SRC_URI[md5sum] = "46c035b4a903ff26e0d8ad0f1fe6cc35"
SRC_URI[sha256sum] = "607148429f85412915e32265779c0cf6d09f73aa97cf1ff0d101ac22c69c4436"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
    "
