SUMMARY = "Controller Area Network (CAN) interface module for Python"
SECTION = "devel/python"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=38138baa100d7259934590850bc0406e"

SRC_URI[md5sum] = "dcdd3f6b924e3f20acb1ccf4598236cf"
SRC_URI[sha256sum] = "b5e93b2ee32bdd597d9d908afe5171c402a04c9678ba47b60f33506738b1375b"

PYPI_PACKAGE="python-can"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-sqlite3 \
    ${PYTHON_PN}-wrapt \
"
