DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI[sha256sum] = "2115c7c6742e6893bf1347f82915572f8895c911cb5abaad4d3596a7daa847cc"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-dnspython \
	${PYTHON_PN}-six \
	${PYTHON_PN}-greenlet \
"
