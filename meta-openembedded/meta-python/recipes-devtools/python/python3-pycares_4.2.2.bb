SUMMARY = "Python interface for c-ares"
DESCRIPTION = "\
pycares is a Python module which provides an interface to c-ares. c-ares is \
a C library that performs DNS requests and name resolutions asynchronously."
HOMEPAGE = "https://github.com/saghul/pycares"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1538fcaea82ebf2313ed648b96c69b1"

SRC_URI[sha256sum] = "e1f57a8004370080694bd6fb969a1ffc9171a59c6824d54f791c1b2e4d298385"

PYPI_PACKAGE = "pycares"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-cffi-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-idna \
"
