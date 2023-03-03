SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "http://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c1cc9ab35a8b2aabf933cd6d245b5db3"

SRC_URI[sha256sum] = "74a3fd7e5a7ec052f183273dc7b0acd3a863edf7520f5d3a1765c04ffdb3b0b1"

inherit pypi setuptools3

DEPENDS += " \
    ${PYTHON_PN}-numpy-native ${PYTHON_PN}-cython-native \
"

CFLAGS:append:toolchain-clang = " -Wno-error=deprecated-declarations"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-json \
    ${PYTHON_PN}-numpy \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-dateutil-zoneinfo \
    ${PYTHON_PN}-pytz \
    ${PYTHON_PN}-profile \
"
