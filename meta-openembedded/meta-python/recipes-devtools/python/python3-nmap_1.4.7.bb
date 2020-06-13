DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "${PYTHON_PN}-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[md5sum] = "4dce15e7889b1e9bfa8e1e2e0904795c"
SRC_URI[sha256sum] = "127b7ad604a3fd34578a6ad848a603ccf1608c607577eb3bba097a1e2fc8f48a"

inherit pypi setuptools3

RDEPENDS_${PN} += "nmap ${PYTHON_PN}-requests"
