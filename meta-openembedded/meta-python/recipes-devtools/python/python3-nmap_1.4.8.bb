DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "${PYTHON_PN}-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[md5sum] = "e7904b39b64a8a44f275388862659a0d"
SRC_URI[sha256sum] = "8d7da78142bee665289a243f71c5f48407d8ab7e5a02ee672ded05f339044759"

inherit pypi setuptools3

RDEPENDS_${PN} += "nmap ${PYTHON_PN}-requests"
