SUMMARY = "RDFLib is a pure Python package for working with RDF"
HOMEPAGE = "https://github.com/RDFLib/rdflib"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0ac0171cee5250c44d7d49847ae53a3e"

SRC_URI[sha256sum] = "6c831288d5e4a5a7ece85d0ccde9877d512a3d0f02d7c06455d00d6d0ea379df"

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
