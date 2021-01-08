# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-sympy_1.1.bb

SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ebb06e9df8f57522b72d0edb0fcf83d4"

SRC_URI[sha256sum] = "a3de9261e97535b83bb8607b0da2c7d03126650fafea2b2789657b229c246b2e"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
