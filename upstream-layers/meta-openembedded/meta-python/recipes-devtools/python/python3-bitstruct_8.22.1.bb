DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "97588318c906c60d33129e0061dd830b03d793c517033d312487c75426d1e808"

inherit pypi python_setuptools_build_meta

CLEANBROKEN = "1"

