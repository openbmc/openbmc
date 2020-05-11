# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-mpmath_0.19.bb

SUMMARY = "Python library for arbitrary-precision floating-point arithmetic"
HOMEPAGE = "https://pypi.org/project/mpmath/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=efe9feb00df0b763941f2b1bbac7c402"

SRC_URI[md5sum] = "acb1cdddf38e16084628065b174ddbfe"
SRC_URI[sha256sum] = "fc17abe05fbab3382b61a123c398508183406fa132e0223874578e20946499f6"

inherit pypi setuptools3

RDEPENDS_${PN} += "python3-image"

BBCLASSEXTEND = "native nativesdk"
