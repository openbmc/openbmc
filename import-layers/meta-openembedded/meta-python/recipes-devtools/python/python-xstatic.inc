DESCRIPTION = "XStatic base package with minimal support code"
HOMEPAGE = "https://pypi.python.org/pypi/XStatic"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.txt;md5=36a1e7eaa71e1bd07b8d2247839f228e"

PYPI_PACKAGE = "XStatic"

SRC_URI[md5sum] = "7064b5ea7fd1ee990e0ae0538301d9bc"
SRC_URI[sha256sum] = "0ec93d7c66ebb9e0d31b664753437dc8634cb66e13310cb47c9eb1e0bc66d726"

DEPENDS += " \
        ${PYTHON_PN}-pip \
        "
