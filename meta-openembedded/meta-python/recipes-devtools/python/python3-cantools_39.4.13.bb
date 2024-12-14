DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "ad84fb561f5ab20ccd2a45b7fcb29093008f6c8981c9f636f2d94613ed37c3d1"

PYPI_PACKAGE = "cantools"

inherit pypi python_poetry_core

DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-can \
    python3-bitstruct \
    python3-core \
    python3-textparser \
    python3-typing-extensions \
    python3-diskcache \
    python3-asyncio \
"

CLEANBROKEN = "1"
