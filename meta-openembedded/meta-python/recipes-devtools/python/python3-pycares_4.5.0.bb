SUMMARY = "Python interface for c-ares"
DESCRIPTION = "\
pycares is a Python module which provides an interface to c-ares. c-ares is \
a C library that performs DNS requests and name resolutions asynchronously."
HOMEPAGE = "https://github.com/saghul/pycares"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1538fcaea82ebf2313ed648b96c69b1"

SRC_URI[sha256sum] = "025b6c2ffea4e9fb8f9a097381c2fecb24aff23fbd6906e70da22ec9ba60e19d"

PYPI_PACKAGE = "pycares"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-cffi-native"

RDEPENDS:${PN} += " \
    python3-cffi \
    python3-idna \
"

BBCLASSEXTEND = "native nativesdk"
