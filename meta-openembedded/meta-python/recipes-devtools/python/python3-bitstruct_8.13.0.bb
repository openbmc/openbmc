DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "7b87d9e41ff552a8cae06ea2368c3772b6f3102bdab4b65e793be7590d69f03b"

PYPI_PACKAGE = "bitstruct"

inherit pypi setuptools3

CLEANBROKEN = "1"

