DESCRIPTION = "CAN BUS tools in Python 3."
HOMEPAGE = "https://github.com/eerimoq/cantools"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9aa4ec07de78abae21c490c9ffe61bd"

SRC_URI[md5sum] = "46b71bbfec33146d9dbba708489a8ae2"
SRC_URI[sha256sum] = "bd0ac5b16bb7fe2ada0c9436c91a0b3795217bed7126296dde1565919a3f44f1"

PYPI_PACKAGE = "cantools"

inherit pypi setuptools3

CLEANBROKEN = "1"

