SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.mit;md5=ca574f2891cf528b3e7a2ee570337e7c"

SRC_URI[md5sum] = "0298efae86e009e24136d39dc8b2d33e"
SRC_URI[sha256sum] = "34b47fa169d6006b32e99d4b3c4031f155e6e68ebcc107d6454852e8e0ee6533"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
