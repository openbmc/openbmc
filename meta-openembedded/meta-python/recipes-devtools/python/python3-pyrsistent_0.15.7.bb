SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.mit;md5=ca574f2891cf528b3e7a2ee570337e7c"

SRC_URI[md5sum] = "da9486d00ef5b213f40d5cf3c5bca82d"
SRC_URI[sha256sum] = "cdc7b5e3ed77bed61270a47d35434a30617b9becdf2478af76ad2c6ade307280"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
