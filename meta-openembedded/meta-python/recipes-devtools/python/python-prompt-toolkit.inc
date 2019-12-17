SUMMARY = "Library for building powerful interactive command lines in Python"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b2cde7da89f0c1f3e49bf968d00d554f"

SRC_URI[md5sum] = "5016c523e603cd7119644fbc0f00ce53"
SRC_URI[sha256sum] = "f15af68f66e664eaa559d4ac8a928111eebd5feda0c11738b5998045224829db"

PYPI_PACKAGE = "prompt_toolkit"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-six \
    ${PYTHON_PN}-terminal \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-wcwidth \
"

BBCLASSEXTEND = "native nativesdk"
