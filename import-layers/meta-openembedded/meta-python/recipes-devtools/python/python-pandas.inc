SUMMARY  = "pandas library for high-performance data analysis tools"
DESCRIPTION = "pandas is an open source, BSD-licensed library providing \
high-performance, easy-to-use data structures and data analysis tools for \
the Python programming language."
HOMEPAGE = "http://pandas.pydata.org/"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=66017d3134349017409386be85176145"

SRC_URI[md5sum] = "4df858f28b4bf4fa07d9fbb7f2568173"
SRC_URI[sha256sum] = "a777e07633d83d546c55706420179551c8e01075b53c497dcf8ae4036766bc66"

PYPI_PACKAGE = "pandas"
inherit pypi

DEPENDS += " \
    ${PYTHON_PN}-numpy-native \
"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-numpy \
    ${PYTHON_PN}-dateutil \
    ${PYTHON_PN}-pytz \
"
