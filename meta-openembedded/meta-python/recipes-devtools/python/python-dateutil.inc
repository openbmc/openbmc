SUMMARY = "Extensions to the standard Python datetime module"
DESCRIPTION = "The dateutil module provides powerful extensions to the datetime module available in the Python standard library."
HOMEPAGE = "https://dateutil.readthedocs.org"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3155c7bdc71f66e02678411d2abf996"

SRC_URI[md5sum] = "354db28843d949f1bc3e49e8458e22df"
SRC_URI[sha256sum] = "c89805f6f4d64db21ed966fda138f8a5ed7a4fdbc1a8ee329ce1b74e3c74da9e"

PYPI_PACKAGE = "python-dateutil"
inherit pypi

PACKAGES =+ "${PN}-zoneinfo"
FILES_${PN}-zoneinfo = "${libdir}/${PYTHON_DIR}/site-packages/dateutil/zoneinfo"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-stringold \
"

BBCLASSEXTEND = "native"
