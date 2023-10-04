SUMMARY = "RDFLib is a pure Python package for working with RDF"
HOMEPAGE = "https://github.com/RDFLib/rdflib"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37d489c0cefe52a17e1d5007e196464a"

SRC_URI[sha256sum] = "9995eb8569428059b8c1affd26b25eac510d64f5043d9ce8c84e0d0036e995ae"

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
