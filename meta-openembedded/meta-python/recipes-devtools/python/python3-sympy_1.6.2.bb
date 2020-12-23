# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-sympy_1.1.bb

SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ebb06e9df8f57522b72d0edb0fcf83d4"

SRC_URI[md5sum] = "50d6b69b1de36b757484b9ff833a9e0a"
SRC_URI[sha256sum] = "1cfadcc80506e4b793f5b088558ca1fcbeaec24cd6fc86f1fdccaa3ee1d48708"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
