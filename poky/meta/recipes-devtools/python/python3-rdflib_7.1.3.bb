SUMMARY = "RDFLib is a pure Python package for working with RDF"
HOMEPAGE = "https://github.com/RDFLib/rdflib"
SECTION = "devel/python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7006e973486c4700556f6d58cba0ab6c"

SRC_URI[sha256sum] = "f3dcb4c106a8cd9e060d92f43d593d09ebc3d07adc244f4c7315856a12e383ee"

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
