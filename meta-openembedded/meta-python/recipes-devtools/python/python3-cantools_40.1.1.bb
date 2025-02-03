DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "b1731da5e6997661084c6019d6cdcb3773868c035ed8a728126cf8c6f31a2368"

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
