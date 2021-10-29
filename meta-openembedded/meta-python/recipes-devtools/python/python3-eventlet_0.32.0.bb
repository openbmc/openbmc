DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI[sha256sum] = "2f0bb8ed0dc0ab21d683975d5d8ab3c054d588ce61def9faf7a465ee363e839b"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-dnspython"
