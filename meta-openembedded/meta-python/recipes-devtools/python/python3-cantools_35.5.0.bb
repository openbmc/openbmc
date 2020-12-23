DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[sha256sum] = "ce26ab5d54762c314bedc35b407d498b8ae11069ab4d9251f8ac83f2956438f9"

PYPI_PACKAGE = "cantools"

inherit pypi setuptools3

CLEANBROKEN = "1"

