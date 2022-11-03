SUMMARY = "A Python generic test automation framework"
DESCRIPTION = "Generic open source test atomation framework for acceptance\
testing and acceptance test-driven development (ATDD). It has easy-to-use\
tabular test data syntax and it utilizes the keyword-driven testing approach.\
Its testing capabilities can be extended by test libraries implemented either\
with Python or Java, and users can create new higher-level keywords from\
existing ones using the same syntax that is used for creating test cases."
HOMEPAGE = "http://robotframework.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

SRC_URI[sha256sum] = "db9498dad21369a6702384db057994628cd2cf0a172f5685be941c4bf94e242f"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-shell \
    ${PYTHON_PN}-pprint \
    ${PYTHON_PN}-xml \
    ${PYTHON_PN}-difflib \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-html \
    ${PYTHON_PN}-docutils \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-numbers \
    ${PYTHON_PN}-profile \
"
