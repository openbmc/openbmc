SUMMARY = "Python interface for c-ares"
DESCRIPTION = "\
pycares is a Python module which provides an interface to c-ares. c-ares is \
a C library that performs DNS requests and name resolutions asynchronously."
HOMEPAGE = "https://github.com/saghul/pycares"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1538fcaea82ebf2313ed648b96c69b1"

SRC_URI[sha256sum] = "c542696f6dac978e9d99192384745a65f80a7d9450501151e4a7563e06010d45"

PYPI_PACKAGE = "pycares"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-cffi-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-idna \
"
