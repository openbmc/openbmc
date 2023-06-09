SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea48085d7dff75b49271b25447e8cdca"

SRC_URI[sha256sum] = "ebf595c8dac3e0fdc4152c51878b498396ec7f30e7a914d6071e674d49420fb8"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-misc \
    python3-mpmath \
"

BBCLASSEXTEND = "native nativesdk"
