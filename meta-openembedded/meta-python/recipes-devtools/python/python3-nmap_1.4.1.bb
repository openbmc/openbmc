DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "${PYTHON_PN}-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[md5sum] = "64a382c870e14b53f2f52b7455996321"
SRC_URI[sha256sum] = "9b64c5956789f4cac9e8ea2e0de6763dea1cecde1a20ae50a4b4dc5ab0ab6e42"

inherit pypi setuptools3

RDEPENDS_${PN} += "nmap ${PYTHON_PN}-requests"
