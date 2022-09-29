DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "${PYTHON_PN}-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[sha256sum] = "cd5b0180d4d8cfe96c33e5e7956fa011379af108e2e8291b84e933b6385856e9"

inherit pypi setuptools3

RDEPENDS:${PN} += "nmap \
	${PYTHON_PN}-requests \
	${PYTHON_PN}-simplejson \
"
