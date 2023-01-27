DESCRIPTION = "A Python Mocking and Patching Library for Testing"
HOMEPAGE = "https://pypi.python.org/pypi/mock"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=de9dfbf780446b18aab11f00baaf5b7e"

inherit pypi setuptools3

RDEPENDS:${PN} += "${PYTHON_PN}-prettytable \
            ${PYTHON_PN}-cmd2 \
            ${PYTHON_PN}-pyparsing \
            ${PYTHON_PN}-mccabe \
            ${PYTHON_PN}-pep8 \
            ${PYTHON_PN}-pyflakes"

SRC_URI[sha256sum] = "e3ea505c03babf7977fd21674a69ad328053d414f05e6433c30d8fa14a534a6b"
