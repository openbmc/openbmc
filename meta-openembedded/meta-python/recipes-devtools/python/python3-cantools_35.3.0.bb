DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[md5sum] = "068514ac776e03ebaa8b2d02dd16fc77"
SRC_URI[sha256sum] = "5e3a6f679ad3dcb31142e388e052187d5fc5bd481c4fc3b2791eaa1e681cca98"

PYPI_PACKAGE = "cantools"

inherit pypi setuptools3

CLEANBROKEN = "1"

