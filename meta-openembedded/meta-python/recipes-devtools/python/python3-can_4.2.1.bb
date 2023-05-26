SUMMARY = "Controller Area Network (CAN) interface module for Python"
SECTION = "devel/python"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "f1ace82545cb4c7edf92a5aa6024bbb8ad23331ee5b0a1f441e782bf680a67ac"

PYPI_PACKAGE="python-can"

inherit pypi setuptools3

RDEPENDS:${PN}:class-target += "\
    ${PYTHON_PN}-aenum \
    ${PYTHON_PN}-ctypes \
    ${PYTHON_PN}-codecs \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-fcntl \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-misc \
    ${PYTHON_PN}-netserver \
    ${PYTHON_PN}-sqlite3 \
    ${PYTHON_PN}-wrapt \
    ${PYTHON_PN}-pkg-resources \
    ${PYTHON_PN}-typing-extensions \
"

BBCLASSEXTEND = "native nativesdk"
