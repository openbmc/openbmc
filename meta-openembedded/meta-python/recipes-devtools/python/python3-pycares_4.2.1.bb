SUMMARY = "Python interface for c-ares"
DESCRIPTION = "\
pycares is a Python module which provides an interface to c-ares. c-ares is \
a C library that performs DNS requests and name resolutions asynchronously."
HOMEPAGE = "https://github.com/saghul/pycares"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1538fcaea82ebf2313ed648b96c69b1"

SRC_URI[md5sum] = "92fa9622ba42cb895d598910722e80b5"
SRC_URI[sha256sum] = "735b4f75fd0f595c4e9184da18cd87737f46bc81a64ea41f4edce2b6b68d46d2"

PYPI_PACKAGE = "pycares"

inherit pypi setuptools3

DEPENDS += "${PYTHON_PN}-cffi-native"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-idna \
"
