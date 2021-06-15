DESCRIPTION = "python-nmap is a python library which helps in using nmap port scanner"
HOMEPAGE = "https://www.nmmapper.com/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "${PYTHON_PN}-wheel-native"

PYPI_PACKAGE = "python3-nmap"

SRC_URI[md5sum] = "3a43dd0f56ade8c76c7c6f994604212d"
SRC_URI[sha256sum] = "b52744e0c9944c567733b8deb60d6363e17233ee40466edfb1b09a5780576f9a"

inherit pypi setuptools3

RDEPENDS_${PN} += "nmap \
	${PYTHON_PN}-requests \
	${PYTHON_PN}-simplejson \
"
