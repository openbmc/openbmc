DESCRIPTION = "Utilities for interacting with PyPI"
HOMEPAGE = "https://twine.readthedocs.io/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a3d1106b253a8d50dd82a4202a045b4c"

SRC_URI[md5sum] = "faf033a3458de37df6cdecceb6da2c2b"
SRC_URI[sha256sum] = "d561a5e511f70275e5a485a6275ff61851c16ffcb3a95a602189161112d9f160"

inherit pypi setuptools3

DEPENDS += "\
    ${PYTHON_PN}-setuptools-scm-native \
"

BBCLASSEXTEND = "native"
