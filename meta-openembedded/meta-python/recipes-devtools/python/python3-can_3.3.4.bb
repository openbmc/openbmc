SUMMARY = "Controller Area Network (CAN) interface module for Python"
SECTION = "devel/python"
LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[md5sum] = "305075968c56bd85130b19d86e9e505d"
SRC_URI[sha256sum] = "2d3c223b7adc4dd46ce258d4a33b7e0dbb6c339e002faa40ee4a69d5fdce9449"

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
"

BBCLASSEXTEND = "native nativesdk"
