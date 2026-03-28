# This recipe is adapted from one in meta-jupyter:
# https://github.com/Xilinx/meta-jupyter/blob/master/recipes-python/python3-mpmath_0.19.bb

SUMMARY = "Python library for arbitrary-precision floating-point arithmetic"
HOMEPAGE = "https://pypi.org/project/mpmath/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6607bd72611b702183473dfb4e6198b"

SRC_URI[sha256sum] = "efd6d1b75f09d69524a67609949812668b28e81ecbfe0ab449ced8c13e92642e"

CVE_PRODUCT = "mpmath"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-setuptools-scm-native"
RDEPENDS:${PN} += " \
    python3-image \
    python3-math \
"

BBCLASSEXTEND = "native nativesdk"
