SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9f6c7fdc2d69e31ad7abaea029c8ac16"

SRC_URI[sha256sum] = "e32380dce63cb7c0108ed525570092fd45168bdae2faa17e528221ef72e88658"

inherit pypi setuptools3

RDEPENDS:${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
