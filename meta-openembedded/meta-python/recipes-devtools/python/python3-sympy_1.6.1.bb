# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-sympy_1.1.bb

SUMMARY = "Computer algebra system (CAS) in Python"
HOMEPAGE = "https://pypi.org/project/sympy/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ebb06e9df8f57522b72d0edb0fcf83d4"

SRC_URI[md5sum] = "8bdf8473751722fd1714aa7125b1478b"
SRC_URI[sha256sum] = "7386dba4f7e162e90766b5ea7cab5938c2fe3c620b310518c8ff504b283cb15b"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-mpmath"

BBCLASSEXTEND = "native nativesdk"
