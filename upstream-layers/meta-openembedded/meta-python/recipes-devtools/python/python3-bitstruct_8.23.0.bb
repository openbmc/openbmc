DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "3d20a8d748add9b1c68efc6c592a99b4b0937c9dcddd140fdae352bf2513aea5"

inherit pypi python_setuptools_build_meta

CLEANBROKEN = "1"

