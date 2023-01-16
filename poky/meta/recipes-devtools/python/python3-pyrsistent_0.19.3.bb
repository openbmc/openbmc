SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.mit;md5=b695eb9c6e7a6fb1b1bc2d193c42776e"

SRC_URI[sha256sum] = "1a2994773706bbb4995c31a97bc94f1418314923bd1048c6d964837040376440"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
