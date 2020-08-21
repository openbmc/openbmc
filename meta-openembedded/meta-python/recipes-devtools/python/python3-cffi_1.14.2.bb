SUMMARY = "Foreign Function Interface for Python calling C code"
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS += "libffi ${PYTHON_PN}-pycparser"

SRC_URI[md5sum] = "3cc2f1daf62dd66eda79b4d6281cebfc"
SRC_URI[sha256sum] = "ae8f34d50af2c2154035984b8b5fc5d9ed63f32fe615646ab435b05b132ca91b"

inherit pypi setuptools3

RDEPENDS_${PN}_class-target = " \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-pycparser \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
