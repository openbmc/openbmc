SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea48085d7dff75b49271b25447e8cdca"

SRC_URI[sha256sum] = "401449d84d07be9d0c7a46a64bd54fe097667d5e7181bfe67ec777be9e01cb13"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-misc \
    python3-mpmath \
"

BBCLASSEXTEND = "native nativesdk"
