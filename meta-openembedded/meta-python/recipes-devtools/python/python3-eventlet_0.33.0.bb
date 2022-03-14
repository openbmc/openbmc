DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI[sha256sum] = "80144f489c1bb273a51b6f96ff9785a382d2866b9bab1f5bd748385019f4141f"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-dnspython \
	${PYTHON_PN}-six \
	${PYTHON_PN}-distutils \
	${PYTHON_PN}-greenlet \
"
