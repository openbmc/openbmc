SUMMARY = "An implementation of time.monotonic() for Python 2.0 through 3.2"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

SRC_URI[md5sum] = "29302cd6b1013b3ab0d1ed78e20d22f2"
SRC_URI[sha256sum] = "a02611d5b518cd4051bf22d21bd0ae55b3a03f2d2993a19b6c90d9d168691f84"

inherit pypi setuptools

RDEPENDS_${PN} += "${PYTHON_PN}-ctypes ${PYTHON_PN}-io ${PYTHON_PN}-re ${PYTHON_PN}-threading"
