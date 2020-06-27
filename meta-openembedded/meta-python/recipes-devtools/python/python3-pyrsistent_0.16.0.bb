SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.mit;md5=ca574f2891cf528b3e7a2ee570337e7c"

SRC_URI[md5sum] = "4ba30da6f0a63554e70ac6c4c1904929"
SRC_URI[sha256sum] = "28669905fe725965daa16184933676547c5bb40a5153055a8dee2a4bd7933ad3"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
