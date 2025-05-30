SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ea48085d7dff75b49271b25447e8cdca"

SRC_URI[sha256sum] = "d3d3fe8df1e5a0b42f0e7bdf50541697dbe7d23746e894990c030e2b05e72517"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    python3-misc \
    python3-mpmath \
"

BBCLASSEXTEND = "native nativesdk"
