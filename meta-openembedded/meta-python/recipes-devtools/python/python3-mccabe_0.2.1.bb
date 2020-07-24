DESCRIPTION = "McCabe checker, plugin for flake8"
HOMEPAGE = "https://github.com/dreamhost/cliff"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;md5=aa0383f6aee4f5c20084a97cd13164c4"

SRC_URI[md5sum] = "5a3f3fa6a4bad126c88aaaa7dab682f5"
SRC_URI[sha256sum] = "5a2a170e47de5593a6abfae1e9542bd2c3924ac62bbe4e6ed96c953c0352243a"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-prettytable \
            ${PYTHON_PN}-cmd2 \
            ${PYTHON_PN}-pyparsing \
           "
