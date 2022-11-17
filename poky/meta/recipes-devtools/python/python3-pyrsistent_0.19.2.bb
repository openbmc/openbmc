SUMMARY = "Persistent/Immutable/Functional data structures for Python"
HOMEPAGE = "https://github.com/tobgu/pyrsistent"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.mit;md5=b695eb9c6e7a6fb1b1bc2d193c42776e"

SRC_URI[sha256sum] = "bfa0351be89c9fcbcb8c9879b826f4353be10f58f8a677efab0c017bf7137ec2"

inherit pypi python_setuptools_build_meta

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-numbers \
"

BBCLASSEXTEND = "native nativesdk"
