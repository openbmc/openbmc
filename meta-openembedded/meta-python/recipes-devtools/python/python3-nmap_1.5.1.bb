DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "${PYTHON_PN}-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[sha256sum] = "0e6667153a84938bdc0e95a64cd86397b4c46724c422873ea8f1d007d248926a"

inherit pypi setuptools3

RDEPENDS:${PN} += "nmap \
	${PYTHON_PN}-requests \
	${PYTHON_PN}-simplejson \
"
