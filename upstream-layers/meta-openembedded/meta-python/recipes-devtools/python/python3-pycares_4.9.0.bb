SUMMARY = "Python interface for c-ares"
DESCRIPTION = "\
pycares is a Python module which provides an interface to c-ares. c-ares is \
a C library that performs DNS requests and name resolutions asynchronously."
HOMEPAGE = "https://github.com/saghul/pycares"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1538fcaea82ebf2313ed648b96c69b1"

SRC_URI[sha256sum] = "8ee484ddb23dbec4d88d14ed5b6d592c1960d2e93c385d5e52b6fad564d82395"

PYPI_PACKAGE = "pycares"

inherit pypi python_setuptools_build_meta

DEPENDS += "python3-cffi-native"

RDEPENDS:${PN} += " \
    python3-cffi \
    python3-idna \
"

BBCLASSEXTEND = "native nativesdk"
