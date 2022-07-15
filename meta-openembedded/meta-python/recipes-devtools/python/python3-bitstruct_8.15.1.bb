DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "6fa6adbfb8f3b8cb68c21b13aa65d23eb2c3ac32419ab926f3fd1fff717a9125"

PYPI_PACKAGE = "bitstruct"

inherit pypi setuptools3

CLEANBROKEN = "1"

