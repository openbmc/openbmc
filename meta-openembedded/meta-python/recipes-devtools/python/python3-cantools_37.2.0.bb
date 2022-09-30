DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "bbb0e802af02a1dc792c32d10b61b51deaefc1c8e9c776b50d2d2e194b702fac"

PYPI_PACKAGE = "cantools"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
	${PYTHON_PN}-can \
	${PYTHON_PN}-bitstruct \
	${PYTHON_PN}-core \
	${PYTHON_PN}-textparser \
	${PYTHON_PN}-typing-extensions \
	${PYTHON_PN}-diskcache \
        ${PYTHON_PN}-asyncio \
"

CLEANBROKEN = "1"
