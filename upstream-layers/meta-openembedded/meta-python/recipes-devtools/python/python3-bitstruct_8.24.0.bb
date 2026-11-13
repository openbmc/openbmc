DESCRIPTION = "This module performs conversions between Python values and C bit field structs represented as Python byte strings."
HOMEPAGE = "https://github.com/eerimoq/bitstruct"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "c72d8bd3b1762a5bcc9fde16bfa4d52800a4bc8d79f81bc7ee0e0bca5b5e901e"

inherit pypi python_setuptools_build_meta

CLEANBROKEN = "1"

