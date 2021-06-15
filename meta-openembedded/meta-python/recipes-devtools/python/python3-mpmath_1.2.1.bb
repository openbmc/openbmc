# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-mpmath_0.19.bb

SUMMARY = "Python library for arbitrary-precision floating-point arithmetic"
HOMEPAGE = "https://pypi.org/project/mpmath/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71970bd3749eebe1bfef9f1efff5b37a"

SRC_URI[sha256sum] = "79ffb45cf9f4b101a807595bcb3e72e0396202e0b1d25d689134b48c4216a81a"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"
RDEPENDS_${PN} += "python3-image"

BBCLASSEXTEND = "native nativesdk"
