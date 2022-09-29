DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "0d84b879a18d869d182023cdebae9318095a8959ceee6309de59fd3c399dbfef"

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
