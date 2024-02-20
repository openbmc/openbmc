SUMMARY = "RDFLib is a pure Python package for working with RDF"
HOMEPAGE = "https://github.com/RDFLib/rdflib"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=37d489c0cefe52a17e1d5007e196464a"

SRC_URI[sha256sum] = "9995eb8569428059b8c1affd26b25eac510d64f5043d9ce8c84e0d0036e995ae"

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    python3-isodate \
    python3-pyparsing \
    python3-logging \
    python3-numbers \
    python3-xml \
    python3-compression \
    python3-core \
"

BBCLASSEXTEND = "native nativesdk"
