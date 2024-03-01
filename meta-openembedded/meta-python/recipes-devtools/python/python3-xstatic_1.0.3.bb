DESCRIPTION = "XStatic base package with minimal support code"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.txt;md5=1418684272f85f400cebf1b1a255c5cd"

PYPI_PACKAGE = "XStatic"

SRC_URI[sha256sum] = "402544cc9e179489441054f09c807804e115ea246907de87c0355fb4f5a31268"

DEPENDS += " \
    python3-pip \
"

inherit pypi setuptools3
