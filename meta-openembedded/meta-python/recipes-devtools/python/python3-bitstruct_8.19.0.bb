DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "d75ba9dded85c17e885a209a00eb8e248ee40762149f2f2a79360ca857467dac"

PYPI_PACKAGE = "bitstruct"

inherit pypi setuptools3

CLEANBROKEN = "1"

