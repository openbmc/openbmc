DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "98c4d007a6d9803c6433c743c0240e73de930530f8255e1e21d2e20e8991a30b"

PYPI_PACKAGE = "cantools"

inherit pypi python_poetry_core

DEPENDS += "python3-setuptools-scm-native"

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
