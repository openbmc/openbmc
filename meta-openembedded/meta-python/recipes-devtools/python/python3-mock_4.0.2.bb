DESCRIPTION = "A Python Mocking and Patching Library for Testing"
HOMEPAGE = "https://pypi.python.org/pypi/mock"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=de9dfbf780446b18aab11f00baaf5b7e"

inherit pypi setuptools3

RDEPENDS_${PN} += "${PYTHON_PN}-prettytable \
            ${PYTHON_PN}-cmd2 \
            ${PYTHON_PN}-pyparsing \
            ${PYTHON_PN}-mccabe \
            ${PYTHON_PN}-pep8 \
            ${PYTHON_PN}-pyflakes"

SRC_URI[md5sum] = "6466619b89c2969dc57722ad73f85676"
SRC_URI[sha256sum] = "dd33eb70232b6118298d516bbcecd26704689c386594f0f3c4f13867b2c56f72"
