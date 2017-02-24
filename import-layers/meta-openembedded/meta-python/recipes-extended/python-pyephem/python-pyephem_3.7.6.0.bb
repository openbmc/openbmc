SUMMARY = "PyEphem astronomical calculations"
HOMEPAGE = "http://rhodesmill.org/pyephem/"

LICENSE = "LGPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=f288303760f6e5ceaafe3aaa32186ab1"

SRC_URI[md5sum] = "405a109f3017251ecd8c2890d850f649"
SRC_URI[sha256sum] = "7a4c82b1def2893e02aec0394f108d24adb17bd7b0ca6f4bc78eb7120c0212ac"

PYPI_PACKAGE = "ephem"

inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-math \
    "
