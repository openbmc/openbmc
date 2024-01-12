DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "8069a8f473bb1679990bea0903f3a22d58df5495aebbf09f58d49f627511b619"

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
