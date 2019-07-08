DESCRIPTION = "A python module for handling realtime sensor data from OBD-II vehicle ports"
HOMEPAGE = "https://github.com/brendan-w/python-OBD"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://README.rst;md5=2988a5e913eb105fa08011135c4157d4"

SRC_URI[md5sum] = "93e04f825e13b7ebc97d06b6a6407807"
SRC_URI[sha256sum] = "4b1bac95df2faac571ebf444778e4d736d050d8ed7049023c0fc929a520eba6d"

inherit pypi

RDEPENDS_${PN} = "${PYTHON_PN}-pyserial ${PYTHON_PN}-pint"
