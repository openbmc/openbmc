SUMMARY = "RDFLib is a pure Python package for working with RDF"
HOMEPAGE = "https://github.com/RDFLib/rdflib"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e1efde25e459057a815649b18cbefa5e"

SRC_URI[sha256sum] = "164de86bd3564558802ca983d84f6616a4a1a420c7a17a8152f5016076b2913e"

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
