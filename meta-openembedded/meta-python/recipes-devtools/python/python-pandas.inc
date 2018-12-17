SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "http://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ee0470f2de336c370a71c2f8d5e81c11"

SRC_URI[md5sum] = "7b597c7f989652e0c9af5f09a157e3ae"
SRC_URI[sha256sum] = "5b24ca47acf69222e82530e89111dd9d14f9b970ab2cd3a1c2c78f0c4fbba4f4"

inherit pypi

DEPENDS += " \
    ${PYTHON_PN}-numpy-native ${PYTHON_PN}-cython-native \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-numpy \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-pytz \
"
