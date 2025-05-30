SUMMARY = "RDFLib is a pure Python package for working with RDF"
HOMEPAGE = "https://github.com/RDFLib/rdflib"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7006e973486c4700556f6d58cba0ab6c"

SRC_URI[sha256sum] = "fed46e24f26a788e2ab8e445f7077f00edcf95abb73bcef4b86cefa8b62dd174"

inherit pypi python_poetry_core

RDEPENDS:${PN} += " \
    python3-datetime \
    python3-pyparsing \
    python3-logging \
    python3-numbers \
    python3-xml \
    python3-compression \
    python3-core \
"

BBCLASSEXTEND = "native nativesdk"
