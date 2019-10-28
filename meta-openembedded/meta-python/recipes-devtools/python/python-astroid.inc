SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

SRC_URI[md5sum] = "b2cd5c0383ff33c1410e737c2607aa7a"
SRC_URI[sha256sum] = "09a3fba616519311f1af8a461f804b68f0370e100c9264a035aa7846d7852e33"

inherit pypi

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

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
