SUMMARY = "Controller Area Network (CAN) interface module for Python"
SECTION = "devel/python"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=38138baa100d7259934590850bc0406e"

SRC_URI[md5sum] = "3310f0aa2a8492d0ff614ecf636ec8a8"
SRC_URI[sha256sum] = "4a5c01dd67feeda35f88e6c12ea14ac8cabd426b9be0cc5f9fd083fe90a9dbfc"

PYPI_PACKAGE="python-can"

RDEPENDS_${PN}_class-target += "\
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-sqlite3 \
"
