DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "${PYTHON_PN}-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[sha256sum] = "892b5091cde429fabfb8ba63382b2db8fd795193ba147558d0a7d5534c956255"

inherit pypi setuptools3

RDEPENDS:${PN} += "nmap \
	${PYTHON_PN}-requests \
	${PYTHON_PN}-simplejson \
"
