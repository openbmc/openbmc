# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-sympy_1.1.bb

SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2245824980a408ef1749391dfba32b3b"

SRC_URI[sha256sum] = "1ca588a9f6ce6a323c5592f9635159c2093572826668a1022c75c75bdf0297cb"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
