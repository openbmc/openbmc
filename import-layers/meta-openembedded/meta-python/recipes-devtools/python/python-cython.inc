DESCRIPTION = "Cython is a language specially designed for writing Python extension modules. \
It's designed to bridge the gap between the nice, high-level, easy-to-use world of Python \
and the messy, low-level world of C."
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e23fadd6ceef8c618fc1c65191d846fa"
PYPI_PACKAGE = "Cython"
BBCLASSEXTEND = "native"

SRC_URI[md5sum] = "890b494a12951f1d6228c416a5789554"
SRC_URI[sha256sum] = "84808fda00508757928e1feadcf41c9f78e9a9b7167b6649ab0933b76f75e7b9"

inherit pypi

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-subprocess \
    ${PYTHON_PN}-shell \
"
RDEPENDS_${PN}_class-native = ""
