# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-mpmath_0.19.bb

SUMMARY = "Python library for arbitrary-precision floating-point arithmetic"
HOMEPAGE = "https://pypi.org/project/mpmath/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bde3c575382996b75d85702949512751"

SRC_URI[sha256sum] = "7a28eb2a9774d00c7bc92411c19a89209d5da7c4c9a9e227be8330a23a25b91f"

inherit pypi setuptools3

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += " \
    python3-image \
    python3-math \
"

BBCLASSEXTEND = "native nativesdk"
