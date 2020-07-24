DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[md5sum] = "8eaf853c95e10ca4b6bca2fe5ff307c6"
SRC_URI[sha256sum] = "2b13f2c3e76b49e8cd854f7a1da590bb73ecbc6cbfacc2d479eacf2b88282d5d"

PYPI_PACKAGE = "bitstruct"

inherit pypi setuptools3

CLEANBROKEN = "1"

