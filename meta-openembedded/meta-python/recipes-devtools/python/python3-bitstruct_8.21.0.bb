DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "ff0be4968a45caf8688e075f55cca7a3fe9212b069ba67e5b27b0926a11948ac"

inherit pypi python_setuptools_build_meta

CLEANBROKEN = "1"

