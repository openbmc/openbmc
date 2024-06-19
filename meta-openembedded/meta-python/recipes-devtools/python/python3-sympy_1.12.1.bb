SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea48085d7dff75b49271b25447e8cdca"

SRC_URI[sha256sum] = "2877b03f998cd8c08f07cd0de5b767119cd3ef40d09f41c30d722f6686b0fb88"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-misc \
    python3-mpmath \
"

BBCLASSEXTEND = "native nativesdk"
