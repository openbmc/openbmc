SUMMARY = "An abstract syntax tree for Python with inference support."
HOMEPAGE = "https://pypi.python.org/pypi/astroid"
SECTION = "devel/python"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a70cf540abf41acb644ac3b621b2fad1"

SRC_URI[sha256sum] = "81f870105d892e73bf535da77a8261aa5bde838fa4ed12bb2f435291a098c581"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-pytest-runner-native"

PACKAGES =+ "${PN}-tests"

FILES:${PN}-tests += " \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/test* \
    ${PYTHON_SITEPACKAGES_DIR}/astroid/__pycache__/test* \
"

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-distutils \
    ${PYTHON_PN}-lazy-object-proxy \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-wrapt \
    ${PYTHON_PN}-setuptools \
"

RDEPENDS:${PN}-tests:class-target += "\
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-xml \
"
