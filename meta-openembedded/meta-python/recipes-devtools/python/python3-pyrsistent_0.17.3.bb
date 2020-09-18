SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENCE.mit;md5=ca574f2891cf528b3e7a2ee570337e7c"

SRC_URI[md5sum] = "cd38658ea772a7f9b12b6f9485a7018b"
SRC_URI[sha256sum] = "2e636185d9eb976a18a8a8e96efce62f2905fea90041958d8cc2a189756ebf3e"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
