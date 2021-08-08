SUMMARY = "RDFLib is a pure Python package for working with RDF"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=89aa9a14f80a6ac70e1d5da763a309ed"

SRC_URI[sha256sum] = "7ce4d757eb26f4dd43205ec340d8c097f29e5adfe45d6ea20238c731dc679879"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-isodate \
    ${PYTHON_PN}-pyparsing \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-xml \
"

BBCLASSEXTEND = "native nativesdk"
