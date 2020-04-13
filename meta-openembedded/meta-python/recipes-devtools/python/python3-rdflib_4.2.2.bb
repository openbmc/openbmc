SUMMARY = "RDFLib is a pure Python package for working with RDF"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=68c1a3bb687bd63b8e5552f3ea249840"

SRC_URI[md5sum] = "534fe35b13c5857d53fa1ac5a41eca67"
SRC_URI[sha256sum] = "da1df14552555c5c7715d8ce71c08f404c988c58a1ecd38552d0da4fc261280d"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-isodate \
    ${PYTHON_PN}-pyparsing \
"

BBCLASSEXTEND = "native nativesdk"
