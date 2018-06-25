SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "http://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ee0470f2de336c370a71c2f8d5e81c11"

SRC_URI[md5sum] = "c7a2757b607748255f3270221ac61d59"
SRC_URI[sha256sum] = "44a94091dd71f05922eec661638ec1a35f26d573c119aa2fad964f10a2880e6c"

inherit pypi

DEPENDS += " \
    ${PYTHON_PN}-numpy-native \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-numpy \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-pytz \
"
