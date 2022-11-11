SUMMARY = "RDFLib is a pure Python package for working with RDF"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bcae79bd3c84b857f42a98a7ccf6ad47"

SRC_URI[sha256sum] = "62dc3c86d1712db0f55785baf8047f63731fa59b2682be03219cb89262065942"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-isodate \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-xml \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-core \
"

BBCLASSEXTEND = "native nativesdk"
