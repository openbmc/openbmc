DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI[sha256sum] = "82c382c2a2c712f1a8320378a9120ac9589d9f1131c36a63780f0b8504afa5bc"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-dnspython \
	${PYTHON_PN}-six \
	${PYTHON_PN}-distutils \
	${PYTHON_PN}-greenlet \
"
