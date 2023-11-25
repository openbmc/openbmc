DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "2c3d081922591bc1611c9f1ff52d6c8af1d03314f8c724cc114d856cc555cc28"

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
