SUMMARY = "Extensions to the standard Python datetime module"
DESCRIPTION = "The dateutil module provides powerful extensions to the datetime module available in the Python standard library."
HOMEPAGE = "https://dateutil.readthedocs.org"
LICENSE = "BSD-3-Clause & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e3155c7bdc71f66e02678411d2abf996"

SRC_URI[md5sum] = "03a08c8bcf0a2b29f1cd21b9de4d12fb"
SRC_URI[sha256sum] = "9d8074be4c993fbe4947878ce593052f71dac82932a677d49194d8ce9778002e"

PYPI_PACKAGE = "python-dateutil"
inherit pypi

PACKAGES =+ "${PN}-zoneinfo"
FILES_${PN}-zoneinfo = "${libdir}/${PYTHON_DIR}/site-packages/dateutil/zoneinfo"

RDEPENDS_${PN}_class-target = "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-stringold \
"
