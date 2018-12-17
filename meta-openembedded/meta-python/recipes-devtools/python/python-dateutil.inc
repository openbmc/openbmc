SUMMARY = "Extensions to the standard Python datetime module"
DESCRIPTION = "The dateutil module provides powerful extensions to the datetime module available in the Python standard library."
HOMEPAGE = "https://dateutil.readthedocs.org"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3155c7bdc71f66e02678411d2abf996"

SRC_URI[md5sum] = "175b7c1a3cc0cb01151eced07c61c0b8"
SRC_URI[sha256sum] = "e27001de32f627c22380a688bcc43ce83504a7bc5da472209b4c70f02829f0b8"

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
