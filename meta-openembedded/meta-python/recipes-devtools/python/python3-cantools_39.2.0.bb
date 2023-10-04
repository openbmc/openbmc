DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "24045c5575217bb5331543e6a55cee822c37ad811dc4a08c75aa2eee3c6d529c"

PYPI_PACKAGE = "cantools"

inherit pypi python_poetry_core

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
