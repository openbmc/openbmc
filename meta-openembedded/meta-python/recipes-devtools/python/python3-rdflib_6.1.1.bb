SUMMARY = "RDFLib is a pure Python package for working with RDF"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b6cde159c801514e0e45a40cf0a9d3d9"

SRC_URI[sha256sum] = "8dbfa0af2990b98471dacbc936d6494c997ede92fd8ed693fb84ee700ef6f754"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-isodate \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
