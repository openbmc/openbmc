SUMMARY = "Foreign Function Interface for Python calling C code"
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"
DEPENDS += "libffi ${PYTHON_PN}-pycparser"

SRC_URI[md5sum] = "ac8492f4ad952360737413e82d661908"
SRC_URI[sha256sum] = "e90f17980e6ab0f3c2f3730e56d1fe9bcba1891eeea58966e89d352492cc74f4"

RDEPENDS_${PN}_class-target = " \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-io \
    ${PYTHON_PN}-shell \
"

BBCLASSEXTEND = "native nativesdk"
