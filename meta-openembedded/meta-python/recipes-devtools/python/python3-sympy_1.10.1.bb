SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=42976c55ba05d15b32a7b4757dee5e64"

SRC_URI[sha256sum] = "5939eeffdf9e152172601463626c022a2c27e75cf6278de8d401d50c9d58787b"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
