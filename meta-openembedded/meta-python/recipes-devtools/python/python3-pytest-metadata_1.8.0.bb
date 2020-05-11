DESCRIPTION = "pytest-metadata is a plugin that allowed for accessing pytest metadata"
HOMEPAGE = "https://github.com/pytest-dev/pytest-metadata"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5d425c8f3157dbf212db2ec53d9e5132"

PYPI_PACKAGE = "pytest-metadata"

inherit pypi setuptools3

SRC_URI[md5sum] = "95674c2390b58e0138cabac5101d1908"
SRC_URI[sha256sum] = "2071a59285de40d7541fde1eb9f1ddea1c9db165882df82781367471238b66ba"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} = "${PYTHON_PN}-pytest"
