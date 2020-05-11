SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "http://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ee0470f2de336c370a71c2f8d5e81c11"

SRC_URI[md5sum] = "628ffa1dd5768e0be70236596cee8d6b"
SRC_URI[sha256sum] = "3c07765308f091d81b6735d4f2242bb43c332cc3461cae60543df6b10967fe27"

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
