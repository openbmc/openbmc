DESCRIPTION = "Highly concurrent networking library"
HOMEPAGE = "http://pypi.python.org/pypi/eventlet"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=56472ad6de4caf50e05332a34b66e778"

SRC_URI[sha256sum] = "722803e7eadff295347539da363d68ae155b8b26ae6a634474d0a920be73cfda"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
	${PYTHON_PN}-dnspython \
	${PYTHON_PN}-six \
	${PYTHON_PN}-distutils \
	${PYTHON_PN}-greenlet \
"
