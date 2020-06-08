# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-sympy_1.1.bb

SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=611b41534dbf5aa01d7c827bf667ef66"

SRC_URI[md5sum] = "b11b310c3e1642bf66e51038cb3c0021"
SRC_URI[sha256sum] = "d77901d748287d15281f5ffe5b0fef62dd38f357c2b827c44ff07f35695f4e7e"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
