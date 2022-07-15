DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "9f2449e94a7698bd44bb50d9b464788053a0bf070faa09a132535c3aa07e7e6a"

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
