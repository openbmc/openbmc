SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "http://pandas.pydata.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3f23c5c092b74d245d48eeef72bc3fd2"

SRC_URI[sha256sum] = "2ff7788468e75917574f080cd4681b27e1a7bf36461fe968b49a87b5a54d007c"

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
