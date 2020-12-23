SUMMARY = "Foreign Function Interface for Python calling C code"
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS += "libffi ${PYTHON_PN}-pycparser"

SRC_URI[md5sum] = "c2a47ffd5d183b193ac8ed3414dcfd07"
SRC_URI[sha256sum] = "f92f789e4f9241cd262ad7a555ca2c648a98178a953af117ef7fad46aa1d5591"

inherit pypi setuptools3

RDEPENDS_${PN}_class-target = " \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-pycparser \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
