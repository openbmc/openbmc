DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "31bcdd56cbb5e26decab42f0624c8cead9b94991f6ee922512b0acd6ae7d6da2"

PYPI_PACKAGE = "cantools"

inherit pypi setuptools3

CLEANBROKEN = "1"

