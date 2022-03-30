SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.mit;md5=b695eb9c6e7a6fb1b1bc2d193c42776e"

SRC_URI[sha256sum] = "d4d61f8b993a7255ba714df3aca52700f8125289f84f704cf80916517c46eb96"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
