DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "e603a7e1e40f5b4963c4c64cf4b013264afa009e232a878c73d31c29942e4466"

PYPI_PACKAGE = "cantools"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	${PYTHON_PN}-can \
	${PYTHON_PN}-bitstruct \
	${PYTHON_PN}-core \
	${PYTHON_PN}-textparser \
	${PYTHON_PN}-typing-extensions \
	${PYTHON_PN}-diskcache \
"

CLEANBROKEN = "1"
