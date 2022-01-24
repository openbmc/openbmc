DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "45b2b932ce6681f5c6ce8cba39abdd423b579b0568c76fa48b1e09c88368ede7"

PYPI_PACKAGE = "bitstruct"

inherit pypi setuptools3

CLEANBROKEN = "1"

