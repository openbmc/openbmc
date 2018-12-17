SUMMARY = "passive checker of Python programs"
HOMEPAGE = "https://github.com/dreamhost/cliff"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=029ce1beb023f2c4ca9f417e4984a865"

SRC_URI[md5sum] = "b385b0e4cd40a522553e97cd2d83573e"
SRC_URI[sha256sum] = "8d616a382f243dbf19b54743f280b80198be0bca3a5396f1d2e1fca6223e8805"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-prettytable \
    ${PYTHON_PN}-cmd2 \
    ${PYTHON_PN}-pyparsing"

BBCLASSEXTEND = "native nativesdk"
