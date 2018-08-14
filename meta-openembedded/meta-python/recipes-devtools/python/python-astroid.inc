SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI[md5sum] = "7d7dc000b25f0cbfcca20cb9a8dc0310"
SRC_URI[sha256sum] = "35cfae47aac19c7b407b7095410e895e836f2285ccf1220336afba744cc4c5f2"

inherit pypi

PACKAGES =+ "${PN}-tests"

FILES_${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/test* \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/__pycache__/test* \
"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-distutils \
    ${PYTHON_PN}-lazy-object-proxy \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-wrapt \
    ${PYTHON_PN}-setuptools \
"

RDEPENDS_${PN}-tests_class-target += "\
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-xml \
"
