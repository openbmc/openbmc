DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "f6b16a93097313f2a6c146640c93e5f988a39c33364f8c20a4286ac1c5ed5dae"

inherit pypi python_setuptools_build_meta

CLEANBROKEN = "1"

