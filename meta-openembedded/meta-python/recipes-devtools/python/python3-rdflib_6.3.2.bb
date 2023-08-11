SUMMARY = "RDFLib is a pure Python package for working with RDF"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37d489c0cefe52a17e1d5007e196464a"

SRC_URI[sha256sum] = "72af591ff704f4caacea7ecc0c5a9056b8553e0489dd4f35a9bc52dbd41522e0"

inherit pypi python_poetry_core

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
