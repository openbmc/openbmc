SUMMARY = "Python interface for c-ares"
DESCRIPTION = "\
pycares is a Python module which provides an interface to c-ares. c-ares is \
a C library that performs DNS requests and name resolutions asynchronously."
HOMEPAGE = "https://github.com/saghul/pycares"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b1538fcaea82ebf2313ed648b96c69b1"

SRC_URI[sha256sum] = "3e89edc56a3745046676207958bf3dbf2971c5df0d64de9f7a893eba8bf255b4"


inherit pypi python_setuptools_build_meta

DEPENDS += "python3-cffi-native"

RDEPENDS:${PN} += " \
    python3-cffi \
    python3-idna \
"

BBCLASSEXTEND = "native nativesdk"
